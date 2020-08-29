package com.mattbertolini.spring.web.servlet.mvc.bind.resolver;

import com.mattbertolini.spring.web.bind.PropertyResolutionException;
import com.mattbertolini.spring.web.bind.annotation.RequestBody;
import com.mattbertolini.spring.web.bind.introspect.BindingProperty;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

import java.util.List;

public class RequestBodyRequestPropertyResolver implements RequestPropertyResolver {
    private final RequestResponseBodyMethodProcessor processor;

    public RequestBodyRequestPropertyResolver(List<HttpMessageConverter<?>> messageConverters) {
        this(new RequestResponseBodyMethodProcessor(messageConverters));
    }

    /**
     * Visible for testing purposes only.
     */
    RequestBodyRequestPropertyResolver(@NonNull RequestResponseBodyMethodProcessor processor) {
        this.processor = processor;
    }

    @Override
    public boolean supports(@NonNull BindingProperty bindingProperty) {
        return bindingProperty.hasAnnotation(RequestBody.class);
    }

    @Override
    public Object resolve(@NonNull BindingProperty bindingProperty, @NonNull NativeWebRequest request) {
        RequestBody annotation = bindingProperty.getAnnotation(RequestBody.class);
        Assert.state(annotation != null, "No RequestBody annotation found on type");
        try {
            return processor.resolveArgument(bindingProperty.getMethodParameter(), null, request, null);
        } catch (Exception e) {
            throw new PropertyResolutionException("Error resolving request body.", e);
        }
    }
}
