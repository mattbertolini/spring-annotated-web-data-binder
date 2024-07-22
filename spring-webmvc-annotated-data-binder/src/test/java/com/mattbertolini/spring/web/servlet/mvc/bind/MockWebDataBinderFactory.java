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
