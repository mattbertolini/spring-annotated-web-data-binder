package com.mattbertolini.spring.web.reactive.bind;

import org.springframework.beans.PropertyValues;
import org.springframework.lang.Nullable;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.support.WebExchangeDataBinder;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

public class MockWebExchangeDataBinder extends WebExchangeDataBinder {
    private boolean bindInvoked = false;
    private boolean validateInvoked = true;
    private PropertyValues pvs;
    private List<Object> validationHints;
    private BindingResult bindingResult;

    public MockWebExchangeDataBinder(@Nullable Object target) {
        super(target);
    }

    @Override
    public Mono<Void> construct(ServerWebExchange exchange) {
        return super.construct(exchange); // TODO
    }

    @Override
    public void bind(PropertyValues pvs) {
        this.pvs = pvs;
        bindInvoked = true;
    }

    @Override
    public void validate() {
        validateInvoked = true;
    }

    @Override
    public void validate(Object... validationHints) {
        this.validationHints = Arrays.asList(validationHints);
        validateInvoked = true;
    }

    @Override
    public BindingResult getBindingResult() {
        if (bindingResult == null) {
            return super.getBindingResult();
        }
        return bindingResult;
    }

    public void setBindingResult(BindingResult bindingResult) {
        this.bindingResult = bindingResult;
    }

    public boolean isBindInvoked() {
        return bindInvoked;
    }

    public boolean isValidateInvoked() {
        return validateInvoked;
    }

    public List<Object> getValidationHints() {
        return validationHints;
    }

    public PropertyValues getPropertyValues() {
        return pvs;
    }
}
