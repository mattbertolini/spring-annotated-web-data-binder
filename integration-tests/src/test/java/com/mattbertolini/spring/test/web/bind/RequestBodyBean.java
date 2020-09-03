package com.mattbertolini.spring.test.web.bind;

import com.mattbertolini.spring.web.bind.annotation.BeanParameter;
import com.mattbertolini.spring.web.bind.annotation.RequestBody;

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
}
