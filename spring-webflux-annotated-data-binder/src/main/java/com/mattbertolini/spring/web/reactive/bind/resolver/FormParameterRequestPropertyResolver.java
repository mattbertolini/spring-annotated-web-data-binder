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
package com.mattbertolini.spring.web.reactive.bind.resolver;

import com.mattbertolini.spring.web.bind.annotation.FormParameter;
import com.mattbertolini.spring.web.bind.introspect.BindingProperty;
import org.springframework.http.codec.multipart.FormFieldPart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

public class FormParameterRequestPropertyResolver implements RequestPropertyResolver {
    @Override
    public boolean supports(BindingProperty bindingProperty) {
        FormParameter annotation = bindingProperty.getAnnotation(FormParameter.class);
        return annotation != null && StringUtils.hasText(annotation.value());
    }

    @NonNull
    @Override
    public Mono<Object> resolve(BindingProperty bindingProperty, ServerWebExchange exchange) {
        FormParameter annotation = bindingProperty.getAnnotation(FormParameter.class);
        Objects.requireNonNull(annotation, "No FormParameter annotation found on type");
        return exchange.getMultipartData()
            .filter(multipartData -> multipartData.getFirst(annotation.value()) != null)
            .map(multipartData -> multipartData.get(annotation.value()))
            .map(this::getPartValues)
            .switchIfEmpty(exchange.getFormData()
                .filter(formData -> formData.getFirst(annotation.value()) != null)
                .map(formData -> formData.get(annotation.value())));
    }

    @NonNull
    private Object getPartValues(@NonNull List<Part> parts) {
        List<Object> values = parts.stream()
            .map(value -> value instanceof FormFieldPart formFieldPart ? formFieldPart.value() : value)
            .toList();
        return values.size() == 1 ? values.get(0) : values;
    }
}
