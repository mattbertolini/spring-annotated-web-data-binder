package com.mattbertolini.spring.web.reactive.bind;

import com.uber.nullaway.annotations.Initializer;
import org.springframework.core.ResolvableType;
import org.springframework.format.support.FormattingConversionServiceFactoryBean;
import org.springframework.lang.Nullable;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.support.BindingAwareConcurrentModel;
import org.springframework.web.bind.support.WebExchangeDataBinder;
import org.springframework.web.reactive.BindingContext;
import org.springframework.web.server.ServerWebExchange;

public class MockBindingContext extends BindingContext {
    private final BindingAwareConcurrentModel model = new BindingAwareConcurrentModel();
    private MockWebExchangeDataBinder dataBinder;
    @Nullable
    private BindingResult bindingResult;

    @Override
    @Initializer
    public WebExchangeDataBinder createDataBinder(ServerWebExchange exchange, @Nullable Object target, String name) {
        dataBinder = new MockWebExchangeDataBinder(target);

        if (bindingResult != null) {
            dataBinder.setBindingResult(bindingResult);
        }

        FormattingConversionServiceFactoryBean conversionServiceFactoryBean = new FormattingConversionServiceFactoryBean();
        conversionServiceFactoryBean.afterPropertiesSet();
        dataBinder.setConversionService(conversionServiceFactoryBean.getObject());

        return dataBinder;
    }

    @Override
    @Initializer
    public WebExchangeDataBinder createDataBinder(ServerWebExchange exchange, @Nullable Object target, String name, ResolvableType targetType) {
        dataBinder = new MockWebExchangeDataBinder(target);

        if (target == null) {
            dataBinder.setTargetType(targetType);
        }

        if (bindingResult != null) {
            dataBinder.setBindingResult(bindingResult);
        }

        FormattingConversionServiceFactoryBean conversionServiceFactoryBean = new FormattingConversionServiceFactoryBean();
        conversionServiceFactoryBean.afterPropertiesSet();
        dataBinder.setConversionService(conversionServiceFactoryBean.getObject());

        return dataBinder;
    }

    @Override
    public Model getModel() {
        return model;
    }

    public MockWebExchangeDataBinder getDataBinder() {
        return dataBinder;
    }

    public void setBindingResult(BindingResult bindingResult) {
        this.bindingResult = bindingResult;
    }
}
