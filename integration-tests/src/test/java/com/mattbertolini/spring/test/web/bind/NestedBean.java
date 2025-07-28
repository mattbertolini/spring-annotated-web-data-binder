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

import com.mattbertolini.spring.web.bind.annotation.CookieParameter;
import com.mattbertolini.spring.web.bind.annotation.FormParameter;
import com.mattbertolini.spring.web.bind.annotation.HeaderParameter;
import com.mattbertolini.spring.web.bind.annotation.PathParameter;
import com.mattbertolini.spring.web.bind.annotation.RequestBody;
import com.mattbertolini.spring.web.bind.annotation.RequestParameter;
import com.mattbertolini.spring.web.bind.annotation.SessionParameter;
import org.springframework.lang.Nullable;

@SuppressWarnings("unused")
public class NestedBean {
    @Nullable
    @RequestParameter("nested_query_param")
    private String queryParam;

    @Nullable
    @FormParameter("nested_form_param")
    private String formData;

    @Nullable
    @CookieParameter("nested_cookie_param")
    private String cookieValue;

    @Nullable
    @HeaderParameter("nested_header_param")
    private String headerValue;

    @Nullable
    @PathParameter("nested_path_param")
    private String pathVariable;

    @Nullable
    @SessionParameter("nested_session_param")
    private String sessionAttribute;

    @Nullable
    public String getQueryParam() {
        return queryParam;
    }

    public void setQueryParam(String queryParam) {
        this.queryParam = queryParam;
    }

    @Nullable
    public String getFormData() {
        return formData;
    }

    public void setFormData(String formData) {
        this.formData = formData;
    }

    @Nullable
    public String getCookieValue() {
        return cookieValue;
    }

    public void setCookieValue(String cookieValue) {
        this.cookieValue = cookieValue;
    }

    @Nullable
    public String getHeaderValue() {
        return headerValue;
    }

    public void setHeaderValue(String headerValue) {
        this.headerValue = headerValue;
    }

    @Nullable
    public String getPathVariable() {
        return pathVariable;
    }

    public void setPathVariable(String pathVariable) {
        this.pathVariable = pathVariable;
    }

    @Nullable
    public String getSessionAttribute() {
        return sessionAttribute;
    }

    public void setSessionAttribute(String sessionAttribute) {
        this.sessionAttribute = sessionAttribute;
    }

    public static class RequestBodyBean {
        @Nullable
        @RequestBody
        private JsonBody requestBody;

        @Nullable
        public JsonBody getRequestBody() {
            return requestBody;
        }

        public void setRequestBody(JsonBody requestBody) {
            this.requestBody = requestBody;
        }
    }
}
