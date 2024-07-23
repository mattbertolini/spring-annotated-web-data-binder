/*
 * Copyright 2024 the original author or authors.
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

import com.mattbertolini.spring.web.bind.annotation.BeanParameter;
import com.mattbertolini.spring.web.bind.annotation.RequestBody;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.codec.multipart.Part;
import org.springframework.lang.Nullable;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Flux;

public class RequestBodyBean {

    @SuppressWarnings("unused")
    public static class AnnotatedField {
        @Nullable
        @RequestBody
        private JsonBody jsonBody;

        @Nullable
        public JsonBody getJsonBody() {
            return jsonBody;
        }

        public void setJsonBody(JsonBody jsonBody) {
            this.jsonBody = jsonBody;
        }
    }

    @SuppressWarnings("unused")
    public static class AnnotatedSetter {
        @Nullable
        private JsonBody jsonBody;

        @Nullable
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
        @Nullable
        private JsonBody jsonBody;

        @Nullable
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
        @Nullable
        @RequestBody
        private JsonBody jsonBody;

        @Nullable
        public JsonBody getJsonBody() {
            return jsonBody;
        }

        public void setJsonBody(JsonBody jsonBody) {
            this.jsonBody = jsonBody;
        }
    }

    @SuppressWarnings("unused")
    public static class Validation {
        @SuppressWarnings("MultipleNullnessAnnotations")
        @Nullable
        @NotNull
        @RequestBody
        private JsonBody jsonBody;

        @Nullable
        public JsonBody getJsonBody() {
            return jsonBody;
        }

        public void setJsonBody(JsonBody jsonBody) {
            this.jsonBody = jsonBody;
        }
    }

    @SuppressWarnings("unused")
    public static class Nested {
        @Nullable
        @BeanParameter
        private NestedBean.RequestBodyBean nestedBean;

        @Nullable
        public NestedBean.RequestBodyBean getNestedBean() {
            return nestedBean;
        }

        public void setNestedBean(NestedBean.RequestBodyBean nestedBean) {
            this.nestedBean = nestedBean;
        }
    }

    @SuppressWarnings("unused")
    public static class WebFluxMultipartMultiValueMap {
        @Nullable
        @RequestBody
        private MultiValueMap<String, Part> parts;

        @Nullable
        public MultiValueMap<String, Part> getParts() {
            return parts;
        }

        public void setParts(MultiValueMap<String, Part> parts) {
            this.parts = parts;
        }
    }

    @SuppressWarnings("unused")
    public static class WebFluxMultipartFlux {
        @Nullable
        @RequestBody
        private Flux<Part> parts;

        @Nullable
        public Flux<Part> getParts() {
            return parts;
        }

        public void setParts(Flux<Part> parts) {
            this.parts = parts;
        }
    }
}
