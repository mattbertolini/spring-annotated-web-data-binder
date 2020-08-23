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

package com.mattbertolini.spring.web.servlet.mvc.bind.resolver;

import com.mattbertolini.spring.web.bind.annotation.RequestParameter;
import com.mattbertolini.spring.web.bind.introspect.BindingProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.request.ServletWebRequest;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class RequestParameterMapRequestPropertyResolverTest {
    private RequestParameterMapRequestPropertyResolver resolver;
    private ServletWebRequest request;
    private MockHttpServletRequest servletRequest;

    @BeforeEach
    void setUp() {
        resolver = new RequestParameterMapRequestPropertyResolver();
        servletRequest = new MockHttpServletRequest();
        request = new ServletWebRequest(servletRequest);
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
        boolean result = resolver.supports(bindingProperty("valuePresent"));
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
        servletRequest.addParameter("request_param", "one", "two", "three");
        Object actual = resolver.resolve(bindingProperty("multivalue"), request);
        assertThat(actual).isInstanceOf(MultiValueMap.class);
        MultiValueMap<String, String> map = (MultiValueMap<String, String>) actual;
        assertThat(map).containsEntry("request_param", Arrays.asList("one", "two", "three"));
    }

    @SuppressWarnings("unchecked")
    @Test
    void returnsMapWithFirstValue() throws Exception {
        servletRequest.addParameter("request_param", "one", "two", "three");
        Object actual = resolver.resolve(bindingProperty("annotated"), request);
        assertThat(actual).isInstanceOf(Map.class);
        Map<String, String> map = (Map<String, String>) actual;
        assertThat(map).containsEntry("request_param", "one");
    }

    private BindingProperty bindingProperty(String property) throws IntrospectionException {
        return BindingProperty.forPropertyDescriptor(new PropertyDescriptor(property, TestingBean.class));
    }

    @SuppressWarnings("unused")
    private static class TestingBean {
        @RequestParameter
        private Map<String, String> annotated;

        private Map<String, String> notAnnotated;

        @RequestParameter
        private MultiValueMap<String, String> multivalue;

        @RequestParameter
        private String notAMap;

        @RequestParameter("irrelevant")
        private Map<String, String> valuePresent;

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

        public MultiValueMap<String, String> getMultivalue() {
            return multivalue;
        }

        public void setMultivalue(MultiValueMap<String, String> multivalue) {
            this.multivalue = multivalue;
        }

        public String getNotAMap() {
            return notAMap;
        }

        public void setNotAMap(String notAMap) {
            this.notAMap = notAMap;
        }

        public Map<String, String> getValuePresent() {
            return valuePresent;
        }

        public void setValuePresent(Map<String, String> valuePresent) {
            this.valuePresent = valuePresent;
        }
    }
}
