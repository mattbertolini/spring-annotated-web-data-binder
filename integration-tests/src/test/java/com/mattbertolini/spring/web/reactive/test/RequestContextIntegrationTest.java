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

package com.mattbertolini.spring.web.reactive.test;

import com.mattbertolini.spring.test.web.bind.RequestContextController;
import com.mattbertolini.spring.web.reactive.bind.config.BinderConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.reactive.config.EnableWebFlux;

import java.time.ZoneId;
import java.util.Locale;
import java.util.TimeZone;

import static com.mattbertolini.spring.web.reactive.test.SessionMutator.session;

@SpringJUnitWebConfig(classes = {RequestContextIntegrationTest.Context.class})
class RequestContextIntegrationTest {
    private WebTestClient webTestClient;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext) {
        webTestClient = WebTestClient.bindToApplicationContext(webApplicationContext).build();
    }

    @Test
    void bindsUsingAnnotatedField() {
        makeRequest("/exchangeField")
            .expectStatus().isOk()
            .expectBody(String.class).isEqualTo("exchangeField");
    }

    @Test
    void bindsUsingAnnotatedSetter() {
        makeRequest("/exchangeSetter")
            .expectStatus().isOk()
            .expectBody(String.class).isEqualTo("exchangeSetter");
    }

    @Test
    void bindsUsingAnnotatedGetter() {
        makeRequest("/exchangeGetter")
            .expectStatus().isOk()
            .expectBody(String.class).isEqualTo("exchangeGetter");
    }

    @Test
    void bindsLocale() {
        makeRequest("/locale")
            .expectStatus().isOk()
            .expectBody(String.class).isEqualTo(Locale.US.toString());
    }

    @Test
    void bindsTimeZone() {
        makeRequest("/timeZone")
            .expectStatus().isOk()
            .expectBody(String.class).isEqualTo(TimeZone.getDefault().toString());
    }

    @Test
    void bindsTimeZoneJavaRecord() {
        makeRequest("/timeZoneRecord")
            .expectStatus().isOk()
            .expectBody(String.class).isEqualTo(TimeZone.getDefault().toString());
    }

    @Test
    void bindsZoneId() {
        makeRequest("/zoneId")
            .expectStatus().isOk()
            .expectBody(String.class).isEqualTo(ZoneId.systemDefault().toString());
    }

    @Test
    void bindsHttpMethod() {
        makeRequest("/method")
            .expectStatus().isOk()
            .expectBody(String.class).isEqualTo("GET");
    }

    @Test
    void bindsSession() {
        makeRequest("/webSession")
            .expectStatus().isOk()
            .expectBody(String.class).isEqualTo("expectedValue");
    }

    private WebTestClient.ResponseSpec makeRequest(String path) {
        return webTestClient.mutateWith(session()
            .attribute("name", "expectedValue"))
            .get().uri(path)
            .accept(MediaType.TEXT_PLAIN)
            .header("Accept-Language", "en-US")
            .exchange();
    }

    @Configuration
    @EnableWebFlux
    static class Context {
        @Bean
        public BinderConfiguration binderConfiguration() {
            return new BinderConfiguration();
        }

        @Bean
        public RequestContextController controller() {
            return new RequestContextController();
        }
    }
}
