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
public class DirectFieldAccessBean {
    @Nullable
    @CookieParameter("cookie_parameter")
    private String cookieParameter;

    @Nullable
    @FormParameter("form_parameter")
    private String formParameter;

    @Nullable
    @HeaderParameter("header_parameter")
    private String headerParameter;

    @Nullable
    @PathParameter("path_parameter")
    private String pathParameter;

    @Nullable
    @RequestParameter("request_parameter")
    private String requestParameter;

    @Nullable
    @SessionParameter("session_parameter")
    private String sessionParameter;

    @Nullable
    public String getCookieParameter() {
        return cookieParameter;
    }

    @Nullable
    public String getFormParameter() {
        return formParameter;
    }

    @Nullable
    public String getHeaderParameter() {
        return headerParameter;
    }

    @Nullable
    public String getPathParameter() {
        return pathParameter;
    }

    @Nullable
    public String getRequestParameter() {
        return requestParameter;
    }

    @Nullable
    public String getSessionParameter() {
        return sessionParameter;
    }

    public static class RequestBodyBean {
        @Nullable
        @RequestBody
        private JsonBody requestBody;

        @Nullable
        public JsonBody getRequestBody() {
            return requestBody;
        }
    }
}
