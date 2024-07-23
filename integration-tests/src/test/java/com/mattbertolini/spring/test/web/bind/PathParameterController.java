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

import com.mattbertolini.spring.test.web.bind.records.PathParameterRecord;
import com.mattbertolini.spring.web.bind.annotation.BeanParameter;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Objects;

@SuppressWarnings("MVCPathVariableInspection")
@RestController
public class PathParameterController {
    @Nullable
    @GetMapping(value = "/annotatedField/{annotated_field}", produces = MediaType.TEXT_PLAIN_VALUE)
    public String annotatedField(@BeanParameter PathParameterBean pathParameterBean) {
        return pathParameterBean.getAnnotatedField();
    }

    @Nullable
    @GetMapping(value = "/annotatedSetter/{annotated_setter}", produces = MediaType.TEXT_PLAIN_VALUE)
    public String annotatedSetter(@BeanParameter PathParameterBean pathParameterBean) {
        return pathParameterBean.getAnnotatedSetter();
    }

    @Nullable
    @GetMapping(value = "/annotatedGetter/{annotated_getter}", produces = MediaType.TEXT_PLAIN_VALUE)
    public String annotatedGetter(@BeanParameter PathParameterBean pathParameterBean) {
        return pathParameterBean.getAnnotatedGetter();
    }

    @Nullable
    @GetMapping(value = "/simpleMap/{simple_map}")
    public String simpleMap(@BeanParameter PathParameterBean pathParameterBean) {
        Map<String, String> simpleMap = pathParameterBean.getSimpleMap();
        return Objects.requireNonNull(simpleMap).get("simple_map");
    }

    @GetMapping(value = "/bindingResult/{validated}", produces = MediaType.TEXT_PLAIN_VALUE)
    public String bindingResult(@BeanParameter PathParameterBean pathParameterBean, BindingResult bindingResult) {
        return Integer.toString(bindingResult.getErrorCount());
    }

    @Nullable
    @GetMapping(value = {"/validated", "/validated/{validated}"}, produces = MediaType.TEXT_PLAIN_VALUE)
    public String validated(@Valid @BeanParameter PathParameterBean pathParameterBean) {
        return pathParameterBean.getValidated();
    }

    @GetMapping(value = {"/validatedWithBindingResult", "/validatedWithBindingResult/{validated}"}, produces = MediaType.TEXT_PLAIN_VALUE)
    public String validatedWithBindingResult(@Valid @BeanParameter PathParameterBean pathParameterBean, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "notValid";
        }
        return "valid";
    }

    @Nullable
    @GetMapping(value = "/nested/{nested_path_param}", produces = MediaType.TEXT_PLAIN_VALUE)
    public String nestedBean(@BeanParameter PathParameterBean pathParameterBean) {
        return Objects.requireNonNull(pathParameterBean.getNestedBean()).getPathVariable();
    }

    @GetMapping(value = "/record/{annotated_field}", produces = MediaType.TEXT_PLAIN_VALUE)
    public String javaRecord(@BeanParameter PathParameterRecord pathParameterRecord) {
        return pathParameterRecord.annotated();
    }
}
