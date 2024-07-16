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

package com.mattbertolini.spring.web.reactive.bind.resolver;

import com.mattbertolini.spring.web.bind.annotation.RequestBody;
import com.mattbertolini.spring.web.bind.introspect.BindingProperty;
import org.springframework.core.MethodParameter;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;
import org.springframework.web.reactive.BindingContext;
import org.springframework.web.reactive.result.method.annotation.AbstractMessageReaderArgumentResolver;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

public class RequestBodyRequestPropertyResolver extends AbstractMessageReaderArgumentResolver implements RequestPropertyResolver {
    public RequestBodyRequestPropertyResolver(List<HttpMessageReader<?>> readers, ReactiveAdapterRegistry registry) {
        super(readers, registry);
    }

    @Override
    public boolean supportsParameter(@NonNull MethodParameter parameter) {
        return false;
    }

    @Override
    public boolean supports(@NonNull BindingProperty bindingProperty) {
        return bindingProperty.hasAnnotation(RequestBody.class);
    }

    @NonNull
    @Override
    public Mono<Object> resolve(@NonNull BindingProperty bindingProperty, @NonNull ServerWebExchange request) {
        RequestBody annotation = bindingProperty.getAnnotation(RequestBody.class);
        Assert.state(annotation != null, "No RequestBody annotation found on type");
        return resolveArgument(bindingProperty.getMethodParameter(), new BindingContext(), request);
    }

    @NonNull
    @Override
    public Mono<Object> resolveArgument(@NonNull MethodParameter parameter, @NonNull BindingContext bindingContext, @NonNull ServerWebExchange exchange) {
        return readBody(parameter, false, bindingContext, exchange);
    }
}
