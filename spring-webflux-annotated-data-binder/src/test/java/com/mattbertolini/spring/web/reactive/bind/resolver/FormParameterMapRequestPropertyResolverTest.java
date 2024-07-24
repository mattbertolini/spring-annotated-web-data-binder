/*
 * Copyright 2024 the original author or authors.
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
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Mono;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
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
        boolean result = resolver.supports(bindingProperty("annotated"));
        assertThat(result).isTrue();
    }

    @Test
    void supportsReturnsFalseOnMissingAnnotation() throws Exception {
        boolean result = resolver.supports(bindingProperty("notAnnotated"));
        assertThat(result).isFalse();
    }

    @Test
    void supportsReturnsFalseWhenAnnotationValueIsPresent() throws Exception {
        boolean result = resolver.supports(bindingProperty("withName"));
        assertThat(result).isFalse();
    }

    @Test
    void supportsReturnsFalseWhenTypeIsNotMap() throws Exception {
        boolean result = resolver.supports(bindingProperty("notAMap"));
        assertThat(result).isFalse();
    }

    @SuppressWarnings("unchecked")
    @Test
    void returnsMultiValueMap() throws Exception {
        MockServerHttpRequest request = MockServerHttpRequest.post("/irrelevant")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body("form_param_one=one&form_param_two=two&form_param_three=three");
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        
        Mono<Object> objectMono = resolver.resolve(bindingProperty("multivalue"), exchange);
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
    void returnsMapWithFirstValue() throws Exception {
        MockServerHttpRequest request = MockServerHttpRequest.post("/irrelevant")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body("form_param=one&form_param=two&form_param=three");
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        
        Mono<Object> objectMono = resolver.resolve(bindingProperty("annotated"), exchange);
        Object actual = objectMono.block();
        assertThat(actual).isInstanceOf(Map.class);
        Map<String, String> map = (Map<String, String>) actual;
        assertThat(map).containsEntry("form_param", "one");
    }

    private BindingProperty bindingProperty(String property) throws IntrospectionException {
        return BindingProperty.forPropertyDescriptor(new PropertyDescriptor(property, TestingBean.class));
    }

    @SuppressWarnings("unused")
    private static class TestingBean {
        @Nullable
        @FormParameter
        private Map<String, String> annotated;

        @Nullable
        private Map<String, String> notAnnotated;

        @Nullable
        @FormParameter
        private MultiValueMap<String, String> multivalue;

        @Nullable
        @FormParameter("name")
        private String withName;

        @Nullable
        @FormParameter
        private String notAMap;

        @Nullable
        public Map<String, String> getAnnotated() {
            return annotated;
        }

        public void setAnnotated(Map<String, String> annotated) {
            this.annotated = annotated;
        }

        @Nullable
        public Map<String, String> getNotAnnotated() {
            return notAnnotated;
        }

        public void setNotAnnotated(Map<String, String> notAnnotated) {
            this.notAnnotated = notAnnotated;
        }

        @Nullable
        public MultiValueMap<String, String> getMultivalue() {
            return multivalue;
        }

        public void setMultivalue(MultiValueMap<String, String> multivalue) {
            this.multivalue = multivalue;
        }

        @Nullable
        public String getWithName() {
            return withName;
        }

        public void setWithName(String withName) {
            this.withName = withName;
        }

        @Nullable
        public String getNotAMap() {
            return notAMap;
        }

        public void setNotAMap(String notAMap) {
            this.notAMap = notAMap;
        }
    }
}
