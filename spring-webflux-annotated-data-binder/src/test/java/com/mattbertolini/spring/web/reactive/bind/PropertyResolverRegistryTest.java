package com.mattbertolini.spring.web.reactive.bind;

import com.mattbertolini.spring.web.bind.introspect.BindingProperty;
import com.mattbertolini.spring.web.reactive.bind.resolver.RequestPropertyResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.convert.TypeDescriptor;
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
        assertThat(registry.getPropertyResolvers().size()).isEqualTo(0);
        registry.addResolvers(Collections.singleton(new FakeResolver()));
        assertThat(registry.getPropertyResolvers().size()).isEqualTo(1);
    }

    @Test
    void addsSingleResolver() {
        assertThat(registry.getPropertyResolvers().size()).isEqualTo(0);
        registry.addResolver(new FakeResolver());
        assertThat(registry.getPropertyResolvers().size()).isEqualTo(1);
    }

    @Test
    void addsResolversFromRegistry() {
        PropertyResolverRegistry anotherRegistry = new PropertyResolverRegistry();
        anotherRegistry.addResolver(new FakeResolver());
        assertThat(registry.getPropertyResolvers().size()).isEqualTo(0);
        registry.addResolvers(anotherRegistry);
        assertThat(registry.getPropertyResolvers().size()).isEqualTo(1);
    }

    private static class FakeResolver implements RequestPropertyResolver {

        @Override
        public boolean supports(@NonNull TypeDescriptor typeDescriptor, @NonNull BindingProperty bindingProperty) {
            return false;
        }

        @NonNull
        @Override
        public Mono<Object> resolve(@NonNull TypeDescriptor typeDescriptor, @NonNull ServerWebExchange exchange) {
            return Mono.empty();
        }
    }
}
