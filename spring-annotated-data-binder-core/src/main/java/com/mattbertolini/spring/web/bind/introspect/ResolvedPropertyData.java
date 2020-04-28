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

package com.mattbertolini.spring.web.bind.introspect;

import com.mattbertolini.spring.web.bind.resolver.RequestPropertyResolverBase;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.lang.NonNull;

import java.util.Objects;

public final class ResolvedPropertyData {
    private final String propertyName;
    private final TypeDescriptor typeDescriptor;
    private final RequestPropertyResolverBase<?, ?> resolver;

    public ResolvedPropertyData(
        @NonNull String propertyName,
        @NonNull TypeDescriptor typeDescriptor,
        @NonNull RequestPropertyResolverBase<?, ?> resolver
    ) {
        this.propertyName = propertyName;
        this.typeDescriptor = typeDescriptor;
        this.resolver = resolver;
    }

    @NonNull
    public String getPropertyName() {
        return propertyName;
    }

    @NonNull
    public TypeDescriptor getTypeDescriptor() {
        return typeDescriptor;
    }

    @NonNull
    public RequestPropertyResolverBase<?, ?> getResolver() {
        return resolver;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ResolvedPropertyData)) {
            return false;
        }
        ResolvedPropertyData that = (ResolvedPropertyData) o;
        return Objects.equals(propertyName, that.propertyName) &&
            Objects.equals(typeDescriptor, that.typeDescriptor) &&
            Objects.equals(resolver, that.resolver);
    }

    @Override
    public int hashCode() {
        return Objects.hash(propertyName, typeDescriptor, resolver);
    }
}
