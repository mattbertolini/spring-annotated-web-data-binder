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
package com.mattbertolini.spring.web.servlet.mvc.bind;

import com.uber.nullaway.annotations.Initializer;
import org.springframework.core.ResolvableType;
import org.springframework.format.support.FormattingConversionServiceFactoryBean;
import org.springframework.lang.Nullable;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;

public class MockWebDataBinderFactory implements WebDataBinderFactory {
    private MockWebDataBinder binder;
    @Nullable
    private BindingResult bindingResult;

    @Initializer
    @Override
    public WebDataBinder createBinder(NativeWebRequest webRequest, @Nullable Object target, String objectName, ResolvableType targetType) throws Exception {
        binder = new MockWebDataBinder(target, objectName);
        if (target == null) {
            binder.setTargetType(targetType);
        }

        if (bindingResult != null) {
            binder.setBindingResult(bindingResult);
        }

        FormattingConversionServiceFactoryBean conversionServiceFactoryBean = new FormattingConversionServiceFactoryBean();
        conversionServiceFactoryBean.afterPropertiesSet();
        binder.setConversionService(conversionServiceFactoryBean.getObject());

        return binder;
    }

    @Initializer
    @Override
    public WebDataBinder createBinder(NativeWebRequest webRequest, @Nullable Object target, String objectName) throws Exception {
        binder = new MockWebDataBinder(target, objectName);

        if (bindingResult != null) {
            binder.setBindingResult(bindingResult);
        }

        FormattingConversionServiceFactoryBean conversionServiceFactoryBean = new FormattingConversionServiceFactoryBean();
        conversionServiceFactoryBean.afterPropertiesSet();
        binder.setConversionService(conversionServiceFactoryBean.getObject());

        return binder;
    }

    public void setBindingResult(BindingResult bindingResult) {
        this.bindingResult = bindingResult;
    }

    public MockWebDataBinder getBinder() {
        return binder;
    }
}
