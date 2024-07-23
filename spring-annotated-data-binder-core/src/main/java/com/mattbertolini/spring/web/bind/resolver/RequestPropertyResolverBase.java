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
package com.mattbertolini.spring.web.bind.resolver;

import com.mattbertolini.spring.web.bind.introspect.BindingProperty;
import org.springframework.lang.Nullable;

/**
 * This interface should be considered an internal interface and should not be implemented by external users. Instead,
 * use one of the sub-interfaces that are bound to a concrete request type.
 * 
 * @param <T> The request type to use with the resolver.
 * @param <R> The response type to use.
 */
public interface RequestPropertyResolverBase<T, R> {
    boolean supports(BindingProperty bindingProperty);
    
    @Nullable
    R resolve(BindingProperty bindingProperty, T request);
}
