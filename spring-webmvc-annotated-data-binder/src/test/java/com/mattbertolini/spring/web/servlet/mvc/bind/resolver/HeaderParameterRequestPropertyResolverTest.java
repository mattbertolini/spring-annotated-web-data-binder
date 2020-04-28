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

import com.mattbertolini.spring.web.bind.annotation.HeaderParameter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.ResolvableType;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;

import java.lang.annotation.Annotation;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class HeaderParameterRequestPropertyResolverTest {
    private HeaderParameterRequestPropertyResolver resolver;
    private ServletWebRequest request;
    private MockHttpServletRequest servletRequest;

    @BeforeEach
    void setUp() {
        resolver = new HeaderParameterRequestPropertyResolver();
        servletRequest = new MockHttpServletRequest();
        request = new ServletWebRequest(servletRequest);
    }

    @Test
    void supportsReturnsTrueOnPresenceOfAnnotation() {
        boolean result = resolver.supports(typeDescriptor(String.class, new StubbingAnnotation("name")));
        assertThat(result).isTrue();
    }

    @Test
    void supportsReturnsFalseOnMissingAnnotation() {
        boolean result = resolver.supports(typeDescriptor(String.class));
        assertThat(result).isFalse();
    }

    @Test
    void supportsReturnsFalseOnMissingAnnotationValue() {
        boolean result = resolver.supports(typeDescriptor(String.class, new StubbingAnnotation(null)));
        assertThat(result).isFalse();
    }

    @Test
    void throwsExceptionIfResolveCalledWithNoAnnotation() {
        // Unlikely to happen as the library always checks the supports method.
        assertThatExceptionOfType(IllegalStateException.class)
            .isThrownBy(() -> resolver.resolve(typeDescriptor(String.class), request));
    }

    @Test
    void returnsValueFromHeader() {
        String[] expected = {"headerValue"};
        String headerName = "X-HeaderName";
        servletRequest.addHeader(headerName, expected);
        Object actual = resolver.resolve(typeDescriptor(String.class, new StubbingAnnotation(headerName)), request);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void returnsNullWhenNoValueFound() {
        Object actual = resolver.resolve(typeDescriptor(Integer.class, new StubbingAnnotation("X-NotFound")), request);
        assertThat(actual).isNull();
    }

    @Test
    void returnsMultipleValues() {
        String[] expected = {"one", "two", "three"};
        String headerName = "X-Multiple";
        servletRequest.addHeader(headerName, "one");
        servletRequest.addHeader(headerName, "two");
        servletRequest.addHeader(headerName, "three");
        Object actual = resolver.resolve(typeDescriptor(List.class, new StubbingAnnotation(headerName)), request);
        assertThat(actual).isEqualTo(expected);
    }

    private TypeDescriptor typeDescriptor(Class<?> clazz, Annotation... annotations) {
        return new TypeDescriptor(ResolvableType.forClass(clazz), null, annotations);
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
}
