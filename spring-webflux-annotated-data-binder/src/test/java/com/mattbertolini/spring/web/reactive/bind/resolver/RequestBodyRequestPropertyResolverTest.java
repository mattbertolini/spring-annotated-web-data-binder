/*
 * Copyright 2019-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mattbertolini.spring.web.reactive.bind.resolver;

import com.mattbertolini.spring.web.bind.annotation.RequestBody;
import com.mattbertolini.spring.web.bind.introspect.BindingProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.core.codec.StringDecoder;
import org.springframework.http.MediaType;
import org.springframework.http.codec.DecoderHttpMessageReader;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import reactor.core.publisher.Mono;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class RequestBodyRequestPropertyResolverTest {
    private RequestBodyRequestPropertyResolver resolver;

    @BeforeEach
    void setUp() {
        List<HttpMessageReader<?>> readers = new ArrayList<>();
        readers.add(new DecoderHttpMessageReader<>(StringDecoder.allMimeTypes()));

        resolver = new RequestBodyRequestPropertyResolver(readers, new ReactiveAdapterRegistry());
    }

    @Test
    void supportsParameterMethodAlwaysReturnsFalse() throws Exception {
        MethodParameter methodParameter = bindingProperty("annotated").getMethodParameter();
        assertThat(resolver.supportsParameter(methodParameter)).isFalse();
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
    void throwsExceptionIfResolveCalledWithNoAnnotation() throws Exception {
        // Unlikely to happen as the library always checks the supports method.
        MockServerHttpRequest request = MockServerHttpRequest.post("/irrelevant")
            .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        BindingProperty bindingProperty = bindingProperty("notAnnotated");
        assertThatExceptionOfType(IllegalStateException.class)
            .isThrownBy(() -> resolver.resolve(bindingProperty, exchange));
    }

    @Test
    void returnsValueFromHttpRequest() throws Exception {
        String expected = "{\"expected value\"}";

        MockServerHttpRequest request = MockServerHttpRequest.post("/irrelevant")
            .contentType(MediaType.APPLICATION_JSON)
            .body(expected);
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

    private BindingProperty bindingProperty(String property) throws IntrospectionException {
        return BindingProperty.forPropertyDescriptor(new PropertyDescriptor(property, TestingBean.class));
    }

    @SuppressWarnings("unused")
    private static class TestingBean {
        @RequestBody
        private String annotated;

        private String notAnnotated;


        public String getAnnotated() {
            return annotated;
        }

        public void setAnnotated(String annotated) {
            this.annotated = annotated;
        }

        public String getNotAnnotated() {
            return notAnnotated;
        }

        public void setNotAnnotated(String notAnnotated) {
            this.notAnnotated = notAnnotated;
        }
    }
}
