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

package com.mattbertolini.spring.web.servlet.mvc.bind.resolver;

import com.mattbertolini.spring.web.bind.annotation.HeaderParameter;
import com.mattbertolini.spring.web.bind.introspect.BindingProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.NativeWebRequest;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class HeaderParameterMapRequestPropertyResolver implements RequestPropertyResolver {
    @Override
    public boolean supports(@NonNull BindingProperty bindingProperty) {
        HeaderParameter annotation = bindingProperty.getAnnotation(HeaderParameter.class);
        return annotation != null && !StringUtils.hasText(annotation.value()) &&
            Map.class.isAssignableFrom(bindingProperty.getType());
    }

    @Override
    public Object resolve(@NonNull BindingProperty bindingProperty, @NonNull NativeWebRequest request) {
        if (MultiValueMap.class.isAssignableFrom(bindingProperty.getType())) {
            MultiValueMap<String, String> retMap;
            if (HttpHeaders.class.isAssignableFrom(bindingProperty.getType())) {
                retMap = new HttpHeaders();
            } else {
                retMap = new LinkedMultiValueMap<>(); 
            }
            for (Iterator<String> iterator = request.getHeaderNames(); iterator.hasNext();) {
                String headerName = iterator.next();
                String[] headerValues = request.getHeaderValues(headerName);
                if (headerValues != null) {
                    for (String headerValue : headerValues) {
                        retMap.add(headerName, headerValue);
                    }
                }
            }
            return retMap;
        }

        Map<String, String> retMap = new LinkedHashMap<>();
        for (Iterator<String> iterator = request.getHeaderNames(); iterator.hasNext();) {
            String headerName = iterator.next();
            String headerValue = request.getHeader(headerName);
            retMap.put(headerName, headerValue);
        }
        return retMap;
    }
}
