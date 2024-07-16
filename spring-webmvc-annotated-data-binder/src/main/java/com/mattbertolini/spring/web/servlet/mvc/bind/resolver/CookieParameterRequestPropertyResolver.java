/*
 * Copyright 2024 the original author or authors.
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

package com.mattbertolini.spring.web.servlet.mvc.bind.resolver;

import com.mattbertolini.spring.web.bind.annotation.CookieParameter;
import com.mattbertolini.spring.web.bind.introspect.BindingProperty;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.util.WebUtils;

public class CookieParameterRequestPropertyResolver implements RequestPropertyResolver {
    @Override
    public boolean supports(BindingProperty bindingProperty) {
        return bindingProperty.hasAnnotation(CookieParameter.class);
    }

    @Override
    @Nullable
    public Object resolve(BindingProperty bindingProperty, NativeWebRequest request) {
        HttpServletRequest servletRequest = request.getNativeRequest(HttpServletRequest.class);
        Assert.state(servletRequest != null, "A HttpServletRequest is required for this resolver and none found.");
        CookieParameter annotation = bindingProperty.getAnnotation(CookieParameter.class);
        Assert.state(annotation != null, "No CookieParameter annotation found on type");
        Cookie cookie = WebUtils.getCookie(servletRequest, annotation.value());
        if (cookie == null) {
            return null;
        }
        if (Cookie.class == bindingProperty.getObjectType()) {
            return cookie;
        }
        return cookie.getValue();
    }
}
