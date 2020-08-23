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

import com.mattbertolini.spring.web.bind.annotation.RequestParameter;
import com.mattbertolini.spring.web.bind.introspect.BindingProperty;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.lang.NonNull;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.NativeWebRequest;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

public class RequestParameterMapRequestPropertyResolver implements RequestPropertyResolver {
    @Override
    public boolean supports(@NonNull BindingProperty bindingProperty) {
        RequestParameter annotation = bindingProperty.getAnnotation(RequestParameter.class);
        return annotation != null && !StringUtils.hasText(annotation.value()) &&
            Map.class.isAssignableFrom(bindingProperty.getType());
    }

    @Override
    public Object resolve(@NonNull TypeDescriptor typeDescriptor, @NonNull NativeWebRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();

        if (MultiValueMap.class.isAssignableFrom(typeDescriptor.getType())) {
            MultiValueMap<String, String> ret = new LinkedMultiValueMap<>(parameterMap.size());
            for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
                ret.put(entry.getKey(), new LinkedList<>(Arrays.asList(entry.getValue())));
            }
            return ret;
        }

        Map<String, String> ret = new LinkedHashMap<>(parameterMap.size());
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            ret.put(entry.getKey(), entry.getValue()[0]);
        }
        return ret;
    }
}
