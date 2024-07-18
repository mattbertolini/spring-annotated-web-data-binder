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

package com.mattbertolini.spring.web.bind.docs;

import com.mattbertolini.spring.web.bind.annotation.BeanParameter;
import com.mattbertolini.spring.web.bind.annotation.CookieParameter;
import com.mattbertolini.spring.web.bind.annotation.FormParameter;
import com.mattbertolini.spring.web.bind.annotation.HeaderParameter;
import com.mattbertolini.spring.web.bind.annotation.PathParameter;
import com.mattbertolini.spring.web.bind.annotation.RequestBean;
import com.mattbertolini.spring.web.bind.annotation.RequestContext;
import com.mattbertolini.spring.web.bind.annotation.RequestParameter;
import com.mattbertolini.spring.web.bind.annotation.SessionParameter;
import org.springframework.lang.Nullable;

import java.time.ZoneId;
import java.util.Locale;

// tag::preload[]
@RequestBean
// end::preload[]
// tag::class[]
public class CustomRequestBean {
    // end::class[]

    // Query parameters
    @Nullable
    // tag::queryParam[]
    @RequestParameter("different_name")
    private String queryParam;
    
    // end::queryParam[]

    // Form data
    @Nullable
    // tag::formParam[]
    @FormParameter("form_data")
    private String formData;

    // end::formParam[]

    // HTTP headers
    @Nullable
    // tag::headerParam[]
    @HeaderParameter("X-Custom-Header")
    private String headerValues;

    // end::headerParam[]

    // Spring MVC/WebFlux path variables
    @Nullable
    // tag::pathParam[]
    @PathParameter("pathParam")
    private Integer pathParam;

    // end::pathParam[]

    // HTTP cookie values
    @Nullable
    // tag::cookieParam[]
    @CookieParameter("cookie_value")
    private String cookieValue;

    // end::cookieParam[]

    // HTTP session attributes
    @Nullable
    // tag::sessionParam[]
    @SessionParameter("sessionAttribute")
    private String sessionAttribute;

    // end::sessionParam[]

    // Spring derived request scoped data like locale and time zone
    @Nullable
    @RequestContext
    private Locale locale;

    @Nullable
    @RequestContext
    private ZoneId timeZone;

    // A nested Java bean with additional annotated properties
    @Nullable
    @BeanParameter
    private NestedBean nestedBean;

    @Nullable
    // tag::queryParam[]
    public String getQueryParam() {
        return queryParam;
    }

    public void setQueryParam(String queryParam) {
        this.queryParam = queryParam;
    }
    // end::queryParam[]

    @Nullable
    // tag::formParam[]
    public String getFormData() {
        return formData;
    }

    public void setFormData(String formData) {
        this.formData = formData;
    }
    // end::formParam[]

    @Nullable
    // tag::headerParam[]
    public String getHeaderValues() {
        return headerValues;
    }

    public void setHeaderValues(String headerValues) {
        this.headerValues = headerValues;
    }
    // end::headerParam[]

    @Nullable
    // tag::pathParam[]
    public Integer getPathParam() {
        return pathParam;
    }

    public void setPathParam(Integer pathParam) {
        this.pathParam = pathParam;
    }
    // end::pathParam[]

    @Nullable
    // tag::cookieParam[]
    public String getCookieValue() {
        return cookieValue;
    }

    public void setCookieValue(String cookieValue) {
        this.cookieValue = cookieValue;
    }
    // end::cookieParam[]

    @Nullable
    // tag::sessionParam[]
    public String getSessionAttribute() {
        return sessionAttribute;
    }

    public void setSessionAttribute(String sessionAttribute) {
        this.sessionAttribute = sessionAttribute;
    }
    // end::sessionParam[]

    @Nullable
    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    @Nullable
    public ZoneId getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(ZoneId timeZone) {
        this.timeZone = timeZone;
    }

    @Nullable
    public NestedBean getNestedBean() {
        return nestedBean;
    }

    public void setNestedBean(NestedBean nestedBean) {
        this.nestedBean = nestedBean;
    }
    // tag::class[]
}
// end::class[]
