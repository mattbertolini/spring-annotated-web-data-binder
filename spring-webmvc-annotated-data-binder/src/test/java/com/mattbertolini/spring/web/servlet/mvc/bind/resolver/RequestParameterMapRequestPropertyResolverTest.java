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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.ResolvableType;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.request.ServletWebRequest;

import java.lang.annotation.Annotation;
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
    void supportsReturnsTrueOnPresenceOfAnnotation() {
        boolean result = resolver.supports(typeDescriptor(Map.class, annotation(null)));
        assertThat(result).isTrue();
    }

    @Test
    void supportsReturnsFalseOnMissingAnnotation() {
        boolean result = resolver.supports(typeDescriptor(Map.class));
        assertThat(result).isFalse();
    }

    @Test
    void supportsReturnsFalseWhenAnnotationValueIsPresent() {
        boolean result = resolver.supports(typeDescriptor(Map.class, annotation("name")));
        assertThat(result).isFalse();
    }

    @Test
    void supportsReturnsFalseWhenTypeIsNotMap() {
        boolean result = resolver.supports(typeDescriptor(String.class, annotation(null)));
        assertThat(result).isFalse();
    }

    @SuppressWarnings("unchecked")
    @Test
    void returnsMultiValueMap() {
        servletRequest.addParameter("request_param", "one", "two", "three");
        Object actual = resolver.resolve(typeDescriptor(MultiValueMap.class, annotation(null)), request);
        assertThat(actual).isInstanceOf(MultiValueMap.class);
        MultiValueMap<String, String> map = (MultiValueMap<String, String>) actual;
        assertThat(map).containsEntry("request_param", Arrays.asList("one", "two", "three"));
    }

    @SuppressWarnings("unchecked")
    @Test
    void returnsMapWithFirstValue() {
        servletRequest.addParameter("request_param", "one", "two", "three");
        Object actual = resolver.resolve(typeDescriptor(Map.class, annotation(null)), request);
        assertThat(actual).isInstanceOf(Map.class);
        Map<String, String> map = (Map<String, String>) actual;
        assertThat(map).containsEntry("request_param", "one");
    }

    private StubbingRequestParameter annotation(String parameterName) {
        return new StubbingRequestParameter(parameterName);
    }

    private TypeDescriptor typeDescriptor(Class<?> clazz, Annotation... annotations) {
        return new TypeDescriptor(ResolvableType.forClass(clazz), null, annotations);
    }

    @SuppressWarnings("ClassExplicitlyAnnotation")
    private static class StubbingRequestParameter implements RequestParameter {
        private final String name;

        private StubbingRequestParameter(String name) {
            this.name = name == null ? "" : name;
        }

        @Override
        public String value() {
            return name;
        }

        @Override
        public Class<? extends Annotation> annotationType() {
            return RequestParameter.class;
        }
    }
}
