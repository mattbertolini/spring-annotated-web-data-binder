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

import com.mattbertolini.spring.web.bind.annotation.HeaderParameter;
import com.mattbertolini.spring.web.bind.introspect.BindingProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.ResolvableType;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import reactor.core.publisher.Mono;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class HeaderParameterRequestPropertyResolverTest {

    private HeaderParameterRequestPropertyResolver resolver;

    @BeforeEach
    void setUp() {
        resolver = new HeaderParameterRequestPropertyResolver();
    }

    @Test
    void supportsReturnsTrueOnPresenceOfAnnotation() throws Exception {
        boolean result = resolver.supports(bindingProperty("annotated", TestingBean.class));
        assertThat(result).isTrue();
    }

    @Test
    void supportsReturnsFalseOnMissingAnnotation() throws Exception {
        boolean result = resolver.supports(bindingProperty("notAnnotated", TestingBean.class));
        assertThat(result).isFalse();
    }

    @Test
    void supportsReturnsFalseOnMissingAnnotationValue() throws Exception {
        boolean result = resolver.supports(bindingProperty("missingValue", TestingBean.class));
        assertThat(result).isFalse();
    }

    @Test
    void throwsExceptionIfResolveCalledWithNoAnnotation() {
        // Unlikely to happen as the library always checks the supports method.
        MockServerHttpRequest request = MockServerHttpRequest.get("/irrelevant")
            .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        assertThatExceptionOfType(IllegalStateException.class)
            .isThrownBy(() -> resolver.resolve(typeDescriptor(String.class), exchange));
    }

    @Test
    void returnsValueFromHeader() {
        List<String> expected = Collections.singletonList("headerValue");
        String headerName = "X-HeaderName";

        MockServerHttpRequest request = MockServerHttpRequest
            .get("/irrelevant")
            .header(headerName, "headerValue")
            .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        Mono<Object> actual = resolver.resolve(typeDescriptor(String.class, new StubbingAnnotation(headerName)), exchange);
        assertThat(actual.block()).isEqualTo(expected);
    }

    @Test
    void returnsNullWhenNoValueFound() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/irrelevant").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        Mono<Object> actual = resolver.resolve(typeDescriptor(Integer.class, new StubbingAnnotation("X-NotFound")), exchange);
        assertThat(actual.block()).isNull();
    }

    @Test
    void returnsMultipleValues() {
        List<String> expected = Arrays.asList("one", "two", "three");
        String headerName = "X-Multiple";

        MockServerHttpRequest request = MockServerHttpRequest.get("/irrelevant")
            .header(headerName, "one", "two", "three")
            .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        
        Mono<Object> actual = resolver.resolve(typeDescriptor(List.class, new StubbingAnnotation(headerName)), exchange);
        assertThat(actual.block()).isEqualTo(expected);
    }

    private TypeDescriptor typeDescriptor(Class<?> clazz, Annotation... annotations) {
        return new TypeDescriptor(ResolvableType.forClass(clazz), null, annotations);
    }

    private BindingProperty bindingProperty(String property, Class<?> clazz) throws IntrospectionException {
        return BindingProperty.forPropertyDescriptor(new PropertyDescriptor(property, clazz));
    }

    @SuppressWarnings("ClassExplicitlyAnnotation")
    private static class StubbingAnnotation implements HeaderParameter {

        private final String value;

        private StubbingAnnotation(String value) {
            this.value = value == null ? "" : value;
        }

        @Override
        public String value() {
            return value;
        }

        @Override
        public Class<? extends Annotation> annotationType() {
            return HeaderParameter.class;
        }
    }

    @SuppressWarnings("unused")
    private static class TestingBean {
        @HeaderParameter("irrelevant")
        private String annotated;

        private String notAnnotated;

        @HeaderParameter
        private String missingValue;

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

        public String getMissingValue() {
            return missingValue;
        }

        public void setMissingValue(String missingValue) {
            this.missingValue = missingValue;
        }
    }
}
