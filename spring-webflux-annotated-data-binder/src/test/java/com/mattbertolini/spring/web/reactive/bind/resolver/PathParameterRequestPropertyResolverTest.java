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

import com.mattbertolini.spring.web.bind.annotation.PathParameter;
import com.mattbertolini.spring.web.bind.introspect.BindingProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.ResolvableType;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.reactive.HandlerMapping;
import reactor.core.publisher.Mono;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class PathParameterRequestPropertyResolverTest {
    private PathParameterRequestPropertyResolver resolver;

    @BeforeEach
    void setUp() {
        resolver = new PathParameterRequestPropertyResolver();
    }

    @Test
    void supportsReturnsTrueOnPresenceOfAnnotation() throws Exception {
        boolean result = resolver.supports(typeDescriptor(new StubbingAnnotation("name")), bindingProperty("annotated", TestingBean.class));
        assertThat(result).isTrue();
    }

    @Test
    void supportsReturnsFalseOnMissingAnnotation() throws Exception {
        boolean result = resolver.supports(typeDescriptor(), bindingProperty("notAnnotated", TestingBean.class));
        assertThat(result).isFalse();
    }

    @Test
    void supportsReturnsFalseOnMissingAnnotationValue() throws Exception {
        boolean result = resolver.supports(typeDescriptor(new StubbingAnnotation(null)), bindingProperty("missingValue", TestingBean.class));
        assertThat(result).isFalse();
    }

    @Test
    void returnsValuePathParameter() {
        String expected = "pathValue";
        String parameterName = "pathParamName";
        MockServerWebExchange exchange = makePathParamMap(parameterName, expected);
        Mono<Object> actual = resolver.resolve(typeDescriptor(new StubbingAnnotation(parameterName)), exchange);
        assertThat(actual.block()).isEqualTo(expected);
    }

    @Test
    void throwsExceptionIfResolveCalledWithNoAnnotation() {
        // Unlikely to happen as the library always checks the supports method.
        MockServerHttpRequest request = MockServerHttpRequest.get("/irrelevant")
            .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        assertThatExceptionOfType(IllegalStateException.class)
            .isThrownBy(() -> resolver.resolve(typeDescriptor(), exchange));
    }

    @Test
    void returnsNullIfNoPathVariableFound() {
        String parameterName = "pathParamName";
        MockServerWebExchange exchange = emptyPathParamMap();
        Mono<Object> actual = resolver.resolve(typeDescriptor(new StubbingAnnotation(parameterName)), exchange);
        assertThat(actual.block()).isNull();
    }

    @Test
    void returnsNullIfNoPathVariablesMapExistsOnRequest() {
        String parameterName = "pathParamName";
        MockServerHttpRequest request = MockServerHttpRequest.get("/irrelevant").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        Mono<Object> actual = resolver.resolve(typeDescriptor(new StubbingAnnotation(parameterName)), exchange);
        assertThat(actual.block()).isNull();
    }

    private MockServerWebExchange makePathParamMap(String name, String value) {
        Map<String, String> pathVarsMap = new HashMap<>();
        pathVarsMap.put(name, value);

        MockServerHttpRequest request = MockServerHttpRequest.get("/irrelevant").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        exchange.getAttributes().put(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, pathVarsMap);
        
        return exchange;
    }

    private MockServerWebExchange emptyPathParamMap() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/irrelevant").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        exchange.getAttributes().put(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, Collections.emptyMap());
        return exchange;
    }

    private TypeDescriptor typeDescriptor(Annotation... annotations) {
        return new TypeDescriptor(ResolvableType.forClass(String.class), null, annotations);
    }

    private BindingProperty bindingProperty(String property, Class<?> clazz) throws IntrospectionException {
        return BindingProperty.forPropertyDescriptor(new PropertyDescriptor(property, clazz));
    }

    @SuppressWarnings("ClassExplicitlyAnnotation")
    private static class StubbingAnnotation implements PathParameter {
        private final String name;

        private StubbingAnnotation(String name) {
            this.name = name == null ? "" : name;
        }

        @Override
        public String value() {
            return name;
        }

        @Override
        public Class<? extends Annotation> annotationType() {
            return PathParameter.class;
        }
    }

    @SuppressWarnings("unused")
    private static class TestingBean {
        @PathParameter("irrelevant")
        private String annotated;

        private String notAnnotated;

        @PathParameter
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
