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

import com.mattbertolini.spring.test.web.bind.FormParameterController;
import com.mattbertolini.spring.web.servlet.mvc.bind.config.BinderConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringJUnitWebConfig(classes = {FormParameterIntegrationTest.Context.class})
public class FormParameterIntegrationTest {

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp(WebApplicationContext webApplicationContext) {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void bindsUsingAnnotatedField() throws Exception {
        makeRequest("/annotatedField", "annotated_field=expectedValue")
            .andExpect(status().isOk())
            .andExpect(content().string("expectedValue"));
    }

    @Test
    void bindsUsingAnnotatedSetter() throws Exception {
        makeRequest("/annotatedSetter", "annotated_setter=expectedValue")
            .andExpect(status().isOk())
            .andExpect(content().string("expectedValue"));
    }

    @Test
    void bindsUsingAnnotatedGetter() throws Exception {
        makeRequest("/annotatedGetter", "annotated_getter=expectedValue")
            .andExpect(status().isOk())
            .andExpect(content().string("expectedValue"));
    }

    @Test
    void bindsUsingSimpleMap() throws Exception {
        makeRequest("/simpleMap", "simple-map=expectedValue")
            .andExpect(status().isOk())
            .andExpect(content().string("expectedValue"));
    }

    @Test
    void bindsUsingMultiValueMap() throws Exception {
        makeRequest("/multiValueMap", "multi-value-map=expectedValue")
            .andExpect(status().isOk())
            .andExpect(content().string("expectedValue"));
    }

    @Test
    void hasBindingResult() throws Exception {
        makeRequest("/bindingResult", "annotated_field")
            .andExpect(status().isOk())
            .andExpect(content().string("0"));
    }

    @Test
    void validationErrorThatThrowsException() throws Exception {
        mockMvc.perform(post("/validated")
            .accept(MediaType.TEXT_PLAIN))
            .andExpect(status().isBadRequest());
    }

    @Test
    void validationErrorThatUsesBindingResult() throws Exception {
        mockMvc.perform(post("/validatedWithBindingResult")
            .accept(MediaType.TEXT_PLAIN))
            .andExpect(status().isOk())
            .andExpect(content().string("notValid"));
    }

    @Test
    void bindsNestedBean() throws Exception {
        makeRequest("/nested", "nested_form_param=expectedValue")
            .andExpect(status().isOk())
            .andExpect(content().string("expectedValue"));
    }

    private ResultActions makeRequest(String path, String inputData) throws Exception {
        return mockMvc.perform(post(path)
            .accept(MediaType.TEXT_PLAIN)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .content(inputData));
    }

    @ContextConfiguration
    @EnableWebMvc
    public static class Context {
        @Bean
        public BinderConfiguration binderConfiguration() {
            return new BinderConfiguration();
        }

        @Bean
        public FormParameterController controller() {
            return new FormParameterController();
        }
    }

}
