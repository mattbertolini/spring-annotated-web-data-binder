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

import com.mattbertolini.spring.web.bind.annotation.FormParameter;
import com.mattbertolini.spring.web.bind.introspect.BindingProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.ResolvableType;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.http.MediaType;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Mono;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class FormParameterMapRequestPropertyResolverTest {
    private FormParameterMapRequestPropertyResolver resolver;

    @BeforeEach
    void setUp() {
        resolver = new FormParameterMapRequestPropertyResolver();
    }

    @Test
    void supportsReturnsTrueOnPresenceOfAnnotation() throws Exception {
        boolean result = resolver.supports(typeDescriptor(Map.class, annotation(null)), bindingProperty("annotated", TestingBean.class));
        assertThat(result).isTrue();
    }

    @Test
    void supportsReturnsFalseOnMissingAnnotation() throws Exception {
        boolean result = resolver.supports(typeDescriptor(Map.class), bindingProperty("notAnnotated", TestingBean.class));
        assertThat(result).isFalse();
    }

    @Test
    void supportsReturnsFalseWhenAnnotationValueIsPresent() throws Exception {
        boolean result = resolver.supports(typeDescriptor(Map.class, annotation("name")), bindingProperty("withName", TestingBean.class));
        assertThat(result).isFalse();
    }

    @Test
    void supportsReturnsFalseWhenTypeIsNotMap() throws Exception {
        boolean result = resolver.supports(typeDescriptor(String.class, annotation(null)), bindingProperty("notAMap", TestingBean.class));
        assertThat(result).isFalse();
    }

    @SuppressWarnings("unchecked")
    @Test
    void returnsMultiValueMap() {
        MockServerHttpRequest request = MockServerHttpRequest.post("/irrelevant")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body("form_param_one=one&form_param_two=two&form_param_three=three");
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        
        Mono<Object> objectMono = resolver.resolve(typeDescriptor(MultiValueMap.class, annotation(null)), exchange);
        Object actual = objectMono.block();
        assertThat(actual).isInstanceOf(MultiValueMap.class);
        MultiValueMap<String, String> map = (MultiValueMap<String, String>) actual;
        assertThat(map)
            .containsEntry("form_param_one", Collections.singletonList("one"))
            .containsEntry("form_param_two", Collections.singletonList("two"))
            .containsEntry("form_param_three", Collections.singletonList("three"));
    }

    @SuppressWarnings("unchecked")
    @Test
    void returnsMapWithFirstValue() {
        MockServerHttpRequest request = MockServerHttpRequest.post("/irrelevant")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body("form_param=one&form_param=two&form_param=three");
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        
        Mono<Object> objectMono = resolver.resolve(typeDescriptor(Map.class, annotation(null)), exchange);
        Object actual = objectMono.block();
        assertThat(actual).isInstanceOf(Map.class);
        Map<String, String> map = (Map<String, String>) actual;
        assertThat(map).containsEntry("form_param", "one");
    }

    private StubbingFormParameter annotation(String parameterName) {
        return new StubbingFormParameter(parameterName);
    }

    private TypeDescriptor typeDescriptor(Class<?> clazz, Annotation... annotations) {
        return new TypeDescriptor(ResolvableType.forClass(clazz), null, annotations);
    }

    private BindingProperty bindingProperty(String property, Class<?> clazz) throws IntrospectionException {
        return BindingProperty.forPropertyDescriptor(new PropertyDescriptor(property, clazz));
    }

    @SuppressWarnings("ClassExplicitlyAnnotation")
    private static class StubbingFormParameter implements FormParameter {
        private final String name;

        private StubbingFormParameter(String name) {
            this.name = name == null ? "" : name;
        }

        @Override
        public String value() {
            return name;
        }

        @Override
        public Class<? extends Annotation> annotationType() {
            return FormParameter.class;
        }
    }

    @SuppressWarnings("unused")
    private static class TestingBean {
        @FormParameter
        private Map<String, String> annotated;

        private Map<String, String> notAnnotated;

        @FormParameter("name")
        private String withName;

        @FormParameter
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

        public String getWithName() {
            return withName;
        }

        public void setWithName(String withName) {
            this.withName = withName;
        }

        public String getNotAMap() {
            return notAMap;
        }

        public void setNotAMap(String notAMap) {
            this.notAMap = notAMap;
        }
    }
}
