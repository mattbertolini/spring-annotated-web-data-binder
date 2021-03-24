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
import com.mattbertolini.spring.web.bind.annotation.RequestParameter;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.Part;
import javax.validation.constraints.NotEmpty;
import java.util.Map;

public class RequestParameterBean {
    @RequestParameter("annotated_field")
    private String annotatedField;

    private String annotatedSetter;

    private String annotatedGetter;

    @RequestParameter
    private Map<String, String> simpleMap;

    @RequestParameter
    private MultiValueMap<String, String> multiValueMap;

    @NotEmpty
    @RequestParameter("validated")
    private String validated;

    @BeanParameter
    private NestedBean nestedBean;

    public String getAnnotatedField() {
        return annotatedField;
    }

    public void setAnnotatedField(String annotatedField) {
        this.annotatedField = annotatedField;
    }

    public String getAnnotatedSetter() {
        return annotatedSetter;
    }

    @RequestParameter("annotated_setter")
    public void setAnnotatedSetter(String annotatedSetter) {
        this.annotatedSetter = annotatedSetter;
    }

    @RequestParameter("annotated_getter")
    public String getAnnotatedGetter() {
        return annotatedGetter;
    }

    public void setAnnotatedGetter(String annotatedGetter) {
        this.annotatedGetter = annotatedGetter;
    }

    public Map<String, String> getSimpleMap() {
        return simpleMap;
    }

    public void setSimpleMap(Map<String, String> simpleMap) {
        this.simpleMap = simpleMap;
    }

    public MultiValueMap<String, String> getMultiValueMap() {
        return multiValueMap;
    }

    public void setMultiValueMap(MultiValueMap<String, String> multiValueMap) {
        this.multiValueMap = multiValueMap;
    }

    public String getValidated() {
        return validated;
    }

    public void setValidated(String validated) {
        this.validated = validated;
    }

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
        @RequestParameter("file")
        private MultipartFile multipartFile;

        @RequestParameter("part")
        private Part part;

        @RequestParameter
        private Map<String, MultipartFile> multipartFileMap;

        @RequestParameter
        private MultiValueMap<String, MultipartFile> multiValueMultipartMap;

        @RequestParameter
        private Map<String, Part> partMap;

        @RequestParameter
        private MultiValueMap<String, Part> multiValuePartMap;

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

        public Map<String, MultipartFile> getMultipartFileMap() {
            return multipartFileMap;
        }

        public void setMultipartFileMap(Map<String, MultipartFile> multipartFileMap) {
            this.multipartFileMap = multipartFileMap;
        }

        public MultiValueMap<String, MultipartFile> getMultiValueMultipartMap() {
            return multiValueMultipartMap;
        }

        public void setMultiValueMultipartMap(MultiValueMap<String, MultipartFile> multiValueMultipartMap) {
            this.multiValueMultipartMap = multiValueMultipartMap;
        }

        public Map<String, Part> getPartMap() {
            return partMap;
        }

        public void setPartMap(Map<String, Part> partMap) {
            this.partMap = partMap;
        }

        public MultiValueMap<String, Part> getMultiValuePartMap() {
            return multiValuePartMap;
        }

        public void setMultiValuePartMap(MultiValueMap<String, Part> multiValuePartMap) {
            this.multiValuePartMap = multiValuePartMap;
        }
    }
}
