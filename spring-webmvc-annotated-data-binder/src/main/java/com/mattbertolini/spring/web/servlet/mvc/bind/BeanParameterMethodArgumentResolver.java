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

package com.mattbertolini.spring.web.servlet.mvc.bind;

import com.mattbertolini.spring.web.bind.RequestPropertyBindingException;
import com.mattbertolini.spring.web.bind.annotation.BeanParameter;
import com.mattbertolini.spring.web.bind.introspect.AnnotatedRequestBeanIntrospector;
import com.mattbertolini.spring.web.bind.introspect.ResolvedPropertyData;
import com.mattbertolini.spring.web.bind.support.MapValueResolver;
import com.mattbertolini.spring.web.servlet.mvc.bind.resolver.RequestPropertyResolver;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.core.MethodParameter;
import org.springframework.util.Assert;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.annotation.ModelAttributeMethodProcessor;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class BeanParameterMethodArgumentResolver extends ModelAttributeMethodProcessor {
    private static final String BIND_VALUES_ATTRIBUTE_KEY = BeanParameterMethodArgumentResolver.class.getName() + ".bindValues";
    private final AnnotatedRequestBeanIntrospector introspector;

    public BeanParameterMethodArgumentResolver(AnnotatedRequestBeanIntrospector introspector) {
        super(false);
        this.introspector = introspector;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(BeanParameter.class) && !BeanUtils.isSimpleProperty(parameter.getParameterType());
    }

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return false;
    }

    @Override
    protected void constructAttribute(WebDataBinder binder, NativeWebRequest request) {
        Assert.state(binder.getTargetType() != null, "WebDataBinder must have a target type");
        Map<String, Object> valuesToBind = memoizedGetValuesToBind(Objects.requireNonNull(binder.getTargetType().getRawClass()), request);
        binder.construct(new MapValueResolver(valuesToBind));
    }

    @Override
    protected void bindRequestParameters(WebDataBinder binder, NativeWebRequest request) {
        Assert.state(binder.getTarget() != null, "WebDataBinder must have a target object");
        Map<String, Object> valuesToBind = memoizedGetValuesToBind(binder.getTarget().getClass(), request);
        binder.bind(new MutablePropertyValues(valuesToBind));
        request.removeAttribute(BIND_VALUES_ATTRIBUTE_KEY, RequestAttributes.SCOPE_REQUEST);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> memoizedGetValuesToBind(Class<?> targetType, NativeWebRequest request) {
        /* Nullable */ Map<String, Object> memoizedValues = (Map<String, Object>) request.getAttribute(BIND_VALUES_ATTRIBUTE_KEY, NativeWebRequest.SCOPE_REQUEST);
        if (memoizedValues != null) {
            return memoizedValues;
        }
        Map<String, Object> valuesToBind = getValuesToBind(targetType, request);
        request.setAttribute(BIND_VALUES_ATTRIBUTE_KEY, valuesToBind, NativeWebRequest.SCOPE_REQUEST);
        return valuesToBind;
    }

    private Map<String, Object> getValuesToBind(Class<?> targetType, NativeWebRequest request) {
        Map<String, Object> values = new HashMap<>();
        Collection<ResolvedPropertyData> propertyData = introspector.getResolversFor(targetType);
        for (ResolvedPropertyData data : propertyData) {
            RequestPropertyResolver resolver = (RequestPropertyResolver) data.getResolver();
            try {
                Object value = resolver.resolve(data.getBindingProperty(), request);
                if (value != null) {
                    String propertyName = data.getPropertyName();
                    values.put(propertyName, value);
                }
            } catch (Exception e) {
                throw new RequestPropertyBindingException("Unable to resolve property. " + e.getMessage(), e);
            }
        }
        return values;
    }
}
