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
package com.mattbertolini.spring.test.web.bind;

import com.mattbertolini.spring.web.bind.annotation.BeanParameter;
import com.mattbertolini.spring.web.bind.annotation.PathParameter;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.lang.Nullable;

import java.util.Map;

public class PathParameterBean {
    @Nullable
    @PathParameter("annotated_field")
    private String annotatedField;

    @Nullable
    private String annotatedSetter;

    @Nullable
    private String annotatedGetter;

    @Nullable
    @PathParameter
    private Map<String, String> simpleMap;

    @Nullable
    @NotEmpty
    @PathParameter("validated")
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

    @PathParameter("annotated_setter")
    public void setAnnotatedSetter(String annotatedSetter) {
        this.annotatedSetter = annotatedSetter;
    }

    @Nullable
    @PathParameter("annotated_getter")
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
}
