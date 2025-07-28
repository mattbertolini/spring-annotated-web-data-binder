/*
 * Copyright 2024 the original author or authors.
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

import com.mattbertolini.spring.web.bind.annotation.SessionParameter;
import com.mattbertolini.spring.web.bind.introspect.BindingProperty;
import org.springframework.lang.Nullable;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;

import java.util.Objects;

public class SessionParameterRequestPropertyResolver implements RequestPropertyResolver {
    @Override
    public boolean supports(BindingProperty bindingProperty) {
        return bindingProperty.hasAnnotation(SessionParameter.class);
    }

    @Override
    @Nullable
    public Object resolve(BindingProperty bindingProperty, NativeWebRequest request) {
        SessionParameter annotation = bindingProperty.getAnnotation(SessionParameter.class);
        Objects.requireNonNull(annotation, "No SessionParameter annotation found on type");
        return request.getAttribute(annotation.value(), RequestAttributes.SCOPE_SESSION);
    }
}
