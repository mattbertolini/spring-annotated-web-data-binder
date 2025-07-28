/*
 * Copyright 2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mattbertolini.spring.web.reactive.bind.resolver;

import com.mattbertolini.spring.web.bind.annotation.FormParameter;
import com.mattbertolini.spring.web.bind.introspect.BindingProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.ResolvableType;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.http.codec.ClientCodecConfigurer;
import org.springframework.http.codec.HttpMessageWriter;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.MultipartHttpMessageWriter;
import org.springframework.http.codec.multipart.Part;
import org.springframework.lang.Nullable;
import org.springframework.mock.http.client.reactive.MockClientHttpRequest;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class FormParameterRequestPropertyResolverTest {

    private MultipartHttpMessageWriter writer;

    private FormParameterRequestPropertyResolver resolver;

    @BeforeEach
    void setUp() {
        List<HttpMessageWriter<?>> writers = ClientCodecConfigurer.create().getWriters();
        writer = new MultipartHttpMessageWriter(writers);

        resolver = new FormParameterRequestPropertyResolver();
    }

    @Test
    void supportsReturnsTrueOnPresenceOfAnnotation() throws Exception {
        boolean result = resolver.supports(bindingProperty("annotated"));
        assertThat(result).isTrue();
    }

    @Test
    void supportsReturnsFalseOnMissingAnnotation() throws Exception {
        boolean result = resolver.supports(bindingProperty("notAnnotated"));
        assertThat(result).isFalse();
    }

    @Test
    void supportsReturnsFalseOnMissingAnnotationValue() throws Exception {
        boolean result = resolver.supports(bindingProperty("missingValue"));
        assertThat(result).isFalse();
    }

    @Test
    void throwsExceptionIfResolveCalledWithNoAnnotation() throws Exception {
        // Unlikely to happen as the library always checks the supports method.
        MockServerHttpRequest request = MockServerHttpRequest.post("/irrelevant")
            .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        BindingProperty bindingProperty = bindingProperty("notAnnotated");
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> resolver.resolve(bindingProperty, exchange));
    }

    @Test
    void returnsValueFromHttpRequest() throws Exception {
        List<String> expected = Collections.singletonList("expected value");

        MockServerHttpRequest request = MockServerHttpRequest.post("/irrelevant")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body("testing=expected+value");
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        Mono<Object> actual = resolver.resolve(bindingProperty("annotated"), exchange);
        assertThat(actual.block()).isEqualTo(expected);
    }

    @Test
    void returnsNullWhenNoValueFound() throws Exception {
        MockServerHttpRequest request = MockServerHttpRequest.post("/irrelevant").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        Mono<Object> actual = resolver.resolve(bindingProperty("annotated"), exchange);
        assertThat(actual.block()).isNull();
    }

    @Test
    void returnsMultipleValues() throws Exception {
        List<String> expected = Arrays.asList("one", "two", "three");

        MockServerHttpRequest request = MockServerHttpRequest.post("/irrelevant")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body("multiple_values=one&multiple_values=two&multiple_values=three");
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        Mono<Object> actual = resolver.resolve(bindingProperty("multipleValues"), exchange);
        assertThat(actual.block()).isEqualTo(expected);
    }

    @Test
    void returnsMultipartFilePart() throws Exception {
        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
        multipartBodyBuilder.part("file", "expected")
            .contentType(MediaType.TEXT_PLAIN)
            .filename("mockFile.txt");

        ServerWebExchange exchange = createMultipartExchange(multipartBodyBuilder);
        Mono<Object> actual = resolver.resolve(bindingProperty("multipartValue"), exchange);
        Object obj = actual.block();
        assertThat(obj).isNotNull()
            .isInstanceOf(FilePart.class);
        FilePart filePart = (FilePart) obj;
        assertThat(partContentToString(filePart)).isEqualTo("expected");
    }

    @SuppressWarnings("unchecked")
    @Test
    void returnsMultipleMultipartFileParts() throws Exception {
        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
        multipartBodyBuilder.part("file", "expectedOne")
            .contentType(MediaType.TEXT_PLAIN)
            .filename("mockFileOne.txt");
        multipartBodyBuilder.part("file", "expectedTwo")
            .contentType(MediaType.TEXT_PLAIN)
            .filename("mockFileTwo.txt");

        ServerWebExchange exchange = createMultipartExchange(multipartBodyBuilder);
        Mono<Object> actual = resolver.resolve(bindingProperty("multipartValue"), exchange);
        Object obj = actual.block();
        assertThat(obj).isNotNull()
            .isInstanceOf(List.class);
        List<Part> partsList = (List<Part>) obj;
        for (Part part : partsList) {
            assertThat(partContentToString(part)).startsWith("expected");
        }

    }

    @Test
    void returnsMultipartFilePartAndFormParameter() throws Exception {
        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
        multipartBodyBuilder.part("testing", "simpleValue");
        multipartBodyBuilder.part("file", "filePart")
            .contentType(MediaType.TEXT_PLAIN)
            .filename("mockFile.txt");

        ServerWebExchange exchange = createMultipartExchange(multipartBodyBuilder);

        Mono<Object> simpleValue = resolver.resolve(bindingProperty("annotated"), exchange);
        assertThat(simpleValue.block()).isEqualTo("simpleValue");

        Mono<Object> multipartValue = resolver.resolve(bindingProperty("multipartValue"), exchange);
        Object obj = multipartValue.block();
        assertThat(obj).isNotNull()
            .isInstanceOf(FilePart.class);
        FilePart filePart = (FilePart) obj;
        assertThat(partContentToString(filePart)).isEqualTo("filePart");
    }

    private BindingProperty bindingProperty(String property) throws IntrospectionException {
        return BindingProperty.forPropertyDescriptor(new PropertyDescriptor(property, TestingBean.class));
    }

    private ServerWebExchange createMultipartExchange(MultipartBodyBuilder builder) {
        MockClientHttpRequest clientRequest = new MockClientHttpRequest(HttpMethod.POST, "/irrelevant");
        writer.write(Mono.just(builder.build()), ResolvableType.forClass(MultiValueMap.class),
            MediaType.MULTIPART_FORM_DATA, clientRequest, Collections.emptyMap()).block();

        MediaType contentType = clientRequest.getHeaders().getContentType();
        Flux<DataBuffer> body = clientRequest.getBody();
        Objects.requireNonNull(contentType, "Content type header missing");
        MockServerHttpRequest serverRequest = MockServerHttpRequest.post("/irrelevant")
            .contentType(contentType)
            .body(body);

        return MockServerWebExchange.from(serverRequest);
    }

    private String partContentToString(Part part) {
        DataBuffer data = DataBufferUtils.join(part.content()).block();
        Objects.requireNonNull(data, "Data buffer is null");
        return data.toString(StandardCharsets.UTF_8);
    }

    @SuppressWarnings("unused")
    private static class TestingBean {
        @Nullable
        @FormParameter("testing")
        private String annotated;

        @Nullable
        private String notAnnotated;

        @Nullable
        @FormParameter("multiple_values")
        private List<String> multipleValues;

        @Nullable
        @FormParameter
        private String missingValue;

        @Nullable
        @FormParameter("file")
        private Part multipartValue;

        @Nullable
        public String getAnnotated() {
            return annotated;
        }

        public void setAnnotated(String annotated) {
            this.annotated = annotated;
        }

        @Nullable
        public String getNotAnnotated() {
            return notAnnotated;
        }

        public void setNotAnnotated(String notAnnotated) {
            this.notAnnotated = notAnnotated;
        }

        @Nullable
        public List<String> getMultipleValues() {
            return multipleValues;
        }

        public void setMultipleValues(List<String> multipleValues) {
            this.multipleValues = multipleValues;
        }

        @Nullable
        public String getMissingValue() {
            return missingValue;
        }

        public void setMissingValue(String missingValue) {
            this.missingValue = missingValue;
        }

        @Nullable
        public Part getMultipartValue() {
            return multipartValue;
        }

        public void setMultipartValue(Part multipartValue) {
            this.multipartValue = multipartValue;
        }
    }
}
