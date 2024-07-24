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

import com.mattbertolini.spring.test.web.bind.records.CookieParameterRecord;
import com.mattbertolini.spring.web.bind.annotation.BeanParameter;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
public class CookieParameterController {
    @Nullable
    @GetMapping(value = "/annotatedField", produces = MediaType.TEXT_PLAIN_VALUE)
    public String annotatedField(@BeanParameter CookieParameterBean cookieParameterBean) {
        return cookieParameterBean.getAnnotatedField();
    }

    @Nullable
    @GetMapping(value = "/annotatedSetter", produces = MediaType.TEXT_PLAIN_VALUE)
    public String annotatedSetter(@BeanParameter CookieParameterBean cookieParameterBean) {
        return cookieParameterBean.getAnnotatedSetter();
    }

    @Nullable
    @GetMapping(value = "/annotatedGetter", produces = MediaType.TEXT_PLAIN_VALUE)
    public String annotatedGetter(@BeanParameter CookieParameterBean cookieParameterBean) {
        return cookieParameterBean.getAnnotatedGetter();
    }

    @GetMapping(value = "/bindingResult", produces = MediaType.TEXT_PLAIN_VALUE)
    public String bindingResult(@BeanParameter CookieParameterBean cookieParameterBean, BindingResult bindingResult) {
        return Integer.toString(bindingResult.getErrorCount());
    }

    @Nullable
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

    @Nullable
    @GetMapping(value = "/nested", produces = MediaType.TEXT_PLAIN_VALUE)
    public String nestedBean(@BeanParameter CookieParameterBean cookieParameterBean) {
        return Objects.requireNonNull(cookieParameterBean.getNestedBean()).getCookieValue();
    }

    @GetMapping(value = "/record", produces = MediaType.TEXT_PLAIN_VALUE)
    public String javaRecord(@BeanParameter CookieParameterRecord cookieParameterRecord) {
        return cookieParameterRecord.annotated();
    }
}
