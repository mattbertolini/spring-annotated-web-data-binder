package com.mattbertolini.spring.web.reactive.test;

import com.mattbertolini.spring.test.web.bind.RequestBodyController;
import com.mattbertolini.spring.web.reactive.bind.config.BinderConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.reactive.config.EnableWebFlux;

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
