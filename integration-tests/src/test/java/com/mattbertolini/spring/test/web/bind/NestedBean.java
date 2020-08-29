package com.mattbertolini.spring.test.web.bind;

import com.mattbertolini.spring.web.bind.annotation.CookieParameter;
import com.mattbertolini.spring.web.bind.annotation.FormParameter;
import com.mattbertolini.spring.web.bind.annotation.HeaderParameter;
import com.mattbertolini.spring.web.bind.annotation.PathParameter;
import com.mattbertolini.spring.web.bind.annotation.RequestBody;
import com.mattbertolini.spring.web.bind.annotation.RequestParameter;
import com.mattbertolini.spring.web.bind.annotation.SessionParameter;

@SuppressWarnings("unused")
public class NestedBean {
    @RequestParameter("nested_query_param")
    private String queryParam;

    @FormParameter("nested_form_param")
    private String formData;

    @CookieParameter("nested_cookie_param")
    private String cookieValue;

    @HeaderParameter("nested_header_param")
    private String headerValue;

    @PathParameter("nested_path_param")
    private String pathVariable;

    @SessionParameter("nested_session_param")
    private String sessionAttribute;

    public String getQueryParam() {
        return queryParam;
    }

    public void setQueryParam(String queryParam) {
        this.queryParam = queryParam;
    }

    public String getFormData() {
        return formData;
    }

    public void setFormData(String formData) {
        this.formData = formData;
    }

    public String getCookieValue() {
        return cookieValue;
    }

    public void setCookieValue(String cookieValue) {
        this.cookieValue = cookieValue;
    }

    public String getHeaderValue() {
        return headerValue;
    }

    public void setHeaderValue(String headerValue) {
        this.headerValue = headerValue;
    }

    public String getPathVariable() {
        return pathVariable;
    }

    public void setPathVariable(String pathVariable) {
        this.pathVariable = pathVariable;
    }

    public String getSessionAttribute() {
        return sessionAttribute;
    }

    public void setSessionAttribute(String sessionAttribute) {
        this.sessionAttribute = sessionAttribute;
    }

    public static class RequestBodyBean {
        @RequestBody
        private JsonBody requestBody;

        public JsonBody getRequestBody() {
            return requestBody;
        }

        public void setRequestBody(JsonBody requestBody) {
            this.requestBody = requestBody;
        }
    }
}
