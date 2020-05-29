package com.mattbertolini.spring.web.bind.resolver;

import org.junit.jupiter.api.Test;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.lang.NonNull;

import static org.assertj.core.api.Assertions.assertThat;

class AbstractNamedRequestPropertyResolverTest {
    @SuppressWarnings("unchecked")
    @Test
    void resolveUsesResolveWithNameMethod() {
        TestingResolver resolver = new TestingResolver("expected");         
        Object actual = resolver.resolve(TypeDescriptor.valueOf(String.class), new Object());
        assertThat(actual).isEqualTo("expected");
    }

    @SuppressWarnings("rawtypes")
    private static class TestingResolver extends AbstractNamedRequestPropertyResolver {

        private final String name;

        public TestingResolver(String name) {
            this.name = name;
        }

        @Override
        @NonNull
        protected String getName(@NonNull TypeDescriptor typeDescriptor) {
            return name;
        }

        @Override
        protected Object resolveWithName(@NonNull TypeDescriptor typeDescriptor, String name, @NonNull Object request) {
            return name;
        }

        @Override
        public boolean supports(@NonNull TypeDescriptor typeDescriptor) {
            return true;
        }
    }
}
