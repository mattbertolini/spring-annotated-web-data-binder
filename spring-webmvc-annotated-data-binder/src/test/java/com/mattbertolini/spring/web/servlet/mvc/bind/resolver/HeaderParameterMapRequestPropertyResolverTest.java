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
package com.mattbertolini.spring.web.servlet.mvc.bind.resolver;

import com.mattbertolini.spring.web.bind.annotation.HeaderParameter;
import com.mattbertolini.spring.web.bind.introspect.BindingProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.Nullable;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class HeaderParameterMapRequestPropertyResolverTest {
    private HeaderParameterMapRequestPropertyResolver resolver;
    private ServletWebRequest request;
    private MockHttpServletRequest servletRequest;

    @BeforeEach
    void setUp() {
        resolver = new HeaderParameterMapRequestPropertyResolver();
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
    void supportsReturnsFalseWhenAnnotationValueIsPresent() throws Exception{
        boolean result = resolver.supports(bindingProperty("withValue"));
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
        servletRequest.addHeader("x-header-one", "one");
        servletRequest.addHeader("x-header-two", "two");
        servletRequest.addHeader("x-header-three", "three");
        Object actual = resolver.resolve(bindingProperty("multivalue"), request);
        assertThat(actual).isInstanceOf(MultiValueMap.class);
        MultiValueMap<String, String> map = (MultiValueMap<String, String>) actual;
        assertThat(map)
            .containsEntry("x-header-one", Collections.singletonList("one"))
            .containsEntry("x-header-two", Collections.singletonList("two"))
            .containsEntry("x-header-three", Collections.singletonList("three"));
    }

    @Test
    void returnsHttpHeadersObject() throws Exception {
        servletRequest.addHeader("x-header-one", "one");
        servletRequest.addHeader("x-header-two", "two");
        servletRequest.addHeader("x-header-three", "three");
        Object actual = resolver.resolve(bindingProperty("httpHeaders"), request);
        assertThat(actual).isInstanceOf(HttpHeaders.class);
        HttpHeaders headers = (HttpHeaders) actual;
        assertThat(headers)
            .containsEntry("x-header-one", Collections.singletonList("one"))
            .containsEntry("x-header-two", Collections.singletonList("two"))
            .containsEntry("x-header-three", Collections.singletonList("three"));
    }

    @SuppressWarnings("unchecked")
    @Test
    void returnsMapWithFirstValue() throws Exception {
        servletRequest.addHeader("x-header", "one");
        servletRequest.addHeader("x-header", "two");
        servletRequest.addHeader("x-header", "three");
        Object actual = resolver.resolve(bindingProperty("annotated"), request);
        assertThat(actual).isInstanceOf(Map.class);
        Map<String, String> map = (Map<String, String>) actual;
        assertThat(map).containsEntry("x-header", "one");
    }

    @SuppressWarnings("unchecked")
    @Test
    void returnsEmptyMapWhenHeaderValuesReturnsNull() throws Exception {
        NativeWebRequest mock = mock(NativeWebRequest.class);
        when(mock.getHeaderNames()).thenReturn(Collections.singletonList("x-header").iterator());
        when(mock.getHeaderValues("x-header")).thenReturn(null);
        Object actual = resolver.resolve(bindingProperty("multivalue"), mock);
        assertThat(actual).isInstanceOf(MultiValueMap.class);
        MultiValueMap<String, String> map = (MultiValueMap<String, String>) actual;
        assertThat(map).isEmpty();
    }

    private BindingProperty bindingProperty(String property) throws IntrospectionException {
        return BindingProperty.forPropertyDescriptor(new PropertyDescriptor(property, TestingBean.class));
    }

    @SuppressWarnings("unused")
    private static class TestingBean {
        @Nullable
        @HeaderParameter
        private Map<String, String> annotated;

        @Nullable
        private Map<String, String> notAnnotated;

        @Nullable
        @HeaderParameter
        private MultiValueMap<String, String> multivalue;

        @Nullable
        @HeaderParameter
        private HttpHeaders httpHeaders;

        @Nullable
        @HeaderParameter("irrelevant")
        private String withValue;

        @Nullable
        @HeaderParameter
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
        public HttpHeaders getHttpHeaders() {
            return httpHeaders;
        }

        public void setHttpHeaders(HttpHeaders httpHeaders) {
            this.httpHeaders = httpHeaders;
        }

        @Nullable
        public String getWithValue() {
            return withValue;
        }

        public void setWithValue(String withValue) {
            this.withValue = withValue;
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
