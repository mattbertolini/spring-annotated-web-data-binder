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
