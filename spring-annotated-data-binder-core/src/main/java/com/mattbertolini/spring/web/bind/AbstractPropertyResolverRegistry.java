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

package com.mattbertolini.spring.web.bind;

import com.mattbertolini.spring.web.bind.introspect.BindingProperty;
import com.mattbertolini.spring.web.bind.resolver.RequestPropertyResolverBase;
import org.springframework.lang.Nullable;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Do not extend directly from this class. Extend from the two subclasses that are specific to Spring MVC or Spring
 * WebFlux.
 * @param <T> The resolver type
 */
public abstract class AbstractPropertyResolverRegistry<T extends RequestPropertyResolverBase<?, ?>> {
    private final Set<T> propertyResolvers;

    protected AbstractPropertyResolverRegistry() {
        propertyResolvers = new LinkedHashSet<>();
    }

    @Nullable
    public T findResolverFor(BindingProperty bindingProperty) {
        for (T resolver : propertyResolvers) {
            if (resolver.supports(bindingProperty)) {
                return resolver;
            }
        }
        return null;
    }

    /**
     * Add the given resolver to this registry.
     *
     * @param resolver The resolver to add.
     */
    public void addResolver(T resolver) {
        propertyResolvers.add(resolver);
    }

    /**
     * Add all the resolvers in the given set to this registry.
     *
     * @param resolvers The set of resolvers to add.
     */
    public void addResolvers(Set<T> resolvers) {
        propertyResolvers.addAll(resolvers);
    }

    /**
     * Add all the resolvers in the given registry to this registry.
     * 
     * @param registry The registry to add resolvers from.
     */
    public void addResolvers(AbstractPropertyResolverRegistry<T> registry) {
        addResolvers(registry.getPropertyResolvers());
    }

    /**
     * Returns an unmodifiable collection of the resolvers.
     */
    public Set<T> getPropertyResolvers() {
        return Collections.unmodifiableSet(propertyResolvers);
    }
}
