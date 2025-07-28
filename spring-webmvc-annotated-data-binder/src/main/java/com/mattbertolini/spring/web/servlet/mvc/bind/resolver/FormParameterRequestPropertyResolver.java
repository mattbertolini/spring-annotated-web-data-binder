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

import com.mattbertolini.spring.web.bind.annotation.FormParameter;
import com.mattbertolini.spring.web.bind.introspect.BindingProperty;
import org.springframework.util.StringUtils;

import java.util.Objects;

public class FormParameterRequestPropertyResolver extends RequestParameterRequestPropertyResolver {
    @Override
    public boolean supports(BindingProperty bindingProperty) {
        FormParameter annotation = bindingProperty.getAnnotation(FormParameter.class);
        return annotation != null && StringUtils.hasText(annotation.value());
    }
    
    @Override
    protected String getName(BindingProperty bindingProperty) {
        FormParameter annotation = bindingProperty.getAnnotation(FormParameter.class);
        Objects.requireNonNull(annotation, "No FormParameter annotation found on type");
        return annotation.value();
    }
}
