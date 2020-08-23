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

package com.mattbertolini.spring.web.servlet.mvc.bind.resolver;

import com.mattbertolini.spring.web.bind.annotation.RequestContext;
import com.mattbertolini.spring.web.bind.introspect.BindingProperty;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.http.HttpMethod;
import org.springframework.lang.NonNull;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.ZoneId;
import java.util.Locale;
import java.util.TimeZone;

public class RequestContextRequestPropertyResolver implements RequestPropertyResolver {
    @Override
    public boolean supports(@NonNull BindingProperty bindingProperty) {
        Class<?> type = bindingProperty.getType();
        return bindingProperty.hasAnnotation(RequestContext.class) && (
            WebRequest.class.isAssignableFrom(type) ||
                ServletRequest.class.isAssignableFrom(type) ||
                HttpSession.class.isAssignableFrom(type) ||
                HttpMethod.class.isAssignableFrom(type)  ||
                Locale.class.isAssignableFrom(type) ||
                TimeZone.class.isAssignableFrom(type) ||
                ZoneId.class.isAssignableFrom(type)
        );
    }

    @Override
    public Object resolve(@NonNull TypeDescriptor typeDescriptor, @NonNull BindingProperty bindingProperty, @NonNull NativeWebRequest request) {
        Class<?> type = typeDescriptor.getType();
        if (WebRequest.class.isAssignableFrom(type)) {
            return request;
        }

        HttpServletRequest servletRequest = request.getNativeRequest(HttpServletRequest.class);
        if (servletRequest == null) {
            throw new IllegalStateException("An HttpServletRequest is required for this resolver to work.");
        }

        if (ServletRequest.class.isAssignableFrom(type)) {
            return servletRequest;
        } else if (HttpSession.class.isAssignableFrom(type)) {
            // Not creating a session here.
            return servletRequest.getSession(false);
        } else if (HttpMethod.class.isAssignableFrom(type)) {
            return HttpMethod.resolve(servletRequest.getMethod());
        } else if (Locale.class.isAssignableFrom(type)) {
            return RequestContextUtils.getLocale(servletRequest);
        } else if(TimeZone.class.isAssignableFrom(type)) {
            TimeZone timeZone = RequestContextUtils.getTimeZone(servletRequest);
            return timeZone != null ? timeZone : TimeZone.getDefault();
        } else if(ZoneId.class.isAssignableFrom(type)) {
            TimeZone timeZone = RequestContextUtils.getTimeZone(servletRequest);
            if (timeZone == null) {
                return ZoneId.systemDefault();
            }
            return timeZone.toZoneId();
        }

        // This should not be thrown if the supports method is correct and in line with this method.
        throw new UnsupportedOperationException("Unable to resolve type " + type);
    }
}
