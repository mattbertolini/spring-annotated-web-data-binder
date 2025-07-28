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
package com.mattbertolini.spring.web.reactive.test;

import com.mattbertolini.spring.test.web.bind.RequestBodyController;
import com.mattbertolini.spring.web.reactive.bind.config.BinderConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.reactive.config.EnableWebFlux;

import static org.springframework.web.reactive.function.BodyInserters.fromMultipartData;
import static org.springframework.web.reactive.function.BodyInserters.fromValue;

@SpringJUnitWebConfig(classes = {RequestBodyIntegrationTest.Context.class})
class RequestBodyIntegrationTest {
    private WebTestClient webTestClient;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext) {
        webTestClient = WebTestClient.bindToApplicationContext(webApplicationContext).build();
    }

    @Test
    void bindsUsingAnnotatedField() {
        makeRequest("/annotatedField")
            .expectStatus().isOk()
            .expectBody(String.class).isEqualTo("expectedValue");
    }

    @Test
    void bindsUsingAnnotatedSetter() {
        makeRequest("/annotatedSetter")
            .expectStatus().isOk()
            .expectBody(String.class).isEqualTo("expectedValue");
    }

    @Test
    void bindsUsingAnnotatedGetter() {
        makeRequest("/annotatedGetter")
            .expectStatus().isOk()
            .expectBody(String.class).isEqualTo("expectedValue");
    }

    @Test
    void hasBindingResult() {
        makeRequest("/bindingResult")
            .expectStatus().isOk()
            .expectBody(String.class).isEqualTo("0");
    }

    @Test
    void validationErrorThatThrowsException() {
        webTestClient.post().uri("/validated")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.TEXT_PLAIN).exchange()
            .expectStatus().isBadRequest();
    }

    @Test
    void validationErrorThatUsesBindingResult() {
        webTestClient.post().uri("/validatedWithBindingResult")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.TEXT_PLAIN).exchange()
            .expectStatus().isOk()
            .expectBody(String.class).isEqualTo("notValid");
    }

    @Test
    void bindsNestedBean() {
        makeRequest("/nested")
            .expectStatus().isOk()
            .expectBody(String.class).isEqualTo("expectedValue");
    }

    @Test
    void bindsMultipartMultiValueMap() {
        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
        multipartBodyBuilder.part("file", "file")
            .contentType(MediaType.TEXT_PLAIN)
            .filename("file.txt");
        multipartBodyBuilder.part("another", "another")
            .contentType(MediaType.TEXT_PLAIN)
            .filename("another.txt");
        webTestClient.post()
            .uri("/multipartMultiValueMap")
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .body(fromMultipartData(multipartBodyBuilder.build()))
            .accept(MediaType.TEXT_PLAIN)
            .exchange()
            .expectStatus().isOk()
            .expectBody(String.class).isEqualTo("multipartMultiValueMap 2");
    }

    @Test
    void bindsMultipartFlux() {
        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
        multipartBodyBuilder.part("file", "file")
            .contentType(MediaType.TEXT_PLAIN)
            .filename("file.txt");
        multipartBodyBuilder.part("another", "another")
            .contentType(MediaType.TEXT_PLAIN)
            .filename("another.txt");
        webTestClient.post()
            .uri("/multipartFlux")
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .body(fromMultipartData(multipartBodyBuilder.build()))
            .accept(MediaType.TEXT_PLAIN)
            .exchange()
            .expectStatus().isOk()
            .expectBody(String.class).isEqualTo("multipartFlux 2");
    }

    @Test
    void bindsUsingJavaRecord() {
        makeRequest("/record")
            .expectStatus().isOk()
            .expectBody(String.class).isEqualTo("expectedValue");
    }

    private WebTestClient.ResponseSpec makeRequest(String path) {
        return webTestClient.post()
            .uri(uriBuilder -> uriBuilder.path(path)
                .build())
            .contentType(MediaType.APPLICATION_JSON)
            .body(fromValue("{\"json_property\":  \"expectedValue\"}"))
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
        public LocalValidatorFactoryBean validator() {
            return new LocalValidatorFactoryBean();
        }

        @Bean
        public RequestBodyController controller() {
            return new RequestBodyController();
        }
    }
}
