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

import com.mattbertolini.spring.web.bind.PropertyResolutionException;
import com.mattbertolini.spring.web.bind.annotation.FormParameter;
import com.mattbertolini.spring.web.bind.introspect.BindingProperty;
import jakarta.servlet.http.Part;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockMultipartHttpServletRequest;
import org.springframework.mock.web.MockPart;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.multipart.MultipartFile;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FormParameterMapRequestPropertyResolverTest {
    private FormParameterMapRequestPropertyResolver resolver;
    private ServletWebRequest request;
    private MockHttpServletRequest servletRequest;

    @BeforeEach
    void setUp() {
        resolver = new FormParameterMapRequestPropertyResolver();
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
        servletRequest.addParameter("form_param", "one", "two", "three");
        Object actual = resolver.resolve(bindingProperty("multivalue"), request);
        assertThat(actual).isInstanceOf(MultiValueMap.class);
        MultiValueMap<String, String> map = (MultiValueMap<String, String>) actual;
        assertThat(map).containsEntry("form_param", Arrays.asList("one", "two", "three"));
    }

    @SuppressWarnings("unchecked")
    @Test
    void returnsMapWithFirstValue() throws Exception {
        servletRequest.addParameter("form_param", "one", "two", "three");
        Object actual = resolver.resolve(bindingProperty("annotated"), request);
        assertThat(actual).isInstanceOf(Map.class);
        Map<String, String> map = (Map<String, String>) actual;
        assertThat(map).containsEntry("form_param", "one");
    }

    @SuppressWarnings("unchecked")
    @Test
    void returnsMultipartFileMap() throws Exception {
        MockMultipartFile fileOne = new MockMultipartFile("file_one",
            "fileOne.txt",
            MediaType.TEXT_PLAIN_VALUE,
            "fileOne".getBytes(StandardCharsets.UTF_8));
        MockMultipartFile fileTwo = new MockMultipartFile("file_two",
            "fileTwo.txt",
            MediaType.TEXT_PLAIN_VALUE,
            "fileTwo".getBytes(StandardCharsets.UTF_8));
        MockMultipartHttpServletRequest multipartRequest = new MockMultipartHttpServletRequest();
        multipartRequest.addFile(fileOne);
        multipartRequest.addFile(fileTwo);
        ServletWebRequest servletWebRequest = new ServletWebRequest(multipartRequest);

        Object actual = resolver.resolve(bindingProperty("multipartFileMap"), servletWebRequest);
        assertThat(actual).isInstanceOf(Map.class);
        Map<String, MultipartFile> map = (Map<String, MultipartFile>) actual;
        assertThat(map).containsEntry("file_one", fileOne)
            .containsEntry("file_two", fileTwo);
    }

    @SuppressWarnings("unchecked")
    @Test
    void returnsMultipartFileMultiValueMap() throws Exception {
        MockMultipartFile fileOne = new MockMultipartFile("file",
            "fileOne.txt",
            MediaType.TEXT_PLAIN_VALUE,
            "fileOne".getBytes(StandardCharsets.UTF_8));
        MockMultipartFile fileTwo = new MockMultipartFile("file",
            "fileTwo.txt",
            MediaType.TEXT_PLAIN_VALUE,
            "fileTwo".getBytes(StandardCharsets.UTF_8));
        MockMultipartHttpServletRequest multipartRequest = new MockMultipartHttpServletRequest();
        multipartRequest.addFile(fileOne);
        multipartRequest.addFile(fileTwo);
        ServletWebRequest servletWebRequest = new ServletWebRequest(multipartRequest);

        Object actual = resolver.resolve(bindingProperty("multiValueMultipartMap"), servletWebRequest);
        assertThat(actual).isInstanceOf(MultiValueMap.class);
        MultiValueMap<String, MultipartFile> map = (MultiValueMap<String, MultipartFile>) actual;
        assertThat(map).containsEntry("file", Arrays.asList(fileOne, fileTwo));
    }

    @SuppressWarnings("unchecked")
    @Test
    void returnsFirstMultipartFileInMap() throws Exception {
        MockMultipartFile fileOne = new MockMultipartFile("file",
            "fileOne.txt",
            MediaType.TEXT_PLAIN_VALUE,
            "fileOne".getBytes(StandardCharsets.UTF_8));
        MockMultipartFile fileTwo = new MockMultipartFile("file",
            "fileTwo.txt",
            MediaType.TEXT_PLAIN_VALUE,
            "fileTwo".getBytes(StandardCharsets.UTF_8));
        MockMultipartHttpServletRequest multipartRequest = new MockMultipartHttpServletRequest();
        multipartRequest.addFile(fileOne);
        multipartRequest.addFile(fileTwo);
        ServletWebRequest servletWebRequest = new ServletWebRequest(multipartRequest);

        Object actual = resolver.resolve(bindingProperty("multipartFileMap"), servletWebRequest);
        assertThat(actual).isInstanceOf(Map.class);
        Map<String, MultipartFile> map = (Map<String, MultipartFile>) actual;
        assertThat(map).containsEntry("file", fileOne);
    }

    @SuppressWarnings("unchecked")
    @Test
    void returnsPartMap() throws Exception {
        MockPart fileOne = new MockPart("file_one",
            "fileOne.txt",
            "fileOne".getBytes(StandardCharsets.UTF_8));
        MockPart fileTwo = new MockPart("file_two",
            "fileTwo.txt",
            "fileTwo".getBytes(StandardCharsets.UTF_8));
        MockMultipartHttpServletRequest multipartRequest = new MockMultipartHttpServletRequest();
        multipartRequest.addPart(fileOne);
        multipartRequest.addPart(fileTwo);
        ServletWebRequest servletWebRequest = new ServletWebRequest(multipartRequest);

        Object actual = resolver.resolve(bindingProperty("partMap"), servletWebRequest);
        assertThat(actual).isInstanceOf(Map.class);
        Map<String, Part> map = (Map<String, Part>) actual;
        assertThat(map).containsEntry("file_one", fileOne)
            .containsEntry("file_two", fileTwo);
    }

    @SuppressWarnings("unchecked")
    @Test
    void returnsPartMultiValueMap() throws Exception {
        MockPart fileOne = new MockPart("file",
            "fileOne.txt",
            "fileOne".getBytes(StandardCharsets.UTF_8));
        MockPart fileTwo = new MockPart("file",
            "fileTwo.txt",
            "fileTwo".getBytes(StandardCharsets.UTF_8));
        MockMultipartHttpServletRequest multipartRequest = new MockMultipartHttpServletRequest();
        multipartRequest.addPart(fileOne);
        multipartRequest.addPart(fileTwo);
        ServletWebRequest servletWebRequest = new ServletWebRequest(multipartRequest);

        Object actual = resolver.resolve(bindingProperty("multiValuePartMap"), servletWebRequest);
        assertThat(actual).isInstanceOf(MultiValueMap.class);
        MultiValueMap<String, Part> map = (MultiValueMap<String, Part>) actual;
        assertThat(map).containsEntry("file", Arrays.asList(fileOne, fileTwo));
    }

    @SuppressWarnings("unchecked")
    @Test
    void returnsFirstPartInMap() throws Exception {
        MockPart fileOne = new MockPart("file",
            "fileOne.txt",
            "fileOne".getBytes(StandardCharsets.UTF_8));
        MockPart fileTwo = new MockPart("file",
            "fileTwo.txt",
            "fileTwo".getBytes(StandardCharsets.UTF_8));
        MockMultipartHttpServletRequest multipartRequest = new MockMultipartHttpServletRequest();
        multipartRequest.addPart(fileOne);
        multipartRequest.addPart(fileTwo);
        ServletWebRequest servletWebRequest = new ServletWebRequest(multipartRequest);

        Object actual = resolver.resolve(bindingProperty("partMap"), servletWebRequest);
        assertThat(actual).isInstanceOf(Map.class);
        Map<String, Part> map = (Map<String, Part>) actual;
        assertThat(map).containsEntry("file", fileOne);
    }

    @SuppressWarnings("unchecked")
    @Test
    void returnsEmptyMultiValueMultipartFileMap() throws Exception {
        MockMultipartHttpServletRequest multipartRequest = new MockMultipartHttpServletRequest();
        ServletWebRequest servletWebRequest = new ServletWebRequest(multipartRequest);

        Object actual = resolver.resolve(bindingProperty("multiValueMultipartMap"), servletWebRequest);
        assertThat(actual).isInstanceOf(MultiValueMap.class);
        MultiValueMap<String, MultipartFile> map = (MultiValueMap<String, MultipartFile>) actual;
        assertThat(map).isEmpty();
    }

    @SuppressWarnings("unchecked")
    @Test
    void returnsEmptyMultipartFileMap() throws Exception {
        MockMultipartHttpServletRequest multipartRequest = new MockMultipartHttpServletRequest();
        ServletWebRequest servletWebRequest = new ServletWebRequest(multipartRequest);

        Object actual = resolver.resolve(bindingProperty("multipartFileMap"), servletWebRequest);
        assertThat(actual).isInstanceOf(Map.class);
        Map<String, MultipartFile> map = (Map<String, MultipartFile>) actual;
        assertThat(map).isEmpty();
    }

    @SuppressWarnings("unchecked")
    @Test
    void returnsEmptyPartMap() throws Exception {
        MockMultipartHttpServletRequest multipartRequest = new MockMultipartHttpServletRequest();
        ServletWebRequest servletWebRequest = new ServletWebRequest(multipartRequest);

        Object actual = resolver.resolve(bindingProperty("partMap"), servletWebRequest);
        assertThat(actual).isInstanceOf(Map.class);
        Map<String, Part> map = (Map<String, Part>) actual;
        assertThat(map).isEmpty();
    }

    @SuppressWarnings("unchecked")
    @Test
    void returnsEmptyPartMultiValueMap() throws Exception {
        MockMultipartHttpServletRequest multipartRequest = new MockMultipartHttpServletRequest();
        ServletWebRequest servletWebRequest = new ServletWebRequest(multipartRequest);

        Object actual = resolver.resolve(bindingProperty("multiValuePartMap"), servletWebRequest);
        assertThat(actual).isInstanceOf(MultiValueMap.class);
        MultiValueMap<String, Part> map = (MultiValueMap<String, Part>) actual;
        assertThat(map).isEmpty();
    }

    @Test
    void throwsExceptionReadingMultipartRequestForMap() throws Exception {
        MockMultipartHttpServletRequest multipartRequest = new ExceptionThrowingMockMultipartHttpServletRequest();
        ServletWebRequest servletWebRequest = new ServletWebRequest(multipartRequest);

        BindingProperty bindingProperty = bindingProperty("partMap");
        assertThatThrownBy(() -> resolver.resolve(bindingProperty, servletWebRequest))
            .isInstanceOf(PropertyResolutionException.class);
    }

    @Test
    void throwsExceptionReadingMultipartRequestForMultiValueMap() throws Exception {
        MockMultipartHttpServletRequest multipartRequest = new ExceptionThrowingMockMultipartHttpServletRequest();
        ServletWebRequest servletWebRequest = new ServletWebRequest(multipartRequest);

        BindingProperty bindingProperty = bindingProperty("multiValuePartMap");
        assertThatThrownBy(() -> resolver.resolve(bindingProperty, servletWebRequest))
            .isInstanceOf(PropertyResolutionException.class);
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
        @FormParameter
        private Map<String, MultipartFile> multipartFileMap;

        @Nullable
        @FormParameter
        private MultiValueMap<String, MultipartFile> multiValueMultipartMap;

        @Nullable
        @FormParameter
        private Map<String, Part> partMap;

        @Nullable
        @FormParameter
        private MultiValueMap<String, Part> multiValuePartMap;

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

        @Nullable
        public Map<String, MultipartFile> getMultipartFileMap() {
            return multipartFileMap;
        }

        public void setMultipartFileMap(Map<String, MultipartFile> multipartFileMap) {
            this.multipartFileMap = multipartFileMap;
        }

        @Nullable
        public MultiValueMap<String, MultipartFile> getMultiValueMultipartMap() {
            return multiValueMultipartMap;
        }

        public void setMultiValueMultipartMap(MultiValueMap<String, MultipartFile> multiValueMultipartMap) {
            this.multiValueMultipartMap = multiValueMultipartMap;
        }

        @Nullable
        public Map<String, Part> getPartMap() {
            return partMap;
        }

        public void setPartMap(Map<String, Part> partMap) {
            this.partMap = partMap;
        }

        @Nullable
        public MultiValueMap<String, Part> getMultiValuePartMap() {
            return multiValuePartMap;
        }

        public void setMultiValuePartMap(MultiValueMap<String, Part> multiValuePartMap) {
            this.multiValuePartMap = multiValuePartMap;
        }
    }
}
