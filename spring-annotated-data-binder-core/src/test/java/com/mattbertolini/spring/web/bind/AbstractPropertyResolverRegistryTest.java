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

package com.mattbertolini.spring.web.bind;

import com.mattbertolini.spring.web.bind.introspect.BindingProperty;
import com.mattbertolini.spring.web.bind.resolver.RequestPropertyResolverBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.convert.TypeDescriptor;

import java.beans.PropertyDescriptor;
import java.util.Collections;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AbstractPropertyResolverRegistryTest {
    private TestingRegistry registry;

    @BeforeEach
    void setUp() {
        registry = new TestingRegistry();
    }

    @Test
    void findsResolvers() throws Exception {
        TestingResolver resolverOne = mock(TestingResolver.class);
        TestingResolver resolverTwo = mock(TestingResolver.class);
        TestingResolver resolverThree = mock(TestingResolver.class);

        when(resolverTwo.supports(any(BindingProperty.class))).thenReturn(true);

        registry.addResolver(resolverOne);
        registry.addResolver(resolverTwo);
        registry.addResolver(resolverThree);

        TestingResolver actual = registry.findResolverFor(BindingProperty.forPropertyDescriptor(new PropertyDescriptor("property", TestingClass.class)));
        assertThat(actual).isEqualTo(resolverTwo);
    }

    @Test
    void addResolversWithSet() {
        TestingResolver resolver = mock(TestingResolver.class);
        Set<TestingResolver> set = Collections.singleton(resolver);

        registry.addResolvers(set);

        Set<TestingResolver> actual = registry.getPropertyResolvers();
        assertThat(actual)
            .isNotEmpty()
            .containsAll(set);
    }

    @Test
    void addResolversFromAnotherRegistry() {
        TestingResolver resolverOne = mock(TestingResolver.class);
        TestingResolver resolverTwo = mock(TestingResolver.class);
        TestingRegistry anotherRegistry = new TestingRegistry();
        anotherRegistry.addResolver(resolverOne);
        anotherRegistry.addResolver(resolverTwo);

        registry.addResolvers(anotherRegistry);

        Set<TestingResolver> actual = registry.getPropertyResolvers();
        assertThat(actual)
            .isNotEmpty()
            .contains(resolverOne, resolverTwo);
    }

    @Test
    void addsSingleResolver() {
        TestingResolver resolver = mock(TestingResolver.class);

        registry.addResolver(resolver);

        Set<TestingResolver> actual = registry.getPropertyResolvers();
        assertThat(actual)
            .isNotEmpty()
            .contains(resolver);
    }

    private interface TestingResolver extends RequestPropertyResolverBase<Object, Object> {}
    private static class TestingRegistry extends AbstractPropertyResolverRegistry<TestingResolver> {}

    @SuppressWarnings("unused")
    private static class TestingClass {
        private String property;

        public String getProperty() {
            return property;
        }

        public void setProperty(String property) {
            this.property = property;
        }
    }
}
