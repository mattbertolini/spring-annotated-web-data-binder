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

import com.mattbertolini.spring.test.web.bind.RequestContextController;
import com.mattbertolini.spring.web.servlet.mvc.bind.config.BinderConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.time.ZoneId;
import java.util.Locale;
import java.util.TimeZone;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringJUnitWebConfig(classes = {RequestContextIntegrationTest.Context.class})
class RequestContextIntegrationTest {
    private MockMvc mockMvc;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext) {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void bindsUsingAnnotatedField() throws Exception {
        makeRequest("/annotatedField")
            .andExpect(status().isOk())
            .andExpect(content().string("annotatedField"));
    }

    @Test
    void bindsUsingAnnotatedSetter() throws Exception {
        makeRequest("/annotatedSetter")
            .andExpect(status().isOk())
            .andExpect(content().string("annotatedSetter"));
    }

    @Test
    void bindsUsingAnnotatedGetter() throws Exception {
        makeRequest("/annotatedGetter")
            .andExpect(status().isOk())
            .andExpect(content().string("annotatedGetter"));
    }

    @Test
    void bindsLocale() throws Exception {
        makeRequest("/locale")
            .andExpect(status().isOk())
            .andExpect(content().string(Locale.US.toString()));
    }

    @Test
    void bindsTimeZone() throws Exception {
        makeRequest("/timeZone")
            .andExpect(status().isOk())
            .andExpect(content().string(TimeZone.getDefault().toString()));
    }

    @Test
    void bindsZoneId() throws Exception {
        makeRequest("/zoneId")
            .andExpect(status().isOk())
            .andExpect(content().string(ZoneId.systemDefault().toString()));
    }

    @Test
    void bindsHttpMethod() throws Exception {
        makeRequest("/method")
            .andExpect(status().isOk())
            .andExpect(content().string("GET"));
    }

    @Test
    void bindsSession() throws Exception {
        makeRequest("/session")
            .andExpect(status().isOk())
            .andExpect(content().string("expectedValue"));
    }

    private ResultActions makeRequest(String path) throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("name", "expectedValue");
        return mockMvc.perform(get(path)
            .session(session)
            .accept(MediaType.TEXT_PLAIN)
            .header("Accept-Language", "en-US"));
    }

    @Configuration
    @EnableWebMvc
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
