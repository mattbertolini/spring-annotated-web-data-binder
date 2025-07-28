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
package com.mattbertolini.spring.test.web.bind;

import com.mattbertolini.spring.test.web.bind.records.HeaderParameterRecord;
import com.mattbertolini.spring.web.bind.annotation.BeanParameter;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Objects;

@RestController
public class HeaderParameterController {
    @Nullable
    @GetMapping(value = "/annotatedField", produces = MediaType.TEXT_PLAIN_VALUE)
    public String annotatedField(@BeanParameter HeaderParameterBean headerParameterBean) {
        return headerParameterBean.getAnnotatedField();
    }

    @Nullable
    @GetMapping(value = "/annotatedSetter", produces = MediaType.TEXT_PLAIN_VALUE)
    public String annotatedSetter(@BeanParameter HeaderParameterBean headerParameterBean) {
        return headerParameterBean.getAnnotatedSetter();
    }

    @Nullable
    @GetMapping(value = "/annotatedGetter", produces = MediaType.TEXT_PLAIN_VALUE)
    public String annotatedGetter(@BeanParameter HeaderParameterBean headerParameterBean) {
        return headerParameterBean.getAnnotatedGetter();
    }

    @Nullable
    @GetMapping(value = "/simpleMap", produces = MediaType.TEXT_PLAIN_VALUE)
    public String simpleMap(@BeanParameter HeaderParameterBean headerParameterBean) {
        Map<String, String> simpleMap = headerParameterBean.getSimpleMap();
        return Objects.requireNonNull(simpleMap).get("x-simple-map");
    }

    @GetMapping(value = "/multiValueMap", produces = MediaType.TEXT_PLAIN_VALUE)
    public String multiValueMap(@BeanParameter HeaderParameterBean headerParameterBean) {
        MultiValueMap<String, String> multiValueMap = headerParameterBean.getMultiValueMap();
        return Objects.requireNonNull(multiValueMap).getFirst("x-multivalue-map");
    }

    @GetMapping(value = "/httpHeaders", produces = MediaType.TEXT_PLAIN_VALUE)
    public String httpHeaders(@BeanParameter HeaderParameterBean headerParameterBean) {
        HttpHeaders httpHeaders = headerParameterBean.getHttpHeaders();
        return Objects.requireNonNull(httpHeaders).getFirst("x-http-headers");
    }

    @GetMapping(value = "/bindingResult", produces = MediaType.TEXT_PLAIN_VALUE)
    public String bindingResult(@BeanParameter HeaderParameterBean headerParameterBean, BindingResult bindingResult) {
        return Integer.toString(bindingResult.getErrorCount());
    }

    @Nullable
    @GetMapping(value = "/validated", produces = MediaType.TEXT_PLAIN_VALUE)
    public String validated(@Valid @BeanParameter HeaderParameterBean headerParameterBean) {
        return headerParameterBean.getValidated();
    }

    @GetMapping(value = "/validatedWithBindingResult", produces = MediaType.TEXT_PLAIN_VALUE)
    public String validatedWithBindingResult(@Valid @BeanParameter HeaderParameterBean headerParameterBean, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "notValid";
        }
        return "valid";
    }

    @Nullable
    @GetMapping(value = "/nested", produces = MediaType.TEXT_PLAIN_VALUE)
    public String nestedBean(@BeanParameter HeaderParameterBean headerParameterBean) {
        return Objects.requireNonNull(headerParameterBean.getNestedBean()).getHeaderValue();
    }

    @GetMapping(value = "/record", produces = MediaType.TEXT_PLAIN_VALUE)
    public String javaRecord(@BeanParameter HeaderParameterRecord headerParameterRecord) {
        return headerParameterRecord.annotated();
    }
}
