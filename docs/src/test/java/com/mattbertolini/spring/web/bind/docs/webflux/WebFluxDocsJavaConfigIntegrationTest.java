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

package com.mattbertolini.spring.web.bind.docs.webflux;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.web.reactive.function.BodyInserters.fromFormData;

@SpringJUnitWebConfig(classes = {ExampleContext.class})
class WebFluxDocsJavaConfigIntegrationTest {
    private WebTestClient webTestClient;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext) {
        webTestClient = WebTestClient.bindToApplicationContext(webApplicationContext).build();
    }

    @Test
    void makesRequestAndBindsData() {
        webTestClient.post()
            .uri("/example/42?different_name=different_value")
            .accept(MediaType.TEXT_PLAIN)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(fromFormData("form_data_", "form_value"))
            .header("Accept-Language", "en-US")
            .header("X-Custom-Header", "A_Header_Value")
            .cookie("cookie_value", "some_cookie_value")
            .exchange().expectStatus().isOk();
    }
}
