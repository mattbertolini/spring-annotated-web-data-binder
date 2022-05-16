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

import com.mattbertolini.spring.test.web.bind.PathParameterController;
import com.mattbertolini.spring.web.reactive.bind.config.BinderConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.reactive.config.EnableWebFlux;

@SpringJUnitWebConfig(classes = {PathParameterIntegrationTest.Context.class})
class PathParameterIntegrationTest {

    private WebTestClient webTestClient;

    @BeforeEach
    public void setUp(WebApplicationContext webApplicationContext) {
        webTestClient = WebTestClient.bindToApplicationContext(webApplicationContext).build();
    }

    @Test
    void bindsUsingAnnotatedField() {
        makeRequest("/annotatedField/expectedValue")
            .expectStatus().isOk()
            .expectBody(String.class).isEqualTo("expectedValue");
    }

    @Test
    void bindsUsingAnnotatedSetter() {
        makeRequest("/annotatedSetter/expectedValue")
            .expectStatus().isOk()
            .expectBody(String.class).isEqualTo("expectedValue");
    }

    @Test
    void bindsUsingAnnotatedGetter() {
        makeRequest("/annotatedGetter/expectedValue")
            .expectStatus().isOk()
            .expectBody(String.class).isEqualTo("expectedValue");
    }

    @Test
    void bindsUsingMap() {
        makeRequest("/simpleMap/expectedValue")
            .expectStatus().isOk()
            .expectBody(String.class).isEqualTo("expectedValue");
    }

    @Test
    void hasBindingResult() {
        makeRequest("/bindingResult/expectedValue")
            .expectStatus().isOk()
            .expectBody(String.class).isEqualTo("0");
    }

    @Test
    void validationErrorThatThrowsException() {
        makeRequest("/validated")
            .expectStatus().isBadRequest();
    }

    @Test
    void validationErrorThatUsesBindingResult() {
        makeRequest("/validatedWithBindingResult")
            .expectStatus().isOk()
            .expectBody(String.class).isEqualTo("notValid");
    }

    @Test
    void bindsNestedBean() {
        makeRequest("/nested/expectedValue")
            .expectStatus().isOk()
            .expectBody(String.class).isEqualTo("expectedValue");
    }

    @Test
    void bindsUsingJavaRecord() {
        makeRequest("/record/expectedValue")
            .expectStatus().isOk()
            .expectBody(String.class).isEqualTo("expectedValue");
    }

    private WebTestClient.ResponseSpec makeRequest(String path) {
        return webTestClient.get()
            .uri(path)
            .accept(MediaType.TEXT_PLAIN)
            .exchange();
    }

    @ContextConfiguration
    @EnableWebFlux
    static class Context {
        @Bean
        public BinderConfiguration binderConfiguration() {
            return new BinderConfiguration();
        }

        @Bean
        public PathParameterController controller() {
            return new PathParameterController();
        }
    }
}
