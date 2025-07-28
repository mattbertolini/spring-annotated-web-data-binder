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
package com.mattbertolini.spring.test.web.bind;

import com.mattbertolini.spring.test.web.bind.records.FormParameterRecord;
import com.mattbertolini.spring.web.bind.annotation.BeanParameter;
import jakarta.servlet.http.Part;
import jakarta.validation.Valid;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.lang.Nullable;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StreamUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@SuppressWarnings("NullAway")
@RestController
public class FormParameterController {
    @Nullable
    @PostMapping(value = "/annotatedField", produces = MediaType.TEXT_PLAIN_VALUE)
    public String annotatedField(@BeanParameter FormParameterBean formParameterBean) {
        return formParameterBean.getAnnotatedField();
    }

    @Nullable
    @PostMapping(value = "/annotatedSetter", produces = MediaType.TEXT_PLAIN_VALUE)
    public String annotatedSetter(@BeanParameter FormParameterBean formParameterBean) {
        return formParameterBean.getAnnotatedSetter();
    }

    @Nullable
    @PostMapping(value = "/annotatedGetter", produces = MediaType.TEXT_PLAIN_VALUE)
    public String handleRequest(@BeanParameter FormParameterBean formParameterBean) {
        return formParameterBean.getAnnotatedGetter();
    }

    @Nullable
    @PostMapping(value = "/simpleMap", produces = MediaType.TEXT_PLAIN_VALUE)
    public String simpleMap(@BeanParameter FormParameterBean formParameterBean) {
        Map<String, String> simpleMap = formParameterBean.getSimpleMap();
        return Objects.requireNonNull(simpleMap).get("simple-map");
    }

    @Nullable
    @PostMapping(value = "/multiValueMap", produces = MediaType.TEXT_PLAIN_VALUE)
    public String multiValueMap(@BeanParameter FormParameterBean formParameterBean) {
        MultiValueMap<String, String> multiValueMap = formParameterBean.getMultiValueMap();
        return Objects.requireNonNull(multiValueMap).getFirst("multi-value-map");
    }

    @SuppressWarnings("unused")
    @PostMapping(value = "/bindingResult", produces = MediaType.TEXT_PLAIN_VALUE)
    public String bindingResult(@BeanParameter FormParameterBean formParameterBean, BindingResult bindingResult) {
        return Integer.toString(bindingResult.getErrorCount());
    }

    @Nullable
    @PostMapping(value = "/validated", produces = MediaType.TEXT_PLAIN_VALUE)
    public String validated(@Valid @BeanParameter FormParameterBean formParameterBean) {
        return formParameterBean.getValidated();
    }

    @SuppressWarnings("unused")
    @PostMapping(value = "/validatedWithBindingResult", produces = MediaType.TEXT_PLAIN_VALUE)
    public String validatedWithBindingResult(@Valid @BeanParameter FormParameterBean formParameterBean, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "notValid";
        }
        return "valid";
    }

    @PostMapping(value = "/multipartFile", produces = MediaType.TEXT_PLAIN_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String multipartFile(@BeanParameter FormParameterBean.ServletMultipartBean formParameterBean) throws Exception {
        MultipartFile multipartFile = formParameterBean.getMultipartFile();
        if (multipartFile == null || multipartFile.isEmpty()) {
            throw new RuntimeException("Multipart file is null or empty");
        }
        return new String(multipartFile.getBytes(), StandardCharsets.UTF_8);
    }

    @PostMapping(value = "/part", produces = MediaType.TEXT_PLAIN_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String part(@BeanParameter FormParameterBean.ServletMultipartBean formParameterBean) throws Exception {
        Part part = formParameterBean.getPart();
        if (part == null || part.getSize() <= 0) {
            throw new RuntimeException("Multipart file is null or empty");
        }
        return StreamUtils.copyToString(part.getInputStream(), StandardCharsets.UTF_8);
    }

    @PostMapping(value = "/multipartFileMap", produces = MediaType.TEXT_PLAIN_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String multipartFileMap(@BeanParameter FormParameterBean.ServletMultipartBean formParameterBean) throws Exception {
        Map<String, MultipartFile> multipartFileMap = formParameterBean.getMultipartFileMap();
        MultipartFile fileOne = multipartFileMap.get("fileOne");
        MultipartFile fileTwo = multipartFileMap.get("fileTwo");
        return new String(fileOne.getBytes(), StandardCharsets.UTF_8) + ", " + new String(fileTwo.getBytes(), StandardCharsets.UTF_8);
    }

    @PostMapping(value = "/multiValueMultipartFileMap", produces = MediaType.TEXT_PLAIN_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String multiValueMultipartFileMap(@BeanParameter FormParameterBean.ServletMultipartBean formParameterBean) {
        MultiValueMap<String, MultipartFile> multiValueMultipartMap = formParameterBean.getMultiValueMultipartMap();
        List<MultipartFile> files = multiValueMultipartMap.get("file");
        return files.stream().map(multipartFile -> {
            try {
                return new String(multipartFile.getBytes(), StandardCharsets.UTF_8);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.joining(", "));
    }

    @PostMapping(value = "/partMap", produces = MediaType.TEXT_PLAIN_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String partMap(@BeanParameter FormParameterBean.ServletMultipartBean formParameterBean) throws Exception {
        Map<String, Part> partMap = formParameterBean.getPartMap();
        Part fileOne = partMap.get("fileOne");
        Part fileTwo = partMap.get("fileTwo");
        return StreamUtils.copyToString(fileOne.getInputStream(), StandardCharsets.UTF_8)
            + ", " + StreamUtils.copyToString(fileTwo.getInputStream(), StandardCharsets.UTF_8);
    }

    @PostMapping(value = "/multiValuePartMap", produces = MediaType.TEXT_PLAIN_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String multiValuePartMap(@BeanParameter FormParameterBean.ServletMultipartBean formParameterBean) {
        MultiValueMap<String, Part> multiValuePartMap = formParameterBean.getMultiValuePartMap();
        List<Part> files = multiValuePartMap.get("file");
        return files.stream().map(part -> {
            try {
                return StreamUtils.copyToString(part.getInputStream(), StandardCharsets.UTF_8);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.joining(", "));
    }

    @PostMapping(value = "/webFluxFilePart", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public String webFluxFilePart(@BeanParameter FormParameterBean.WebfluxMultipartBean formParameterBean) {
        FilePart filePart = formParameterBean.getFilePart();
        if (filePart == null) {
            throw new RuntimeException("Multipart file is null or empty");
        }
        ByteArrayOutputStream data = new ByteArrayOutputStream();
        Flux<DataBuffer> dataBuffer = DataBufferUtils.write(filePart.content(), data);
        dataBuffer.subscribe(DataBufferUtils.releaseConsumer());
        return new String(data.toByteArray(), StandardCharsets.UTF_8);
    }

    @Nullable
    @PostMapping(value = "/nested", produces = MediaType.TEXT_PLAIN_VALUE)
    public String nestedBeanParameter(@BeanParameter FormParameterBean formParameterBean) {
        return Objects.requireNonNull(formParameterBean.getNestedBean()).getFormData();
    }

    @PostMapping(value = "/record", produces = MediaType.TEXT_PLAIN_VALUE)
    public String javaRecord(@BeanParameter FormParameterRecord formParameterRecord) {
        return formParameterRecord.annotated();
    }
}
