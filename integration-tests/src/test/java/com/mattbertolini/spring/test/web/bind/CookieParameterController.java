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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class CookieParameterController {
    @GetMapping(value = "/annotatedField", produces = MediaType.TEXT_PLAIN_VALUE)
    public String annotatedField(@BeanParameter CookieParameterBean cookieParameterBean) {
        return cookieParameterBean.getAnnotatedField();
    }

    @GetMapping(value = "/annotatedSetter", produces = MediaType.TEXT_PLAIN_VALUE)
    public String annotatedSetter(@BeanParameter CookieParameterBean cookieParameterBean) {
        return cookieParameterBean.getAnnotatedSetter();
    }

    @GetMapping(value = "/annotatedGetter", produces = MediaType.TEXT_PLAIN_VALUE)
    public String annotatedGetter(@BeanParameter CookieParameterBean cookieParameterBean) {
        return cookieParameterBean.getAnnotatedGetter();
    }

    @GetMapping(value = "/bindingResult", produces = MediaType.TEXT_PLAIN_VALUE)
    public String bindingResult(@BeanParameter CookieParameterBean cookieParameterBean, BindingResult bindingResult) {
        return Integer.toString(bindingResult.getErrorCount());
    }

    @GetMapping(value = "/validated", produces = MediaType.TEXT_PLAIN_VALUE)
    public String validated(@Valid @BeanParameter CookieParameterBean cookieParameterBean) {
        return cookieParameterBean.getValidated();
    }

    @GetMapping(value = "/validatedWithBindingResult", produces = MediaType.TEXT_PLAIN_VALUE)
    public String validatedWithBindingResult(@Valid @BeanParameter CookieParameterBean cookieParameterBean, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "notValid";
        }
        return "valid";
    }

    @GetMapping(value = "/nested", produces = MediaType.TEXT_PLAIN_VALUE)
    public String nestedBean(@BeanParameter CookieParameterBean cookieParameterBean) {
        return cookieParameterBean.getNestedBean().getCookieValue();
    }
}
