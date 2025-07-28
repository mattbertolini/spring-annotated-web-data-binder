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

import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValues;
import org.springframework.lang.Nullable;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;

import java.util.ArrayList;
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
        pvs = new MutablePropertyValues();
        validationHints = new ArrayList<>();
    }

    public MockWebDataBinder(@Nullable Object target, String objectName) {
        super(target, objectName);
        pvs = new MutablePropertyValues();
        validationHints = new ArrayList<>();
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
