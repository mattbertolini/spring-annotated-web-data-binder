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

import com.mattbertolini.spring.web.bind.annotation.HeaderParameter;
import com.mattbertolini.spring.web.bind.introspect.BindingProperty;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.NativeWebRequest;

/**
 * @see NativeWebRequest#getHeaderValues(String)
 */
public class HeaderParameterRequestPropertyResolver implements RequestPropertyResolver {

    @Override
    public boolean supports(@NonNull BindingProperty bindingProperty) {
        HeaderParameter annotation = bindingProperty.getAnnotation(HeaderParameter.class);
        return annotation != null && StringUtils.hasText(annotation.value());
    }

    @Override
    public Object resolve(@NonNull BindingProperty bindingProperty, @NonNull NativeWebRequest request) {
        HeaderParameter annotation = bindingProperty.getAnnotation(HeaderParameter.class);
        Assert.state(annotation != null, "No HeaderParameter annotation found on type");
        return request.getHeaderValues(annotation.value());
    }
}
