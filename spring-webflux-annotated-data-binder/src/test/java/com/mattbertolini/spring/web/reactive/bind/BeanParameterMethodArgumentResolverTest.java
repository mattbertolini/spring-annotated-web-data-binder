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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.PropertyValues;
import org.springframework.core.MethodParameter;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.format.support.FormattingConversionServiceFactoryBean;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.validation.support.BindingAwareConcurrentModel;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.bind.support.WebExchangeDataBinder;
import org.springframework.web.reactive.BindingContext;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BeanParameterMethodArgumentResolverTest {
    private BeanParameterMethodArgumentResolver resolver;

    private ServerWebExchange exchange;
    private AnnotatedRequestBeanIntrospector introspector;
    private BindingContext bindingContext;

    @BeforeEach
    void setUp() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/irrelevant").build();
        exchange = MockServerWebExchange.from(request);
        introspector = mock(AnnotatedRequestBeanIntrospector.class);
        bindingContext = mock(BindingContext.class);
        when(bindingContext.getModel()).thenReturn(new BindingAwareConcurrentModel());
        ReactiveAdapterRegistry registry = new ReactiveAdapterRegistry();
        resolver = new BeanParameterMethodArgumentResolver(registry, introspector);
    }

    @Test
    void supportsParameterReturnsTrueWhenMethodArgumentIsAnnotated() throws Exception {
        MethodParameter methodParameter = createMethodParameter("anAnnotatedMethod", ABeanClass.class);
        boolean result = resolver.supportsParameter(methodParameter);
        assertThat(result).isTrue();
    }

    @Test
    void supportsParameterReturnsFalseWhenArgumentIsSimpleType() throws Exception {
        MethodParameter methodParameter = createMethodParameter("annotatedSimpleType", int.class);
        boolean result = resolver.supportsParameter(methodParameter);
        assertThat(result).isFalse();
    }

    @Test
    void supportsParameterReturnsFalseWhenAnnotationIsMissing() throws Exception {
        MethodParameter methodParameter = createMethodParameter("missingAnnotation", ABeanClass.class);
        boolean result = resolver.supportsParameter(methodParameter);
        assertThat(result).isFalse();
    }

    @Test
    void resolvesPropertyValues() throws Exception {
        List<ResolvedPropertyData> propertyData = Arrays.asList(
            new ResolvedPropertyData("propertyOne", BindingProperty.forPropertyDescriptor(new PropertyDescriptor("propertyOne", ABeanClass.class)), MockRequestPropertyResolver.value("expected")),
            new ResolvedPropertyData("propertyTwo", BindingProperty.forPropertyDescriptor(new PropertyDescriptor("propertyTwo", ABeanClass.class)), MockRequestPropertyResolver.value(42))
        );

        MethodParameter methodParameter = createMethodParameter("anAnnotatedMethod", ABeanClass.class);

        StubWebExchangeDataBinder dataBinder = new StubWebExchangeDataBinder(new ABeanClass());
        when(bindingContext.createDataBinder(eq(exchange), any(ABeanClass.class), anyString())).thenReturn(dataBinder);

        when(introspector.getResolversFor(ABeanClass.class)).thenReturn(propertyData);
        Mono<Object> objectMono = resolver.resolveArgument(methodParameter, bindingContext, exchange);
        objectMono.block();

        PropertyValues propertyValues = dataBinder.getPropertyValues();
        assertThat(propertyValues.contains("propertyOne")).isTrue();
        assertThat(propertyValues.contains("propertyTwo")).isTrue();

        assertThat(propertyValues.getPropertyValue("propertyOne"))
            .isNotNull()
            .extracting(PropertyValue::getValue).isEqualTo("expected");
        assertThat(propertyValues.getPropertyValue("propertyTwo"))
            .isNotNull()
            .extracting(PropertyValue::getValue).isEqualTo(42);
    }

    @Test
    void resolvesOnlyFoundPropertyValues() throws Exception {
        List<ResolvedPropertyData> propertyData = Arrays.asList(
            new ResolvedPropertyData("propertyOne", BindingProperty.forPropertyDescriptor(new PropertyDescriptor("propertyOne", ABeanClass.class)), MockRequestPropertyResolver.noValueFound()),
            new ResolvedPropertyData("propertyTwo", BindingProperty.forPropertyDescriptor(new PropertyDescriptor("propertyTwo", ABeanClass.class)), MockRequestPropertyResolver.value(42))
        );

        MethodParameter methodParameter = createMethodParameter("anAnnotatedMethod", ABeanClass.class);

        StubWebExchangeDataBinder dataBinder = new StubWebExchangeDataBinder(new ABeanClass());
        when(bindingContext.createDataBinder(eq(exchange), any(ABeanClass.class), anyString())).thenReturn(dataBinder);

        when(introspector.getResolversFor(ABeanClass.class)).thenReturn(propertyData);
        Mono<Object> objectMono = resolver.resolveArgument(methodParameter, bindingContext, exchange);
        objectMono.block();

        PropertyValues propertyValues = dataBinder.getPropertyValues();
        assertThat(propertyValues.contains("propertyOne")).isFalse();
        assertThat(propertyValues.contains("propertyTwo")).isTrue();
        
        assertThat(propertyValues.getPropertyValue("propertyTwo"))
            .isNotNull()
            .extracting(PropertyValue::getValue).isEqualTo(42);
    }

    @Test
    void throwsExceptionWhenIntrospectorErrors() throws Exception {
        List<ResolvedPropertyData> propertyData = Arrays.asList(
            new ResolvedPropertyData("propertyOne", BindingProperty.forPropertyDescriptor(new PropertyDescriptor("propertyOne", ABeanClass.class)), MockRequestPropertyResolver.value("expected")),
            new ResolvedPropertyData("propertyTwo", BindingProperty.forPropertyDescriptor(new PropertyDescriptor("propertyTwo", ABeanClass.class)), MockRequestPropertyResolver.throwsException())
        );

        MethodParameter methodParameter = createMethodParameter("anAnnotatedMethod", ABeanClass.class);
        when(introspector.getResolversFor(ABeanClass.class)).thenReturn(propertyData);

        Mono<Object> objectMono = resolver.resolveArgument(methodParameter, bindingContext, exchange);
        assertThatThrownBy(objectMono::block)
            .isInstanceOf(RuntimeException.class);
    }

    @Test
    void throwsExceptionWhenResolverErrors() throws Exception {
        List<ResolvedPropertyData> propertyData = Arrays.asList(
            new ResolvedPropertyData("propertyOne", BindingProperty.forPropertyDescriptor(new PropertyDescriptor("propertyOne", ABeanClass.class)), MockRequestPropertyResolver.value("expected")),
            new ResolvedPropertyData("propertyTwo", BindingProperty.forPropertyDescriptor(new PropertyDescriptor("propertyTwo", ABeanClass.class)), MockRequestPropertyResolver.throwsException())
        );

        MethodParameter methodParameter = createMethodParameter("anAnnotatedMethod", ABeanClass.class);

        when(bindingContext.createDataBinder(eq(exchange), any(ABeanClass.class), anyString())).thenReturn(new StubWebExchangeDataBinder(new ABeanClass()));
        when(introspector.getResolversFor(ABeanClass.class)).thenReturn(propertyData);
        Mono<Object> objectMono = resolver.resolveArgument(methodParameter, bindingContext, exchange);
        assertThatThrownBy(objectMono::block).isInstanceOf(RequestPropertyBindingException.class);
    }

    @Test
    void validatesWhenValidAnnotationPresent() throws Exception {
        MethodParameter methodParameter = createMethodParameter("aValidMethod", ABeanClass.class);

        StubWebExchangeDataBinder dataBinder = new StubWebExchangeDataBinder(new ABeanClass());
        when(bindingContext.createDataBinder(eq(exchange), any(ABeanClass.class), anyString())).thenReturn(dataBinder);

        Mono<Object> objectMono = resolver.resolveArgument(methodParameter, bindingContext, exchange);
        objectMono.block();

        assertThat(dataBinder.isValidateInvoked()).isTrue();
    }

    @Test
    void validatesWhenValidatedAnnotationPresent() throws Exception {
        MethodParameter methodParameter = createMethodParameter("aValidatedMethod", ABeanClass.class);

        StubWebExchangeDataBinder dataBinder = new StubWebExchangeDataBinder(new ABeanClass());
        when(bindingContext.createDataBinder(eq(exchange), any(ABeanClass.class), anyString())).thenReturn(dataBinder);

        Mono<Object> objectMono = resolver.resolveArgument(methodParameter, bindingContext, exchange);
        objectMono.block();

        assertThat(dataBinder.isValidateInvoked()).isTrue();
    }

    @Test
    void validatesWithGroups() throws Exception {
        MethodParameter methodParameter = createMethodParameter("validationGroups", ABeanClass.class);

        StubWebExchangeDataBinder dataBinder = new StubWebExchangeDataBinder(new ABeanClass());
        when(bindingContext.createDataBinder(eq(exchange), any(ABeanClass.class), anyString())).thenReturn(dataBinder);

        Mono<Object> objectMono = resolver.resolveArgument(methodParameter, bindingContext, exchange);
        objectMono.block();

        assertThat(dataBinder.isValidateInvoked()).isTrue();
        assertThat(dataBinder.getValidationHints()).contains(ValidationGroupOne.class, ValidationGroupTwo.class);
    }

    @Test
    void validationThrowsBindException() throws Exception {
        MethodParameter methodParameter = createMethodParameter("aValidMethod", ABeanClass.class);

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);

        StubWebExchangeDataBinder dataBinder = new StubWebExchangeDataBinder(new ABeanClass());
        dataBinder.setBindingResult(bindingResult);
        when(bindingContext.createDataBinder(eq(exchange), any(ABeanClass.class), anyString())).thenReturn(dataBinder);

        Mono<Object> objectMono = resolver.resolveArgument(methodParameter, bindingContext, exchange);
        WebExchangeBindException exception = catchThrowableOfType(objectMono::block, WebExchangeBindException.class);
        assertThat(exception.getBindingResult()).isEqualTo(bindingResult);
    }

    @Test
    void validationHasErrorsWithBindingResultMethodParameter() throws Exception {
        MethodParameter methodParameter = createMethodParameter("withBindingResult", ABeanClass.class, BindingResult.class);

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);

        StubWebExchangeDataBinder dataBinder = new StubWebExchangeDataBinder(new ABeanClass());
        dataBinder.setBindingResult(bindingResult);
        when(bindingContext.createDataBinder(eq(exchange), any(ABeanClass.class), anyString())).thenReturn(dataBinder);

        Mono<Object> objectMono = resolver.resolveArgument(methodParameter, bindingContext, exchange);
        objectMono.block();

        assertThat(dataBinder.getBindingResult()).isEqualTo(bindingResult);
    }

    private MethodParameter createMethodParameter(String anAnnotatedMethod, Class<?>... parameterTypes) throws NoSuchMethodException {
        return new MethodParameter(FakeHandlerMethod.class.getMethod(anAnnotatedMethod, parameterTypes), 0);
    }

    private static class StubWebExchangeDataBinder extends WebExchangeDataBinder {
        private boolean bindInvoked = false;
        private boolean validateInvoked = true;
        private PropertyValues pvs;
        private List<Object> validationHints;
        private BindingResult bindingResult;

        public StubWebExchangeDataBinder(Object target) {
            super(target);
            FormattingConversionServiceFactoryBean conversionServiceFactoryBean = new FormattingConversionServiceFactoryBean();
            conversionServiceFactoryBean.afterPropertiesSet();
            setConversionService(conversionServiceFactoryBean.getObject());
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

    private static class MockRequestPropertyResolver implements RequestPropertyResolver {
        private final Object value;
        private final RuntimeException exception;

        private <T> MockRequestPropertyResolver(@Nullable T value, @Nullable RuntimeException exception) {
            this.value = value;
            this.exception = exception;
        }

        @Override
        public boolean supports(@NonNull BindingProperty bindingProperty) {
            // Not used in this test
            return true;
        }

        @NonNull
        @Override
        public Mono<Object> resolve(@NonNull BindingProperty bindingProperty, @NonNull ServerWebExchange exchange) {
            if (exception != null) {
                throw exception;
            }
            return Mono.justOrEmpty(value);
        }

        public static <T> MockRequestPropertyResolver value(T value) {
            return new MockRequestPropertyResolver(value, null);
        }

        public static MockRequestPropertyResolver throwsException() {
            return new MockRequestPropertyResolver(null, new RuntimeException());
        }

        public static MockRequestPropertyResolver noValueFound() {
            return new MockRequestPropertyResolver(null, null);
        }
    }

    @SuppressWarnings("unused")
    private static class FakeHandlerMethod {
        public void anAnnotatedMethod(@BeanParameter ABeanClass aBeanClass) {
            // Do nothing
        }

        public void annotatedSimpleType(@BeanParameter int simpleType) {
            // Do nothing
        }

        @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
        public void optionalTypeAnnotated(@BeanParameter Optional<ABeanClass> optionalBeanClass) {
            // Do nothing
        }

        public void missingAnnotation(ABeanClass aBeanClass) {
            // Do nothing
        }

        public void aValidatedMethod(@BeanParameter @Validated ABeanClass validatedBean) {
            // Do nothing
        }

        public void aValidMethod(@BeanParameter @Valid ABeanClass validBean) {
            // Do nothing
        }

        public void validationGroups(@BeanParameter @Validated({ValidationGroupOne.class, ValidationGroupTwo.class}) ABeanClass validatedBean) {
            // Do nothing
        }

        public void withBindingResult(@BeanParameter @Validated ABeanClass aBeanClass, BindingResult bindingResult) {
            // Do nothing
        }
    }

    private static class ABeanClass {
        private String propertyOne;

        private Integer propertyTwo;

        public String getPropertyOne() {
            return propertyOne;
        }

        public void setPropertyOne(String propertyOne) {
            this.propertyOne = propertyOne;
        }

        public Integer getPropertyTwo() {
            return propertyTwo;
        }

        public void setPropertyTwo(Integer propertyTwo) {
            this.propertyTwo = propertyTwo;
        }
    }
    
    private static class ValidationGroupOne {}
    private static class ValidationGroupTwo {}
}
