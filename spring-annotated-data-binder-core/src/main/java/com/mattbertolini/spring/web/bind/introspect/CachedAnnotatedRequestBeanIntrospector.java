/*
 * Copyright 2019-2022 the original author or authors.
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

package com.mattbertolini.spring.web.bind.introspect;

import org.springframework.lang.NonNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class CachedAnnotatedRequestBeanIntrospector implements AnnotatedRequestBeanIntrospector {
    private final AnnotatedRequestBeanIntrospector delegate;
    private final ConcurrentMap<Class<?>, Map<String, ResolvedPropertyData>> cache;

    public CachedAnnotatedRequestBeanIntrospector(@NonNull AnnotatedRequestBeanIntrospector delegate) {
        this.delegate = delegate;
        cache = new ConcurrentHashMap<>();
    }

    @Override
    @NonNull
    public Map<String, ResolvedPropertyData> getResolverMapFor(@NonNull Class<?> targetType) {
        return cache.computeIfAbsent(targetType, delegate::getResolverMapFor);
    }
}
