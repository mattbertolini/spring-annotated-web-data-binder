package com.mattbertolini.spring.test.web.bind;

import com.mattbertolini.spring.web.bind.annotation.RequestBody;

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
}
