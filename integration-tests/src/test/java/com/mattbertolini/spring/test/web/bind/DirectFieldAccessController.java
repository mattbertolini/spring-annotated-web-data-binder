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

import com.mattbertolini.spring.web.bind.annotation.BeanParameter;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
public class DirectFieldAccessController {
    @Nullable
    @GetMapping(value = "/cookieParameter", produces = MediaType.TEXT_PLAIN_VALUE)
    public String cookieParameter(@BeanParameter DirectFieldAccessBean directFieldAccessBean) {
        return directFieldAccessBean.getCookieParameter();
    }

    @Nullable
    @PostMapping(value = "/formParameter", produces = MediaType.TEXT_PLAIN_VALUE)
    public String formParameter(@BeanParameter DirectFieldAccessBean directFieldAccessBean) {
        return directFieldAccessBean.getFormParameter();
    }

    @Nullable
    @GetMapping(value = "/headerParameter", produces = MediaType.TEXT_PLAIN_VALUE)
    public String headerParameter(@BeanParameter DirectFieldAccessBean directFieldAccessBean) {
        return directFieldAccessBean.getHeaderParameter();
    }

    @Nullable
    @SuppressWarnings("MVCPathVariableInspection")
    @GetMapping(value = "/pathParameter/{path_parameter}", produces = MediaType.TEXT_PLAIN_VALUE)
    public String pathParameter(@BeanParameter DirectFieldAccessBean directFieldAccessBean) {
        return directFieldAccessBean.getPathParameter();
    }

    @Nullable
    @GetMapping(value = "/requestParameter", produces = MediaType.TEXT_PLAIN_VALUE)
    public String requestParameter(@BeanParameter DirectFieldAccessBean directFieldAccessBean) {
        return directFieldAccessBean.getRequestParameter();
    }

    @Nullable
    @GetMapping(value = "/sessionParameter", produces = MediaType.TEXT_PLAIN_VALUE)
    public String sessionParameter(@BeanParameter DirectFieldAccessBean directFieldAccessBean) {
        return directFieldAccessBean.getSessionParameter();
    }

    @Nullable
    @PostMapping(value = "/requestBody", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public String requestBody(@BeanParameter DirectFieldAccessBean.RequestBodyBean directFieldAccessBean) {
        return Objects.requireNonNull(directFieldAccessBean.getRequestBody()).getProperty();
    }
}
