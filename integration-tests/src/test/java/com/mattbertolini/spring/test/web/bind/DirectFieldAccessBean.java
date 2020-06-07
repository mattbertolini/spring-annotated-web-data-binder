/*
 * Copyright 2019-2020 the original author or authors.
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
import com.mattbertolini.spring.web.bind.annotation.RequestParameter;
import com.mattbertolini.spring.web.bind.annotation.SessionParameter;

@SuppressWarnings("unused")
public class DirectFieldAccessBean {
    @CookieParameter("cookie_parameter")
    private String cookieParameter;

    @FormParameter("form_parameter")
    private String formParameter;
    
    @HeaderParameter("header_parameter")
    private String headerParameter;

    @PathParameter("path_parameter")
    private String pathParameter;
    
    @RequestParameter("request_parameter")
    private String requestParameter;

    @SessionParameter("session_parameter")
    private String sessionParameter;

    public String getCookieParameter() {
        return cookieParameter;
    }

    public String getFormParameter() {
        return formParameter;
    }

    public String getHeaderParameter() {
        return headerParameter;
    }

    public String getPathParameter() {
        return pathParameter;
    }

    public String getRequestParameter() {
        return requestParameter;
    }

    public String getSessionParameter() {
        return sessionParameter;
    }
}
