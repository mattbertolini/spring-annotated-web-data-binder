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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DirectFieldAccessController {
    @GetMapping(value = "/cookieParameter", produces = MediaType.TEXT_PLAIN_VALUE)
    public String cookieParameter(@BeanParameter DirectFieldAccessBean directFieldAccessBean) {
        return directFieldAccessBean.getCookieParameter();
    }

    @PostMapping(value = "/formParameter", produces = MediaType.TEXT_PLAIN_VALUE)
    public String formParameter(@BeanParameter DirectFieldAccessBean directFieldAccessBean) {
        return directFieldAccessBean.getFormParameter();
    }

    @GetMapping(value = "/headerParameter", produces = MediaType.TEXT_PLAIN_VALUE)
    public String headerParameter(@BeanParameter DirectFieldAccessBean directFieldAccessBean) {
        return directFieldAccessBean.getHeaderParameter();
    }

    @SuppressWarnings("MVCPathVariableInspection")
    @GetMapping(value = "/pathParameter/{path_parameter}", produces = MediaType.TEXT_PLAIN_VALUE)
    public String pathParameter(@BeanParameter DirectFieldAccessBean directFieldAccessBean) {
        return directFieldAccessBean.getPathParameter();
    }

    @GetMapping(value = "/requestParameter", produces = MediaType.TEXT_PLAIN_VALUE)
    public String requestParameter(@BeanParameter DirectFieldAccessBean directFieldAccessBean) {
        return directFieldAccessBean.getRequestParameter();
    }

    @GetMapping(value = "/sessionParameter", produces = MediaType.TEXT_PLAIN_VALUE)
    public String sessionParameter(@BeanParameter DirectFieldAccessBean directFieldAccessBean) {
        return directFieldAccessBean.getSessionParameter();
    }
}
