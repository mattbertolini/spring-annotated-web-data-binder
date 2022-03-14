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

package com.mattbertolini.spring.test.web.bind;

import com.mattbertolini.spring.web.bind.annotation.BeanParameter;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StreamUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.Part;
import javax.validation.Valid;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class RequestParameterController {
    @GetMapping(value = "/annotatedField", produces = MediaType.TEXT_PLAIN_VALUE)
    public String annotatedField(@BeanParameter RequestParameterBean requestParameterBean) {
        return requestParameterBean.getAnnotatedField();
    }

    @GetMapping(value = "/annotatedSetter", produces = MediaType.TEXT_PLAIN_VALUE)
    public String annotatedSetter(@BeanParameter RequestParameterBean requestParameterBean) {
        return requestParameterBean.getAnnotatedSetter();
    }

    @GetMapping(value = "/annotatedGetter", produces = MediaType.TEXT_PLAIN_VALUE)
    public String annotatedGetter(@BeanParameter RequestParameterBean requestParameterBean) {
        return requestParameterBean.getAnnotatedGetter();
    }

    @GetMapping(value = "/simpleMap", produces = MediaType.TEXT_PLAIN_VALUE)
    public String simpleMap(@BeanParameter RequestParameterBean requestParameterBean) {
        Map<String, String> simpleMap = requestParameterBean.getSimpleMap();
        return simpleMap.get("simpleMap");
    }

    @GetMapping(value = "/multiValueMap", produces = MediaType.TEXT_PLAIN_VALUE)
    public String multiValueMap(@BeanParameter RequestParameterBean requestParameterBean) {
        MultiValueMap<String, String> multiValueMap = requestParameterBean.getMultiValueMap();
        return multiValueMap.getFirst("multiValueMap");
    }

    @GetMapping(value = "/bindingResult", produces = MediaType.TEXT_PLAIN_VALUE)
    public String bindingResult(@BeanParameter RequestParameterBean requestParameterBean, BindingResult bindingResult) {
        return Integer.toString(bindingResult.getErrorCount());
    }

    @GetMapping(value = "/validated", produces = MediaType.TEXT_PLAIN_VALUE)
    public String validated(@Valid @BeanParameter RequestParameterBean requestParameterBean) {
        return requestParameterBean.getValidated();
    }

    @GetMapping(value = "/validatedWithBindingResult", produces = MediaType.TEXT_PLAIN_VALUE)
    public String validatedWithBindingResult(@Valid @BeanParameter RequestParameterBean requestParameterBean, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "notValid";
        }
        return "valid";
    }

    @PostMapping(value = "/multipartFile", produces = MediaType.TEXT_PLAIN_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String multipartFile(@BeanParameter RequestParameterBean.ServletMultipartBean requestParameterBean) throws Exception {
        MultipartFile multipartFile = requestParameterBean.getMultipartFile();
        if (multipartFile == null || multipartFile.isEmpty()) {
            throw new RuntimeException("Multipart file is null or empty");
        }
        return new String(multipartFile.getBytes());
    }

    @PostMapping(value = "/part", produces = MediaType.TEXT_PLAIN_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String part(@BeanParameter RequestParameterBean.ServletMultipartBean requestParameterBean) throws Exception {
        Part part = requestParameterBean.getPart();
        if (part == null || part.getSize() <= 0) {
            throw new RuntimeException("Multipart file is null or empty");
        }
        return StreamUtils.copyToString(part.getInputStream(), StandardCharsets.UTF_8);
    }

    @PostMapping(value = "/multipartFileMap", produces = MediaType.TEXT_PLAIN_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String multipartFileMap(@BeanParameter RequestParameterBean.ServletMultipartBean requestParameterBean) throws Exception {
        Map<String, MultipartFile> multipartFileMap = requestParameterBean.getMultipartFileMap();
        MultipartFile fileOne = multipartFileMap.get("fileOne");
        MultipartFile fileTwo = multipartFileMap.get("fileTwo");
        return new String(fileOne.getBytes()) + ", " + new String(fileTwo.getBytes());
    }

    @PostMapping(value = "/multiValueMultipartFileMap", produces = MediaType.TEXT_PLAIN_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String multiValueMultipartFileMap(@BeanParameter RequestParameterBean.ServletMultipartBean requestParameterBean) {
        MultiValueMap<String, MultipartFile> multiValueMultipartMap = requestParameterBean.getMultiValueMultipartMap();
        List<MultipartFile> files = multiValueMultipartMap.get("file");
        return files.stream().map(multipartFile -> {
            try {
                return new String(multipartFile.getBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.joining(", "));
    }

    @PostMapping(value = "/partMap", produces = MediaType.TEXT_PLAIN_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String partMap(@BeanParameter RequestParameterBean.ServletMultipartBean requestParameterBean) throws Exception {
        Map<String, Part> partMap = requestParameterBean.getPartMap();
        Part fileOne = partMap.get("fileOne");
        Part fileTwo = partMap.get("fileTwo");
        return StreamUtils.copyToString(fileOne.getInputStream(), StandardCharsets.UTF_8)
            + ", " + StreamUtils.copyToString(fileTwo.getInputStream(), StandardCharsets.UTF_8);
    }

    @PostMapping(value = "/multiValuePartMap", produces = MediaType.TEXT_PLAIN_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String multiValuePartMap(@BeanParameter RequestParameterBean.ServletMultipartBean requestParameterBean) {
        MultiValueMap<String, Part> multiValuePartMap = requestParameterBean.getMultiValuePartMap();
        List<Part> files = multiValuePartMap.get("file");
        return files.stream().map(part -> {
            try {
                return StreamUtils.copyToString(part.getInputStream(), StandardCharsets.UTF_8);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.joining(", "));
    }

    @GetMapping(value = "/nested", produces = MediaType.TEXT_PLAIN_VALUE)
    public String nestedBeanParameter(@BeanParameter RequestParameterBean requestParameterBean) {
        return requestParameterBean.getNestedBean().getQueryParam();
    }

    @GetMapping(value = "/record", produces = MediaType.TEXT_PLAIN_VALUE)
    public String javaRecord(@BeanParameter RequestParameterRecord requestParameterRecord) {
        return requestParameterRecord.annotated();
    }
}
