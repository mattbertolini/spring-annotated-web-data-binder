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
import org.springframework.lang.Nullable;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import reactor.core.publisher.Mono;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
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
        MockServerHttpRequest request = MockServerHttpRequest.get("/irrelevant")
            .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        BindingProperty bindingProperty = bindingProperty("notAnnotated");
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> resolver.resolve(bindingProperty, exchange));
    }

    @Test
    void returnsValueFromHeader() throws Exception {
        List<String> expected = Collections.singletonList("headerValue");
        String headerName = "X-HeaderName";

        MockServerHttpRequest request = MockServerHttpRequest
            .get("/irrelevant")
            .header(headerName, "headerValue")
            .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        Mono<Object> actual = resolver.resolve(bindingProperty("annotated"), exchange);
        assertThat(actual.block()).isEqualTo(expected);
    }

    @Test
    void returnsNullWhenNoValueFound() throws Exception {
        MockServerHttpRequest request = MockServerHttpRequest.get("/irrelevant").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        Mono<Object> actual = resolver.resolve(bindingProperty("annotated"), exchange);
        assertThat(actual.block()).isNull();
    }

    @Test
    void returnsMultipleValues() throws Exception {
        List<String> expected = Arrays.asList("one", "two", "three");
        String headerName = "X-Multiple";

        MockServerHttpRequest request = MockServerHttpRequest.get("/irrelevant")
            .header(headerName, "one", "two", "three")
            .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        
        Mono<Object> actual = resolver.resolve(bindingProperty("multipleValues"), exchange);
        assertThat(actual.block()).isEqualTo(expected);
    }

    private BindingProperty bindingProperty(String property) throws IntrospectionException {
        return BindingProperty.forPropertyDescriptor(new PropertyDescriptor(property, TestingBean.class));
    }

    @SuppressWarnings("unused")
    private static class TestingBean {
        @Nullable
        @HeaderParameter("X-HeaderName")
        private String annotated;

        @Nullable
        private String notAnnotated;

        @Nullable
        @HeaderParameter
        private String missingValue;

        @Nullable
        @HeaderParameter("X-Multiple")
        private List<String> multipleValues;

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
        public String getMissingValue() {
            return missingValue;
        }

        public void setMissingValue(String missingValue) {
            this.missingValue = missingValue;
        }

        @Nullable
        public List<String> getMultipleValues() {
            return multipleValues;
        }

        public void setMultipleValues(List<String> multipleValues) {
            this.multipleValues = multipleValues;
        }
    }
}
