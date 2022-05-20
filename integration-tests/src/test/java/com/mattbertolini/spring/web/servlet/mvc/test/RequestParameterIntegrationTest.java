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

package com.mattbertolini.spring.web.servlet.mvc.test;

import com.mattbertolini.spring.test.web.bind.RequestParameterController;
import com.mattbertolini.spring.web.servlet.mvc.bind.config.BinderConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringJUnitWebConfig(classes = {RequestParameterIntegrationTest.Context.class})
class RequestParameterIntegrationTest {

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp(WebApplicationContext webApplicationContext) {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void bindsUsingAnnotatedField() throws Exception {
        makeRequest("/annotatedField", "annotated_field")
            .andExpect(status().isOk())
            .andExpect(content().string("expectedValue"));
    }

    @Test
    void bindsUsingAnnotatedSetterMethod() throws Exception {
        makeRequest("/annotatedSetter", "annotated_setter")
            .andExpect(status().isOk())
            .andExpect(content().string("expectedValue"));
    }

    @Test
    void bindsUsingAnnotatedGetterMethod() throws Exception {
        makeRequest("/annotatedGetter", "annotated_getter")
            .andExpect(status().isOk())
            .andExpect(content().string("expectedValue"));
    }

    @Test
    void bindsUsingSimpleMap() throws Exception {
        makeRequest("/simpleMap", "simpleMap")
            .andExpect(status().isOk())
            .andExpect(content().string("expectedValue"));
    }

    @Test
    void bindsUsingMultiValueMap() throws Exception {
        makeRequest("/multiValueMap", "multiValueMap")
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
        mockMvc.perform(get("/validated")
            .accept(MediaType.TEXT_PLAIN))
            .andExpect(status().isBadRequest());
    }

    @Test
    void validationErrorThatUsesBindingResult() throws Exception {
        mockMvc.perform(get("/validatedWithBindingResult")
            .accept(MediaType.TEXT_PLAIN))
            .andExpect(status().isOk())
            .andExpect(content().string("notValid"));
    }

    @Test
    void bindsMultipartFile() throws Exception {
        String expected = "this is a multipart file";
        MockMultipartFile multipartFile = new MockMultipartFile(
            "file",
            "mockFile.txt",
            MediaType.TEXT_PLAIN_VALUE,
            expected.getBytes(StandardCharsets.UTF_8)
        );
        mockMvc.perform(multipart("/multipartFile")
            .file(multipartFile)
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .accept(MediaType.TEXT_PLAIN))
            .andExpect(status().isOk())
            .andExpect(content().string(expected));
    }

    @Test
    void bindsPart() throws Exception {
        String expected = "this is a file part";
        MockPart part = new MockPart(
            "part",
            "mockFile.txt",
            expected.getBytes(StandardCharsets.UTF_8)
        );
        mockMvc.perform(multipart("/part")
            .part(part)
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .accept(MediaType.TEXT_PLAIN))
            .andExpect(status().isOk())
            .andExpect(content().string(expected));
    }

    @Test
    void bindsMultipartFileMap() throws Exception {
        MockMultipartFile fileOne = new MockMultipartFile(
            "fileOne",
            "fileOne.txt",
            MediaType.TEXT_PLAIN_VALUE,
            "fileOneValue".getBytes(StandardCharsets.UTF_8)
        );
        MockMultipartFile fileTwo = new MockMultipartFile(
            "fileTwo",
            "fileTwo.txt",
            MediaType.TEXT_PLAIN_VALUE,
            "fileTwoValue".getBytes(StandardCharsets.UTF_8)
        );
        mockMvc.perform(multipart("/multipartFileMap")
            .file(fileOne)
            .file(fileTwo)
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .accept(MediaType.TEXT_PLAIN))
            .andExpect(status().isOk())
            .andExpect(content().string("fileOneValue, fileTwoValue"));
    }

    @Test
    void bindsMultiValueMultipartFileMap() throws Exception {
        MockMultipartFile fileOne = new MockMultipartFile(
            "file",
            "fileOne.txt",
            MediaType.TEXT_PLAIN_VALUE,
            "fileOneValue".getBytes(StandardCharsets.UTF_8)
        );
        MockMultipartFile fileTwo = new MockMultipartFile(
            "file",
            "fileTwo.txt",
            MediaType.TEXT_PLAIN_VALUE,
            "fileTwoValue".getBytes(StandardCharsets.UTF_8)
        );
        mockMvc.perform(multipart("/multiValueMultipartFileMap")
            .file(fileOne)
            .file(fileTwo)
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .accept(MediaType.TEXT_PLAIN))
            .andExpect(status().isOk())
            .andExpect(content().string("fileOneValue, fileTwoValue"));
    }

    @Test
    void bindsPartMap() throws Exception {
        MockPart fileOne = new MockPart(
            "fileOne",
            "fileOne.txt",
            "fileOneValue".getBytes(StandardCharsets.UTF_8)
        );
        MockPart fileTwo = new MockPart(
            "fileTwo",
            "fileTwo.txt",
            "fileTwoValue".getBytes(StandardCharsets.UTF_8)
        );
        mockMvc.perform(multipart("/partMap")
            .part(fileOne)
            .part(fileTwo)
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .accept(MediaType.TEXT_PLAIN))
            .andExpect(status().isOk())
            .andExpect(content().string("fileOneValue, fileTwoValue"));
    }

    @Test
    void bindsMultiValuePartMap() throws Exception {
        MockPart fileOne = new MockPart(
            "file",
            "fileOne.txt",
            "fileOneValue".getBytes(StandardCharsets.UTF_8)
        );
        MockPart fileTwo = new MockPart(
            "file",
            "fileTwo.txt",
            "fileTwoValue".getBytes(StandardCharsets.UTF_8)
        );
        mockMvc.perform(multipart("/multiValuePartMap")
            .part(fileOne)
            .part(fileTwo)
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .accept(MediaType.TEXT_PLAIN))
            .andExpect(status().isOk())
            .andExpect(content().string("fileOneValue, fileTwoValue"));
    }

    @Test
    void bindsNestedBean() throws Exception {
        makeRequest("/nested", "nested_query_param")
            .andExpect(status().isOk())
            .andExpect(content().string("expectedValue"));
    }

    @Test
    void bindsJavaRecord() throws Exception {
        makeRequest("/record", "annotated_field")
            .andExpect(status().isOk())
            .andExpect(content().string("expectedValue"));
    }

    private ResultActions makeRequest(String path, String inputParameter) throws Exception {
        return mockMvc.perform(get(path)
            .accept(MediaType.TEXT_PLAIN)
            .queryParam(inputParameter, "expectedValue"));
    }

    @ContextConfiguration
    @EnableWebMvc
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
        public RequestParameterController controller() {
            return new RequestParameterController();
        }
    }
}
