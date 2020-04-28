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

package com.mattbertolini.spring.test.web.bind;

import com.mattbertolini.spring.web.bind.annotation.BeanParameter;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Map;

@RestController
public class FormParameterController {
    @PostMapping(value = "/annotatedField", produces = MediaType.TEXT_PLAIN_VALUE)
    public String annotatedField(@BeanParameter FormParameterBean formParameterBean) {
        return formParameterBean.getAnnotatedField();
    }

    @PostMapping(value = "/annotatedSetter", produces = MediaType.TEXT_PLAIN_VALUE)
    public String annotatedSetter(@BeanParameter FormParameterBean formParameterBean) {
        return formParameterBean.getAnnotatedSetter();
    }

    @PostMapping(value = "/annotatedGetter", produces = MediaType.TEXT_PLAIN_VALUE)
    public String handleRequest(@BeanParameter FormParameterBean formParameterBean) {
        return formParameterBean.getAnnotatedGetter();
    }

    @PostMapping(value = "/simpleMap", produces = MediaType.TEXT_PLAIN_VALUE)
    public String simpleMap(@BeanParameter FormParameterBean formParameterBean) {
        Map<String, String> simpleMap = formParameterBean.getSimpleMap();
        return simpleMap.get("simple-map");
    }

    @PostMapping(value = "/multiValueMap", produces = MediaType.TEXT_PLAIN_VALUE)
    public String multiValueMap(@BeanParameter FormParameterBean formParameterBean) {
        MultiValueMap<String, String> multiValueMap = formParameterBean.getMultiValueMap();
        return multiValueMap.getFirst("multi-value-map");
    }

    @PostMapping(value = "/bindingResult", produces = MediaType.TEXT_PLAIN_VALUE)
    public String bindingResult(@BeanParameter FormParameterBean formParameterBean, BindingResult bindingResult) {
        return Integer.toString(bindingResult.getErrorCount());
    }

    @PostMapping(value = "/validated", produces = MediaType.TEXT_PLAIN_VALUE)
    public String validated(@Valid @BeanParameter FormParameterBean formParameterBean) {
        return formParameterBean.getValidated();
    }

    @PostMapping(value = "/validatedWithBindingResult", produces = MediaType.TEXT_PLAIN_VALUE)
    public String validatedWithBindingResult(@Valid @BeanParameter FormParameterBean formParameterBean, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "notValid";
        }
        return "valid";
    }

    @PostMapping(value = "/nested", produces = MediaType.TEXT_PLAIN_VALUE)
    public String nestedBeanParameter(@BeanParameter FormParameterBean formParameterBean) {
        return formParameterBean.getNestedBean().getFormData();
    }
}
