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

package com.mattbertolini.spring.test.web.bind;

import com.mattbertolini.spring.web.bind.annotation.BeanParameter;
import com.mattbertolini.spring.web.bind.annotation.FormParameter;
import jakarta.servlet.http.Part;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.lang.Nullable;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public class FormParameterBean {
    @Nullable
    @FormParameter("annotated_field")
    private String annotatedField;

    @Nullable
    private String annotatedSetter;

    @Nullable
    private String annotatedGetter;

    @Nullable
    @FormParameter
    private Map<String, String> simpleMap;

    @Nullable
    @FormParameter
    private MultiValueMap<String, String> multiValueMap;

    @Nullable
    @NotEmpty
    @FormParameter("validated")
    private String validated;

    @Nullable
    @BeanParameter
    private NestedBean nestedBean;

    @Nullable
    public String getAnnotatedField() {
        return annotatedField;
    }

    public void setAnnotatedField(String annotatedField) {
        this.annotatedField = annotatedField;
    }

    @Nullable
    public String getAnnotatedSetter() {
        return annotatedSetter;
    }

    @FormParameter("annotated_setter")
    public void setAnnotatedSetter(String annotatedSetter) {
        this.annotatedSetter = annotatedSetter;
    }

    @Nullable
    @FormParameter("annotated_getter")
    public String getAnnotatedGetter() {
        return annotatedGetter;
    }

    public void setAnnotatedGetter(String annotatedGetter) {
        this.annotatedGetter = annotatedGetter;
    }

    @Nullable
    public Map<String, String> getSimpleMap() {
        return simpleMap;
    }

    public void setSimpleMap(Map<String, String> simpleMap) {
        this.simpleMap = simpleMap;
    }

    @Nullable
    public MultiValueMap<String, String> getMultiValueMap() {
        return multiValueMap;
    }

    public void setMultiValueMap(MultiValueMap<String, String> multiValueMap) {
        this.multiValueMap = multiValueMap;
    }

    @Nullable
    public String getValidated() {
        return validated;
    }

    public void setValidated(String validated) {
        this.validated = validated;
    }

    @Nullable
    public NestedBean getNestedBean() {
        return nestedBean;
    }

    public void setNestedBean(NestedBean nestedBean) {
        this.nestedBean = nestedBean;
    }

    /**
     * Test bean for multipart binding in a Servlet/WebMVC application
     */
    static class ServletMultipartBean {
        @Nullable
        @FormParameter("file")
        private MultipartFile multipartFile;

        @Nullable
        @FormParameter("part")
        private Part part;

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
        public MultipartFile getMultipartFile() {
            return multipartFile;
        }

        public void setMultipartFile(MultipartFile multipartFile) {
            this.multipartFile = multipartFile;
        }

        @Nullable
        public Part getPart() {
            return part;
        }

        public void setPart(Part part) {
            this.part = part;
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

    /**
     * Test bean for multipart binding in a WebFlux application
     */
    static class WebfluxMultipartBean {
        @Nullable
        @FormParameter("file")
        private FilePart filePart;

        @Nullable
        public FilePart getFilePart() {
            return filePart;
        }

        public void setFilePart(FilePart filePart) {
            this.filePart = filePart;
        }
    }
}
