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

package com.mattbertolini.spring.web.servlet.mvc.test;

import com.mattbertolini.spring.test.web.bind.DirectFieldAccessController;
import com.mattbertolini.spring.web.servlet.mvc.bind.config.BinderConfiguration;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.support.ConfigurableWebBindingInitializer;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringJUnitWebConfig(classes = {DirectFieldAccessIntegrationTest.Context.class})
class DirectFieldAccessIntegrationTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext) {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void cookieParameter() throws Exception {
        mockMvc.perform(get("/cookieParameter")
            .accept(MediaType.TEXT_PLAIN)
            .cookie(new Cookie("cookie_parameter", "expectedValue")))
            .andExpect(status().isOk())
            .andExpect(content().string("expectedValue"));
    }

    @Test
    void formParameter() throws Exception {
        mockMvc.perform(post("/formParameter")
            .accept(MediaType.TEXT_PLAIN)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .content("form_parameter=expectedValue"))
            .andExpect(status().isOk())
            .andExpect(content().string("expectedValue"));
    }

    @Test
    void headerParameter() throws Exception {
        mockMvc.perform(get("/headerParameter")
            .accept(MediaType.TEXT_PLAIN)
            .header("header_parameter", "expectedValue"))
            .andExpect(status().isOk())
            .andExpect(content().string("expectedValue"));
    }

    @Test
    void pathParameter() throws Exception {
        mockMvc.perform(get("/pathParameter/expectedValue")
            .accept(MediaType.TEXT_PLAIN))
            .andExpect(status().isOk())
            .andExpect(content().string("expectedValue"));
    }

    @Test
    void requestParameter() throws Exception {
        mockMvc.perform(get("/requestParameter")
            .accept(MediaType.TEXT_PLAIN)
            .queryParam("request_parameter", "expectedValue"))
            .andExpect(status().isOk())
            .andExpect(content().string("expectedValue"));
    }

    @Test
    void sessionParameter() throws Exception {
        mockMvc.perform(get("/sessionParameter")
            .accept(MediaType.TEXT_PLAIN)
            .sessionAttr("session_parameter", "expectedValue"))
            .andExpect(status().isOk())
            .andExpect(content().string("expectedValue"));
    }

    @Test
    void requestBody() throws Exception {
        mockMvc.perform(post("/requestBody")
            .accept(MediaType.TEXT_PLAIN)
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"json_property\":  \"expectedValue\"}"))
            .andExpect(status().isOk())
            .andExpect(content().string("expectedValue"));
    }

    @Configuration
    static class Context extends WebMvcConfigurationSupport {

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
