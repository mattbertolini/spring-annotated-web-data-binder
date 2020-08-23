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
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class PathParameterMapRequestPropertyResolverTest {
    private PathParameterMapRequestPropertyResolver resolver;

    @BeforeEach
    void setUp() {
        resolver = new PathParameterMapRequestPropertyResolver();
    }

    @Test
    void supportsReturnsTrueOnPresenceOfAnnotation() throws Exception {
        boolean result = resolver.supports(typeDescriptor(Map.class, new StubbingAnnotation(null)), bindingProperty("annotated", TestingBean.class));
        assertThat(result).isTrue();
    }

    @Test
    void supportsReturnsFalseOnMissingAnnotation() throws Exception {
        boolean result = resolver.supports(typeDescriptor(Map.class), bindingProperty("notAnnotated", TestingBean.class));
        assertThat(result).isFalse();
    }

    @Test
    void supportsReturnsFalseWhenAnnotationValueIsPresent() throws Exception {
        boolean result = resolver.supports(typeDescriptor(Map.class, new StubbingAnnotation("name")), bindingProperty("withValue", TestingBean.class));
        assertThat(result).isFalse();
    }

    @Test
    void supportsReturnsFalseWhenTypeIsNotMap() throws Exception {
        boolean result = resolver.supports(typeDescriptor(String.class, new StubbingAnnotation(null)), bindingProperty("notAMap", TestingBean.class));
        assertThat(result).isFalse();
    }

    @SuppressWarnings("unchecked")
    @Test
    void returnsMapPathParameter() {
        Map<String, String> pathVarsMap = new HashMap<>();
        pathVarsMap.put("path_one", "one");
        pathVarsMap.put("path_two", "two");
        pathVarsMap.put("path_three", "three");
        MockServerHttpRequest request = MockServerHttpRequest.get("/irrelevant").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        exchange.getAttributes().put(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, pathVarsMap);

        Mono<Object> objectMono = resolver.resolve(typeDescriptor(Map.class, new StubbingAnnotation(null)), exchange);
        Object actual = objectMono.block();
        assertThat(actual).isInstanceOf(Map.class);
        Map<String, String> map = (Map<String, String>) actual;
        assertThat(map)
            .containsEntry("path_one", "one")
            .containsEntry("path_two", "two")
            .containsEntry("path_three", "three");
    }

    @SuppressWarnings("unchecked")
    @Test
    void returnsEmptyMapWhenNoPathVariables() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/irrelevant").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        Mono<Object> objectMono = resolver.resolve(typeDescriptor(Map.class, new StubbingAnnotation(null)), exchange);
        Object actual = objectMono.block();
        assertThat(actual).isInstanceOf(Map.class);
        Map<String, String> map = (Map<String, String>) actual;
        assertThat(map).isEmpty();
    }

    private TypeDescriptor typeDescriptor(Class<?> clazz, Annotation... annotations) {
        return new TypeDescriptor(ResolvableType.forClass(clazz), null, annotations);
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
        @PathParameter
        private Map<String, String> annotated;

        private Map<String, String> notAnnotated;

        @PathParameter("irrelevant")
        private String withValue;

        @PathParameter
        private String notAMap;

        public Map<String, String> getAnnotated() {
            return annotated;
        }

        public void setAnnotated(Map<String, String> annotated) {
            this.annotated = annotated;
        }

        public Map<String, String> getNotAnnotated() {
            return notAnnotated;
        }

        public void setNotAnnotated(Map<String, String> notAnnotated) {
            this.notAnnotated = notAnnotated;
        }

        public String getWithValue() {
            return withValue;
        }

        public void setWithValue(String withValue) {
            this.withValue = withValue;
        }

        public String getNotAMap() {
            return notAMap;
        }

        public void setNotAMap(String notAMap) {
            this.notAMap = notAMap;
        }
    }
}
