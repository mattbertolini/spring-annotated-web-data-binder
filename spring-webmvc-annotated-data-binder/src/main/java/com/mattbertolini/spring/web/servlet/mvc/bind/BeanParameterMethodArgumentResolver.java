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
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.annotation.ModelAttributeMethodProcessor;

import java.util.List;

public class BeanParameterMethodArgumentResolver extends ModelAttributeMethodProcessor {
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
        List<ResolvedPropertyData> propertyData = introspector.getResolversFor(targetType);
        for (ResolvedPropertyData data : propertyData) {
            String propertyName = data.getPropertyName();
            RequestPropertyResolver resolver = (RequestPropertyResolver) data.getResolver();
            try {
                Object value = resolver.resolve(data.getBindingProperty(), request);
                if (value != null) {
                    propertyValues.add(propertyName, value);
                }
            } catch (Exception e) {
                throw new RequestPropertyBindingException("Unable to resolve property. " + e.getMessage(), e);
            }
        }
        return propertyValues;
    }
}
