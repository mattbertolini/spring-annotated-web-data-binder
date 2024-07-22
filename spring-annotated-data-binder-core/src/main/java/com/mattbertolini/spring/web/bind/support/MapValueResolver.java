/*
 * Copyright 2024 the original author or authors.
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

package com.mattbertolini.spring.web.bind.support;

import org.springframework.lang.Nullable;
import org.springframework.validation.DataBinder;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * Implementation of the {@link DataBinder.ValueResolver} to assist in mapping binding data for constructor binding.
 *
 * @param values The Map of values to pass to the binder
 */
public record MapValueResolver(Map<String, Object> values) implements DataBinder.ValueResolver {
    @Override
    @Nullable
    public Object resolveValue(String name, Class<?> type) {
        return values.get(name);
    }

    @Override
    public Set<String> getNames() {
        return Set.copyOf(values.keySet());
    }

    @Override
    public Map<String, Object> values() {
        return Collections.unmodifiableMap(values);
    }
}
