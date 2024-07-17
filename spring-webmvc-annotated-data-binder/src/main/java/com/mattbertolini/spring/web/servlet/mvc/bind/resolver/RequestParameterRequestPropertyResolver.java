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

import com.mattbertolini.spring.web.bind.PropertyResolutionException;
import com.mattbertolini.spring.web.bind.annotation.RequestParameter;
import com.mattbertolini.spring.web.bind.introspect.BindingProperty;
import com.mattbertolini.spring.web.bind.resolver.AbstractNamedRequestPropertyResolver;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.multipart.support.MultipartResolutionDelegate;

import java.util.Objects;

public class RequestParameterRequestPropertyResolver extends AbstractNamedRequestPropertyResolver<NativeWebRequest, Object>
    implements RequestPropertyResolver {

    @Override
    public boolean supports(BindingProperty bindingProperty) {
        RequestParameter annotation = bindingProperty.getAnnotation(RequestParameter.class);
        return annotation != null && StringUtils.hasText(annotation.value());
    }

    @Override
    @Nullable
    protected Object resolveWithName(BindingProperty bindingProperty, String name, NativeWebRequest request) {
        HttpServletRequest servletRequest = request.getNativeRequest(HttpServletRequest.class);
        if (servletRequest != null) {
            try {
                Object value = MultipartResolutionDelegate.resolveMultipartArgument(name, bindingProperty.getMethodParameter(), servletRequest);
                if (MultipartResolutionDelegate.UNRESOLVABLE != value) {
                    return value;
                }
            } catch (Exception e) {
                throw new PropertyResolutionException("Exception resolving multipart argument", e);
            }
        }
        
        return request.getParameterValues(name);
    }
    
    @Override
    protected String getName(BindingProperty bindingProperty) {
        RequestParameter annotation = bindingProperty.getAnnotation(RequestParameter.class);
        Objects.requireNonNull(annotation, "No RequestParameter annotation found on type");
        return annotation.value();
    }
}
