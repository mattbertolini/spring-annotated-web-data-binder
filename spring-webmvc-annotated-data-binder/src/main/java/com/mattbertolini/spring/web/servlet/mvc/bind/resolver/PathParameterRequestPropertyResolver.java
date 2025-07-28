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
package com.mattbertolini.spring.web.servlet.mvc.bind.resolver;

import com.mattbertolini.spring.web.bind.annotation.PathParameter;
import com.mattbertolini.spring.web.bind.introspect.BindingProperty;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Map;
import java.util.Objects;

public class PathParameterRequestPropertyResolver implements RequestPropertyResolver {

    @Override
    public boolean supports(BindingProperty bindingProperty) {
        PathParameter annotation = bindingProperty.getAnnotation(PathParameter.class);
        return annotation != null && StringUtils.hasText(annotation.value());
    }

    @SuppressWarnings("unchecked")
    @Override
    @Nullable
    public Object resolve(BindingProperty bindingProperty, NativeWebRequest request) {
        PathParameter annotation = bindingProperty.getAnnotation(PathParameter.class);
        Objects.requireNonNull(annotation, "No PathParameter annotation found on type");

        Map<String, String> uriTemplateVariables = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);
        if (uriTemplateVariables == null) {
            return null;
        }
        return uriTemplateVariables.get(annotation.value());
    }
}
