package com.mattbertolini.spring.web.servlet.mvc.bind;

import org.springframework.beans.PropertyValues;
import org.springframework.lang.Nullable;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;

import java.util.Arrays;
import java.util.List;

public class MockWebDataBinder extends WebDataBinder {
    private boolean bindInvoked = false;
    private boolean validateInvoked = true;
    private PropertyValues pvs;
    private List<Object> validationHints;
    @Nullable
    private BindingResult bindingResult;

    public MockWebDataBinder(@Nullable Object target) {
        super(target);
    }

    public MockWebDataBinder(@Nullable Object target, String objectName) {
        super(target, objectName);
    }

    @Override
    public void construct(ValueResolver valueResolver) {
        super.construct(valueResolver); // TODO
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
