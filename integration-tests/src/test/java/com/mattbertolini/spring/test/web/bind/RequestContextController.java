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

import com.mattbertolini.spring.test.web.bind.records.RequestContextRecord;
import com.mattbertolini.spring.web.bind.annotation.BeanParameter;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@RestController
public class RequestContextController {
    // Web MVC native request
    @GetMapping(value = "/annotatedField", produces = MediaType.TEXT_PLAIN_VALUE)
    public String annotatedField(@BeanParameter RequestContextBean requestContextBean) {
        assertThat(requestContextBean.getWebRequestField()).isNotNull();
        return "annotatedField";
    }

    @GetMapping(value = "/annotatedSetter", produces = MediaType.TEXT_PLAIN_VALUE)
    public String annotatedSetter(@BeanParameter RequestContextBean requestContextBean) {
        assertThat(requestContextBean.getWebRequestSetter()).isNotNull();
        return "annotatedSetter";
    }

    @GetMapping(value = "/annotatedGetter", produces = MediaType.TEXT_PLAIN_VALUE)
    public String annotatedGetter(@BeanParameter RequestContextBean requestContextBean) {
        assertThat(requestContextBean.getWebRequestGetter()).isNotNull();
        return "annotatedGetter";
    }

    // Reactive Server Exchange
    @GetMapping(value = "/exchangeField", produces = MediaType.TEXT_PLAIN_VALUE)
    public String exchangeField(@BeanParameter RequestContextBean requestContextBean) {
        assertThat(requestContextBean.getExchangeField()).isNotNull();
        return "exchangeField";
    }

    @GetMapping(value = "/exchangeSetter", produces = MediaType.TEXT_PLAIN_VALUE)
    public String exchangeSetter(@BeanParameter RequestContextBean requestContextBean) {
        assertThat(requestContextBean.getExchangeSetter()).isNotNull();
        return "exchangeSetter";
    }

    @GetMapping(value = "/exchangeGetter", produces = MediaType.TEXT_PLAIN_VALUE)
    public String exchangeGetter(@BeanParameter RequestContextBean requestContextBean) {
        assertThat(requestContextBean.getExchangeGetter()).isNotNull();
        return "exchangeGetter";
    }

    // Locale
    @Nullable
    @GetMapping(value = "/locale", produces = MediaType.TEXT_PLAIN_VALUE)
    public String locale(@BeanParameter RequestContextBean requestContextBean) {
        return Objects.requireNonNull(requestContextBean.getLocale()).toString();
    }

    @Nullable
    @GetMapping(value = "/timeZone", produces = MediaType.TEXT_PLAIN_VALUE)
    public String timeZone(@BeanParameter RequestContextBean requestContextBean) {
        return Objects.requireNonNull(requestContextBean.getTimeZone()).toString();
    }

    @GetMapping(value = "/timeZoneRecord", produces = MediaType.TEXT_PLAIN_VALUE)
    public String javaRecordTimeZone(@BeanParameter RequestContextRecord requestContextRecord) {
        return requestContextRecord.timeZone().toString();
    }

    @Nullable
    @GetMapping(value = "/zoneId", produces = MediaType.TEXT_PLAIN_VALUE)
    public String zoneId(@BeanParameter RequestContextBean requestContextBean) {
        return Objects.requireNonNull(requestContextBean.getZoneId()).toString();
    }

    @Nullable
    @GetMapping(value = "/method", produces = MediaType.TEXT_PLAIN_VALUE)
    public String method(@BeanParameter RequestContextBean requestContextBean) {
        return Objects.requireNonNull(requestContextBean.getMethod()).toString();
    }

    @Nullable
    @GetMapping(value = "/session", produces = MediaType.TEXT_PLAIN_VALUE)
    public String httpSession(@BeanParameter RequestContextBean requestContextBean) {
        return (String) Objects.requireNonNull(requestContextBean.getHttpSession()).getAttribute("name");
    }

    @Nullable
    @GetMapping(value = "/webSession", produces = MediaType.TEXT_PLAIN_VALUE)
    public String webSession(@BeanParameter RequestContextBean requestContextBean) {
        return Objects.requireNonNull(requestContextBean.getWebSession()).getAttribute("name");
    }
}
