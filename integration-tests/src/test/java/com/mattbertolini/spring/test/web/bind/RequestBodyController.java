/*
 * Copyright 2019-2022 the original author or authors.
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

import com.mattbertolini.spring.test.web.bind.records.RequestBodyRecord;
import com.mattbertolini.spring.web.bind.annotation.BeanParameter;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.Part;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.concurrent.atomic.AtomicInteger;

@RestController
public class RequestBodyController {
    @PostMapping(value = "/annotatedField", produces = MediaType.TEXT_PLAIN_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public String annotatedField(@BeanParameter RequestBodyBean.AnnotatedField requestBodyBean) {
        JsonBody jsonBody = requestBodyBean.getJsonBody();
        return jsonBody.getProperty();
    }

    @PostMapping(value = "/annotatedSetter", produces = MediaType.TEXT_PLAIN_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public String annotatedSetter(@BeanParameter RequestBodyBean.AnnotatedSetter requestBodyBean) {
        JsonBody jsonBody = requestBodyBean.getJsonBody();
        return jsonBody.getProperty();
    }

    @PostMapping(value = "/annotatedGetter", produces = MediaType.TEXT_PLAIN_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public String annotatedGetter(@BeanParameter RequestBodyBean.AnnotatedGetter requestBodyBean) {
        JsonBody jsonBody = requestBodyBean.getJsonBody();
        return jsonBody.getProperty();
    }

    @SuppressWarnings("unused")
    @PostMapping(value = "/bindingResult", produces = MediaType.TEXT_PLAIN_VALUE)
    public String bindingResult(@BeanParameter RequestBodyBean.BindingResult requestBodyBean, BindingResult bindingResult) {
        return Integer.toString(bindingResult.getErrorCount());
    }

    @PostMapping(value = "/validated", produces = MediaType.TEXT_PLAIN_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public String validated(@Valid @BeanParameter RequestBodyBean.Validation requestBodyBean) {
        return requestBodyBean.getJsonBody().getProperty();
    }

    @SuppressWarnings("unused")
    @PostMapping(value = "/validatedWithBindingResult", produces = MediaType.TEXT_PLAIN_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public String validatedWithBindingResult(@Valid @BeanParameter RequestBodyBean.Validation requestBodyBean, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "notValid";
        }
        return "valid";
    }

    @PostMapping(value = "/nested", produces = MediaType.TEXT_PLAIN_VALUE)
    public String nestedBeanParameter(@BeanParameter RequestBodyBean.Nested requestBodyBean) {
        return requestBodyBean.getNestedBean().getRequestBody().getProperty();
    }

    @PostMapping(value = "/multipartMultiValueMap", produces = MediaType.TEXT_PLAIN_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String multipartMultiValueMap(@BeanParameter RequestBodyBean.WebFluxMultipartMultiValueMap requestBodyBean) {
        MultiValueMap<String, Part> parts = requestBodyBean.getParts();
        Assert.notEmpty(parts, "Multipart multi-value map is null or empty");
        return "multipartMultiValueMap " + parts.size();
    }

    @PostMapping(value = "/multipartFlux", produces = MediaType.TEXT_PLAIN_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String multipartFlux(@BeanParameter RequestBodyBean.WebFluxMultipartFlux requestBodyBean) {
        Flux<Part> parts = requestBodyBean.getParts();
        AtomicInteger count = new AtomicInteger();
        parts.collectList().map(partList -> {
            partList.forEach(part -> count.getAndIncrement());
            return count.get();
        }).subscribe();
        return "multipartFlux " + count.get();
    }

    @PostMapping(value = "/record", produces = MediaType.TEXT_PLAIN_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public String javaRecord(@BeanParameter RequestBodyRecord requestBodyRecord) {
        JsonBody jsonBody = requestBodyRecord.jsonBody();
        return jsonBody.getProperty();
    }
}
