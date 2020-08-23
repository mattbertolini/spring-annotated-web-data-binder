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
import org.springframework.core.ResolvableType;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class RequestParameterRequestPropertyResolverTest {
    private RequestParameterRequestPropertyResolver resolver;
    private ServletWebRequest request;
    private MockHttpServletRequest servletRequest;

    @BeforeEach
    void setUp() {
        resolver = new RequestParameterRequestPropertyResolver();
        servletRequest = new MockHttpServletRequest();
        request = new ServletWebRequest(servletRequest);
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
        assertThatExceptionOfType(IllegalStateException.class)
            .isThrownBy(() -> resolver.resolve(typeDescriptor(String.class), bindingProperty("notAnnotated", TestingBean.class), request));
    }

    @Test
    void returnsValueFromHttpRequest() throws Exception {
        String[] expected = {"expected value"};
        String parameterName = "testing";
        servletRequest.addParameter(parameterName, expected);
        Object actual = resolver.resolve(typeDescriptor(String.class, annotation(parameterName)), bindingProperty("annotated", TestingBean.class), request);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void returnsNullWhenNoValueFound() throws Exception {
        Object actual = resolver.resolve(typeDescriptor(Integer.class, annotation("not_found")), bindingProperty("annotated", TestingBean.class), request);
        assertThat(actual).isNull();
    }

    @Test
    void returnsMultipleValues() throws Exception {
        String[] expected = {"one", "two", "three"};
        String parameterName = "multiple_values";
        servletRequest.addParameter(parameterName, "one");
        servletRequest.addParameter(parameterName, "two");
        servletRequest.addParameter(parameterName, "three");
        Object actual = resolver.resolve(typeDescriptor(List.class, annotation(parameterName)), bindingProperty("multipleValues", TestingBean.class), request);
        assertThat(actual).isEqualTo(expected);
    }

    private StubbingRequestParameter annotation(String parameterName) {
        return new StubbingRequestParameter(parameterName);
    }

    private TypeDescriptor typeDescriptor(Class<?> clazz, Annotation... annotations) {
        return new TypeDescriptor(ResolvableType.forClass(clazz), null, annotations);
    }

    private BindingProperty bindingProperty(String property, Class<?> clazz) throws IntrospectionException {
        return BindingProperty.forPropertyDescriptor(new PropertyDescriptor(property, clazz));
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

    @SuppressWarnings("unused")
    private static class TestingBean {
        @RequestParameter("testing")
        private String annotated;

        private String notAnnotated;

        @RequestParameter
        private String missingValue;

        @RequestParameter("multiple_values")
        private List<String> multipleValues;

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

        public List<String> getMultipleValues() {
            return multipleValues;
        }

        public void setMultipleValues(List<String> multipleValues) {
            this.multipleValues = multipleValues;
        }
    }
}
