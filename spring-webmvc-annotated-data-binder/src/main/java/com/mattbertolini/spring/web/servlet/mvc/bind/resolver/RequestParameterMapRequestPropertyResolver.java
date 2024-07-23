/*
 * Copyright 2024 the original author or authors.
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
package com.mattbertolini.spring.web.servlet.mvc.bind.resolver;

import com.mattbertolini.spring.web.bind.PropertyResolutionException;
import com.mattbertolini.spring.web.bind.annotation.RequestParameter;
import com.mattbertolini.spring.web.bind.introspect.BindingProperty;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;
import org.springframework.core.ResolvableType;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;
import org.springframework.web.multipart.support.MultipartResolutionDelegate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class RequestParameterMapRequestPropertyResolver implements RequestPropertyResolver {
    @Override
    public boolean supports(@NonNull BindingProperty bindingProperty) {
        RequestParameter annotation = bindingProperty.getAnnotation(RequestParameter.class);
        return annotation != null && !StringUtils.hasText(annotation.value()) &&
            Map.class.isAssignableFrom(bindingProperty.getType());
    }

    @Override
    @Nullable
    public Object resolve(@NonNull BindingProperty bindingProperty, @NonNull NativeWebRequest request) {
        ResolvableType resolvableType = ResolvableType.forMethodParameter(bindingProperty.getMethodParameter());

        // Multipart params
        if (MultiValueMap.class.isAssignableFrom(bindingProperty.getType())) {
            Class<?> mapValueType = resolvableType.as(MultiValueMap.class).getGeneric(1).resolve();
            if (MultipartFile.class == mapValueType) {
                MultipartRequest multipartRequest = MultipartResolutionDelegate.resolveMultipartRequest(request);
                return (multipartRequest != null ? multipartRequest.getMultiFileMap() : new LinkedMultiValueMap<>());
            } else if (Part.class == mapValueType) {
                return resolveServletRequestPartsToMultiValueMap(request);
            }

            // Standard params
            Map<String, String[]> parameterMap = request.getParameterMap();
            MultiValueMap<String, String> ret = new LinkedMultiValueMap<>(parameterMap.size());
            for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
                ret.put(entry.getKey(), new ArrayList<>(Arrays.asList(entry.getValue())));
            }
            return ret;
        }

        // Multipart params
        Class<?> mapValueType = resolvableType.asMap().getGeneric(1).resolve();
        if (MultipartFile.class == mapValueType) {
            MultipartRequest multipartRequest = MultipartResolutionDelegate.resolveMultipartRequest(request);
            return (multipartRequest != null ? multipartRequest.getFileMap() : new LinkedHashMap<>());
        } else if (Part.class == mapValueType) {
            return resolveServletRequestPartsToMap(request);
        }

        // Standard params
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, String> ret = new LinkedHashMap<>(parameterMap.size());
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            ret.put(entry.getKey(), entry.getValue()[0]);
        }
        return ret;
    }

    private LinkedMultiValueMap<?, ?> resolveServletRequestPartsToMultiValueMap(NativeWebRequest request) {
        try {
            HttpServletRequest servletRequest = request.getNativeRequest(HttpServletRequest.class);
            if (servletRequest != null && MultipartResolutionDelegate.isMultipartRequest(servletRequest)) {
                Collection<Part> parts = servletRequest.getParts();
                LinkedMultiValueMap<String, Part> result = new LinkedMultiValueMap<>(parts.size());
                for (Part part : parts) {
                    result.add(part.getName(), part);
                }
                return result;
            }
            return new LinkedMultiValueMap<>();
        } catch (IOException | ServletException e) {
            throw new PropertyResolutionException("Exception resolving multipart objects into MultiValueMap.", e);
        }
    }

    private Map<?, ?> resolveServletRequestPartsToMap(NativeWebRequest request) {
        try {
            HttpServletRequest servletRequest = request.getNativeRequest(HttpServletRequest.class);
            if (servletRequest != null && MultipartResolutionDelegate.isMultipartRequest(servletRequest)) {
                Collection<Part> parts = servletRequest.getParts();
                LinkedHashMap<String, Part> result = new LinkedHashMap<>(parts.size());
                for (Part part : parts) {
                    if (!result.containsKey(part.getName())) {
                        result.put(part.getName(), part);
                    }
                }
                return result;
            }
            return new LinkedHashMap<>();
        } catch (IOException | ServletException e) {
            throw new PropertyResolutionException("Exception resolving multipart objects into Map", e);
        }
    }
}
