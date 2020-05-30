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
