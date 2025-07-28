/*
 * Copyright 2025 the original author or authors.
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
package com.mattbertolini.spring.web.reactive.bind;

import com.mattbertolini.spring.web.bind.introspect.BindingProperty;
import com.mattbertolini.spring.web.reactive.bind.resolver.RequestPropertyResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.lang.NonNull;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class PropertyResolverRegistryTest {

    private PropertyResolverRegistry registry;

    @BeforeEach
    void setUp() {
        registry = new PropertyResolverRegistry();
    }

    @Test
    void addsResolversFromSet() {
        assertThat(registry.getPropertyResolvers()).isEmpty();
        registry.addResolvers(Collections.singleton(new FakeResolver()));
        assertThat(registry.getPropertyResolvers()).hasSize(1);
    }

    @Test
    void addsSingleResolver() {
        assertThat(registry.getPropertyResolvers()).isEmpty();
        registry.addResolver(new FakeResolver());
        assertThat(registry.getPropertyResolvers()).hasSize(1);
    }

    @Test
    void addsResolversFromRegistry() {
        PropertyResolverRegistry anotherRegistry = new PropertyResolverRegistry();
        anotherRegistry.addResolver(new FakeResolver());
        assertThat(registry.getPropertyResolvers()).isEmpty();
        registry.addResolvers(anotherRegistry);
        assertThat(registry.getPropertyResolvers()).hasSize(1);
    }

    private static class FakeResolver implements RequestPropertyResolver {

        @Override
        public boolean supports(@NonNull BindingProperty bindingProperty) {
            return false;
        }

        @NonNull
        @Override
        public Mono<Object> resolve(@NonNull BindingProperty bindingProperty, @NonNull ServerWebExchange exchange) {
            return Mono.empty();
        }
    }
}
