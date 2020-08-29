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

import com.mattbertolini.spring.web.bind.PropertyResolutionException;
import com.mattbertolini.spring.web.bind.annotation.RequestBody;
import com.mattbertolini.spring.web.bind.introspect.BindingProperty;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

import java.util.List;

public class RequestBodyRequestPropertyResolver implements RequestPropertyResolver {
    private final RequestResponseBodyMethodProcessor processor;

    public RequestBodyRequestPropertyResolver(List<HttpMessageConverter<?>> messageConverters) {
        this(new RequestResponseBodyMethodProcessor(messageConverters));
    }

    /**
     * Visible for testing purposes only.
     */
    RequestBodyRequestPropertyResolver(@NonNull RequestResponseBodyMethodProcessor processor) {
        this.processor = processor;
    }

    @Override
    public boolean supports(@NonNull BindingProperty bindingProperty) {
        return bindingProperty.hasAnnotation(RequestBody.class);
    }

    @Override
    public Object resolve(@NonNull BindingProperty bindingProperty, @NonNull NativeWebRequest request) {
        RequestBody annotation = bindingProperty.getAnnotation(RequestBody.class);
        Assert.state(annotation != null, "No RequestBody annotation found on type");
        try {
            return processor.resolveArgument(bindingProperty.getMethodParameter(), null, request, null);
        } catch (Exception e) {
            throw new PropertyResolutionException("Error resolving request body.", e);
        }
    }
}
