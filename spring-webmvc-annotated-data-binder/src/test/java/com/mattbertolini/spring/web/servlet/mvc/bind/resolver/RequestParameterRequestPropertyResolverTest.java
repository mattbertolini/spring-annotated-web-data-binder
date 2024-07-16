/*
 * Copyright 2019-2021 the original author or authors.
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

import com.mattbertolini.spring.web.bind.PropertyResolutionException;
import com.mattbertolini.spring.web.bind.annotation.RequestParameter;
import com.mattbertolini.spring.web.bind.introspect.BindingProperty;
import jakarta.servlet.http.Part;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockMultipartHttpServletRequest;
import org.springframework.mock.web.MockPart;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.multipart.MultipartFile;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
        BindingProperty bindingProperty = bindingProperty("notAnnotated");
        assertThatExceptionOfType(IllegalStateException.class)
            .isThrownBy(() -> resolver.resolve(bindingProperty, request));
    }

    @Test
    void returnsValueFromHttpRequest() throws Exception {
        String[] expected = {"expected value"};
        String parameterName = "testing";
        servletRequest.addParameter(parameterName, expected);
        Object actual = resolver.resolve(bindingProperty("annotated"), request);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void returnsNullWhenNoValueFound() throws Exception {
        Object actual = resolver.resolve(bindingProperty("annotated"), request);
        assertThat(actual).isNull();
    }

    @Test
    void returnsMultipleValues() throws Exception {
        String[] expected = {"one", "two", "three"};
        String parameterName = "multiple_values";
        servletRequest.addParameter(parameterName, "one");
        servletRequest.addParameter(parameterName, "two");
        servletRequest.addParameter(parameterName, "three");
        Object actual = resolver.resolve(bindingProperty("multipleValues"), request);
        assertThat(actual).isEqualTo(expected);
    }
    
    @Test
    void returnsMultipartFile() throws Exception {
        MockMultipartHttpServletRequest multipartRequest = new MockMultipartHttpServletRequest();
        MockMultipartFile multipartFile = new MockMultipartFile(
            "multipart_file",
            "testfile.txt",
            MediaType.TEXT_PLAIN_VALUE,
            "testing".getBytes(StandardCharsets.UTF_8)
        );
        multipartRequest.addFile(multipartFile);

        Object actual = resolver.resolve(bindingProperty("multipartFile"), new ServletWebRequest(multipartRequest));
        assertThat(actual).isNotNull();
        assertThat((MultipartFile) actual).isEqualTo(multipartFile);
    }

    @Test
    void returnsPart() throws Exception {
        MockMultipartHttpServletRequest multipartRequest = new MockMultipartHttpServletRequest();
        MockPart part = new MockPart(
            "part",
            "testfile.txt",
            "testing".getBytes(StandardCharsets.UTF_8)
        );
        multipartRequest.addPart(part);

        Object actual = resolver.resolve(bindingProperty("part"), new ServletWebRequest(multipartRequest));
        assertThat(actual).isNotNull();
        assertThat((Part) actual).isEqualTo(part);
    }

    @Test
    void returnsMultipartFileAndRequestParameter() throws Exception {
        MockMultipartFile multipartFile = new MockMultipartFile(
            "multipart_file",
            "testfile.txt",
            MediaType.TEXT_PLAIN_VALUE,
            "testing".getBytes(StandardCharsets.UTF_8)
        );

        MockMultipartHttpServletRequest multipartRequest = new MockMultipartHttpServletRequest();
        multipartRequest.addParameter("testing", "value");
        multipartRequest.addFile(multipartFile);
        ServletWebRequest request = new ServletWebRequest(multipartRequest);

        Object formValue = resolver.resolve(bindingProperty("annotated"), request);
        assertThat(formValue).isEqualTo(new String[]{"value"});

        Object actual = resolver.resolve(bindingProperty("multipartFile"), request);
        assertThat(actual).isNotNull();
        assertThat((MultipartFile) actual).isEqualTo(multipartFile);
    }

    @Test
    void throwsExceptionReadingMultipartRequest() throws Exception {
        MockMultipartHttpServletRequest multipartRequest = new ExceptionThrowingMockMultipartHttpServletRequest();
        ServletWebRequest request = new ServletWebRequest(multipartRequest);

        BindingProperty bindingProperty = bindingProperty("multipartFile");
        assertThatThrownBy(() -> resolver.resolve(bindingProperty, request))
            .isInstanceOf(PropertyResolutionException.class);
    }

    private BindingProperty bindingProperty(String property) throws IntrospectionException {
        return BindingProperty.forPropertyDescriptor(new PropertyDescriptor(property, TestingBean.class));
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

        @RequestParameter("multipart_file")
        private MultipartFile multipartFile;

        @RequestParameter("part")
        private Part part;

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

        public MultipartFile getMultipartFile() {
            return multipartFile;
        }

        public void setMultipartFile(MultipartFile multipartFile) {
            this.multipartFile = multipartFile;
        }

        public Part getPart() {
            return part;
        }

        public void setPart(Part part) {
            this.part = part;
        }
    }
}
