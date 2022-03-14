/*
 * Copyright 2019-2022 the original author or authors.
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

package com.mattbertolini.spring.web.servlet.mvc.bind;

import com.mattbertolini.spring.web.bind.RequestPropertyBindingException;
import com.mattbertolini.spring.web.bind.annotation.BeanParameter;
import com.mattbertolini.spring.web.bind.introspect.AnnotatedRequestBeanIntrospector;
import com.mattbertolini.spring.web.bind.introspect.ResolvedPropertyData;
import com.mattbertolini.spring.web.servlet.mvc.bind.resolver.RequestPropertyResolver;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValues;
import org.springframework.core.MethodParameter;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.ModelAttributeMethodProcessor;

import java.util.Collection;
import java.util.Map;

public class BeanParameterMethodArgumentResolver extends ModelAttributeMethodProcessor {
    private static final String INTROSPECTOR_TARGET_CLASS = BeanParameterMethodArgumentResolver.class +
        ".INTROSPECTOR_TARGET_CLASS";

    private final AnnotatedRequestBeanIntrospector introspector;

    public BeanParameterMethodArgumentResolver(@NonNull AnnotatedRequestBeanIntrospector introspector) {
        super(false);
        this.introspector = introspector;
    }

    @Override
    public boolean supportsParameter(@NonNull MethodParameter parameter) {
        return parameter.hasParameterAnnotation(BeanParameter.class) && !BeanUtils.isSimpleProperty(parameter.getParameterType());
    }

    @Override
    public boolean supportsReturnType(@NonNull MethodParameter returnType) {
        return false;
    }

    @Override
    protected void bindRequestParameters(@NonNull WebDataBinder binder, @NonNull NativeWebRequest request) {
        Assert.state(binder.getTarget() != null, "WebDataBinder must have a target object");
        PropertyValues propertyValues = makePropertyValues(binder.getTarget().getClass(), request);
        binder.bind(propertyValues);
    }

    @NonNull
    private PropertyValues makePropertyValues(@NonNull Class<?> targetType, @NonNull NativeWebRequest request) {
        MutablePropertyValues propertyValues = new MutablePropertyValues();
        Collection<ResolvedPropertyData> propertyData = introspector.getResolversFor(targetType);
        for (ResolvedPropertyData data : propertyData) {
            RequestPropertyResolver resolver = (RequestPropertyResolver) data.getResolver();
            try {
                Object value = resolver.resolve(data.getBindingProperty(), request);
                if (value != null) {
                    String propertyName = data.getPropertyName();
                    propertyValues.add(propertyName, value);
                }
            } catch (Exception e) {
                throw new RequestPropertyBindingException("Unable to resolve property. " + e.getMessage(), e);
            }
        }
        return propertyValues;
    }

    @Override
    @NonNull
    protected Object createAttribute(@NonNull String attributeName, MethodParameter parameter, @NonNull WebDataBinderFactory binderFactory, NativeWebRequest webRequest) throws Exception {
        try {
            MethodParameter nestedParameter = parameter.nestedIfOptional();
            Class<?> clazz = nestedParameter.getNestedParameterType();
            webRequest.setAttribute(INTROSPECTOR_TARGET_CLASS, clazz, WebRequest.SCOPE_REQUEST);
            return super.createAttribute(attributeName, parameter, binderFactory, webRequest);
        } finally {
            webRequest.removeAttribute(INTROSPECTOR_TARGET_CLASS, WebRequest.SCOPE_REQUEST);
        }
    }

    @Override
    public Object resolveConstructorArgument(@NonNull String paramName,@NonNull Class<?> paramType, NativeWebRequest request) throws Exception {
        Class<?> clazz = (Class<?>) request.getAttribute(INTROSPECTOR_TARGET_CLASS, WebRequest.SCOPE_REQUEST);
        if (clazz == null) {
            return super.resolveConstructorArgument(paramName, paramType, request);
        }

        final Map<String, ResolvedPropertyData> resolvers = introspector.getResolverMapFor(clazz);
        final ResolvedPropertyData resolvedPropertyData = resolvers.get(paramName);
        final RequestPropertyResolver resolver = (RequestPropertyResolver) resolvedPropertyData.getResolver();
        return resolver.resolve(resolvedPropertyData.getBindingProperty(), request);
    }
}
