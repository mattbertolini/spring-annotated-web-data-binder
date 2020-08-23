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

package com.mattbertolini.spring.web.reactive.bind;

import com.mattbertolini.spring.web.bind.RequestPropertyBindingException;
import com.mattbertolini.spring.web.bind.annotation.BeanParameter;
import com.mattbertolini.spring.web.bind.introspect.AnnotatedRequestBeanIntrospector;
import com.mattbertolini.spring.web.bind.introspect.BindingProperty;
import com.mattbertolini.spring.web.bind.introspect.ResolvedPropertyData;
import com.mattbertolini.spring.web.reactive.bind.resolver.RequestPropertyResolver;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.core.MethodParameter;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;
import org.springframework.web.bind.support.WebExchangeDataBinder;
import org.springframework.web.reactive.result.method.annotation.ModelAttributeMethodArgumentResolver;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class BeanParameterMethodArgumentResolver extends ModelAttributeMethodArgumentResolver {

    private final AnnotatedRequestBeanIntrospector introspector;

    public BeanParameterMethodArgumentResolver(
        @NonNull ReactiveAdapterRegistry adapterRegistry,
        @NonNull AnnotatedRequestBeanIntrospector introspector) {
        super(adapterRegistry, false);
        this.introspector = introspector;
    }

    @Override
    public boolean supportsParameter(@NonNull MethodParameter parameter) {
        return parameter.hasParameterAnnotation(BeanParameter.class) && !BeanUtils.isSimpleProperty(parameter.getParameterType());
    }

    @Override
    @NonNull
    protected Mono<Void> bindRequestParameters(WebExchangeDataBinder binder, @NonNull ServerWebExchange exchange) {
        Assert.state(binder.getTarget() != null, "WebExchangeDataBinder must have a target object");
        List<ResolvedPropertyData> propertyData = introspector.getResolversFor(binder.getTarget().getClass());
        return getValuesToBind(propertyData, exchange)
            .map(MutablePropertyValues::new)
            .doOnNext(binder::bind)
            .then();
    }

    @NonNull
    private Mono<Map<String, Object>> getValuesToBind(@NonNull List<ResolvedPropertyData> propertyData, @NonNull ServerWebExchange exchange) {
        return Flux.fromIterable(propertyData).collectMap(ResolvedPropertyData::getPropertyName, data -> {
            BindingProperty bindingProperty = data.getBindingProperty();
            RequestPropertyResolver resolver = (RequestPropertyResolver) data.getResolver();
            // TODO: Not sure how to do this without the block. I would love some suggestions.
            //noinspection ReactiveStreamsNullableInLambdaInTransform
            return resolver.resolve(bindingProperty, exchange).toProcessor().block();
        }).onErrorMap(e -> new RequestPropertyBindingException("Unable to resolve property. " + e.getMessage(), e))
            .doOnSuccess(valuesMap -> valuesMap.values().removeIf(Objects::isNull));
    }
}
