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
import org.junit.jupiter.api.Test;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.beans.PropertyDescriptor;

import static org.assertj.core.api.Assertions.assertThat;

class AbstractNamedRequestPropertyResolverTest {
    @SuppressWarnings("unchecked")
    @Test
    void resolveUsesResolveWithNameMethod() throws Exception {
        TestingResolver resolver = new TestingResolver("expected");
        BindingProperty bindingProperty = BindingProperty.forPropertyDescriptor(new PropertyDescriptor("property", TestingBean.class));
        Object actual = resolver.resolve(bindingProperty, new Object());
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
        protected String getName(@NonNull BindingProperty bindingProperty) {
            return name;
        }

        @Override
        protected Object resolveWithName(@NonNull BindingProperty bindingProperty, String name, @NonNull Object request) {
            return name;
        }

        @Override
        public boolean supports(@NonNull BindingProperty bindingProperty) {
            return true;
        }
    }

    @SuppressWarnings("unused")
    private static class TestingBean {
        @Nullable
        private String property;

        @Nullable
        public String getProperty() {
            return property;
        }

        public void setProperty(String property) {
            this.property = property;
        }
    }
}
