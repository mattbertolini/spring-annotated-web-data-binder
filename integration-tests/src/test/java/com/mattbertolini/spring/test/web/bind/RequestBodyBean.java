/*
 * Copyright 2019-2021 the original author or authors.
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
import com.mattbertolini.spring.web.bind.annotation.RequestBody;
import org.springframework.http.codec.multipart.Part;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Flux;

import javax.validation.constraints.NotNull;

public class RequestBodyBean {

    @SuppressWarnings("unused")
    public static class AnnotatedField {
        @RequestBody
        private JsonBody jsonBody;

        public JsonBody getJsonBody() {
            return jsonBody;
        }

        public void setJsonBody(JsonBody jsonBody) {
            this.jsonBody = jsonBody;
        }
    }

    @SuppressWarnings("unused")
    public static class AnnotatedSetter {
        private JsonBody jsonBody;

        public JsonBody getJsonBody() {
            return jsonBody;
        }

        @RequestBody
        public void setJsonBody(JsonBody jsonBody) {
            this.jsonBody = jsonBody;
        }
    }

    @SuppressWarnings("unused")
    public static class AnnotatedGetter {
        private JsonBody jsonBody;

        @RequestBody
        public JsonBody getJsonBody() {
            return jsonBody;
        }

        public void setJsonBody(JsonBody jsonBody) {
            this.jsonBody = jsonBody;
        }
    }

    @SuppressWarnings("unused")
    public static class BindingResult {
        @RequestBody
        private JsonBody jsonBody;

        public JsonBody getJsonBody() {
            return jsonBody;
        }

        public void setJsonBody(JsonBody jsonBody) {
            this.jsonBody = jsonBody;
        }
    }

    @SuppressWarnings("unused")
    public static class Validation {
        @NotNull
        @RequestBody
        private JsonBody jsonBody;
        
        public JsonBody getJsonBody() {
            return jsonBody;
        }

        public void setJsonBody(JsonBody jsonBody) {
            this.jsonBody = jsonBody;
        }
    }

    @SuppressWarnings("unused")
    public static class Nested {
        @BeanParameter
        private NestedBean.RequestBodyBean nestedBean;

        public NestedBean.RequestBodyBean getNestedBean() {
            return nestedBean;
        }

        public void setNestedBean(NestedBean.RequestBodyBean nestedBean) {
            this.nestedBean = nestedBean;
        }
    }

    @SuppressWarnings("unused")
    public static class WebFluxMultipartMultiValueMap {
        @RequestBody
        private MultiValueMap<String, Part> parts;

        public MultiValueMap<String, Part> getParts() {
            return parts;
        }

        public void setParts(MultiValueMap<String, Part> parts) {
            this.parts = parts;
        }
    }

    @SuppressWarnings("unused")
    public static class WebFluxMultipartFlux {
        @RequestBody
        private Flux<Part> parts;

        public Flux<Part> getParts() {
            return parts;
        }

        public void setParts(Flux<Part> parts) {
            this.parts = parts;
        }
    }
}
