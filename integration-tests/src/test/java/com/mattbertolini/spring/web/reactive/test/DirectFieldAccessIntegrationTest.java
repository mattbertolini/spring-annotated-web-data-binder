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

import com.mattbertolini.spring.test.web.bind.DirectFieldAccessController;
import com.mattbertolini.spring.web.reactive.bind.config.BinderConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.support.ConfigurableWebBindingInitializer;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.reactive.config.WebFluxConfigurationSupport;

import static org.springframework.web.reactive.function.BodyInserters.fromFormData;
import static org.springframework.web.reactive.function.BodyInserters.fromValue;

@SpringJUnitWebConfig(classes = {DirectFieldAccessIntegrationTest.Context.class})
class DirectFieldAccessIntegrationTest {

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext) {
        webTestClient = WebTestClient.bindToApplicationContext(webApplicationContext).build();
    }

    @Test
    void cookieParameter() {
        webTestClient.get()
            .uri("/cookieParameter")
            .accept(MediaType.TEXT_PLAIN)
            .cookie("cookie_parameter", "expectedValue")
            .exchange()
            .expectStatus().isOk()
            .expectBody(String.class).isEqualTo("expectedValue");
    }

    @Test
    void formParameter() {
        webTestClient.post()
            .uri("/formParameter")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(fromFormData("form_parameter", "expectedValue"))
            .accept(MediaType.TEXT_PLAIN)
            .exchange()
            .expectStatus().isOk()
            .expectBody(String.class).isEqualTo("expectedValue");
    }

    @Test
    void headerParameter() {
        webTestClient.get()
            .uri("/headerParameter")
            .accept(MediaType.TEXT_PLAIN)
            .header("header_parameter", "expectedValue")
            .exchange()
            .expectStatus().isOk()
            .expectBody(String.class).isEqualTo("expectedValue");
    }

    @Test
    void pathParameter() {
        webTestClient.get()
            .uri("/pathParameter/expectedValue")
            .accept(MediaType.TEXT_PLAIN)
            .exchange()
            .expectStatus().isOk()
            .expectBody(String.class).isEqualTo("expectedValue");
    }

    @Test
    void requestParameter() {
        webTestClient.get()
            .uri("/requestParameter?request_parameter=expectedValue")
            .accept(MediaType.TEXT_PLAIN)
            .exchange()
            .expectStatus().isOk()
            .expectBody(String.class).isEqualTo("expectedValue");
    }

    @Test
    void sessionParameter() {
        webTestClient.mutateWith(SessionMutator.session()
            .attribute("session_parameter", "expectedValue"))
            .get().uri("/sessionParameter")
            .accept(MediaType.TEXT_PLAIN)
            .exchange()
            .expectStatus().isOk()
            .expectBody(String.class).isEqualTo("expectedValue");
    }

    @Test
    void requestBody() {
        webTestClient.post()
            .uri("/requestBody")
            .contentType(MediaType.APPLICATION_JSON)
            .body(fromValue("{\"json_property\":  \"expectedValue\"}"))
            .accept(MediaType.TEXT_PLAIN)
            .exchange()
            .expectStatus().isOk()
            .expectBody(String.class).isEqualTo("expectedValue");
    }

    @Configuration
    static class Context extends WebFluxConfigurationSupport {

        @Override
        @NonNull
        protected ConfigurableWebBindingInitializer getConfigurableWebBindingInitializer(@NonNull FormattingConversionService mvcConversionService, @NonNull Validator mvcValidator) {
            ConfigurableWebBindingInitializer initializer = super.getConfigurableWebBindingInitializer(mvcConversionService, mvcValidator);
            initializer.setDirectFieldAccess(true);
            return initializer;
        }

        @Bean
        public BinderConfiguration binderConfiguration() {
            return new BinderConfiguration();
        }

        @Bean
        public LocalValidatorFactoryBean validator() {
            return new LocalValidatorFactoryBean();
        }

        @Bean
        public DirectFieldAccessController controller() {
            return new DirectFieldAccessController();
        }
    }
}
