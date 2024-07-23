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
package com.mattbertolini.spring.web.bind.introspect;

import com.mattbertolini.spring.web.bind.resolver.RequestPropertyResolverBase;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.lang.Nullable;

import java.beans.PropertyDescriptor;

import static org.assertj.core.api.Assertions.assertThat;

class ResolvedPropertyDataTest {
    private ResolvedPropertyData propertyData;

    @BeforeEach
    void setUp() throws Exception {
        propertyData = new ResolvedPropertyData(
            "name",
            BindingProperty.forPropertyDescriptor(new PropertyDescriptor("property", TestingClass.class)),
            new StubResolver());
    }

    @Test
    void returnsPropertyName() {
        assertThat(propertyData.propertyName()).isEqualTo("name");
    }

    @Test
    void returnsBindingProperty() throws Exception {
        BindingProperty expected = BindingProperty.forPropertyDescriptor(
            new PropertyDescriptor("property", TestingClass.class));
        assertThat(propertyData.bindingProperty()).isEqualTo(expected);
    }

    @Test
    void returnsResolver() {
        assertThat(propertyData.resolver()).isNotNull();
        assertThat(propertyData.resolver()).isInstanceOf(StubResolver.class);
    }

    @Test
    void equalsContract() throws Exception {
        EqualsVerifier.forClass(ResolvedPropertyData.class)
            .withPrefabValues(TypeDescriptor.class, TypeDescriptor.valueOf(String.class), TypeDescriptor.valueOf(Integer.class))
            .withPrefabValues(BindingProperty.class,
                BindingProperty.forPropertyDescriptor(new PropertyDescriptor("property", TestingClass.class)),
                BindingProperty.forPropertyDescriptor(new PropertyDescriptor("another", TestingClass.class)))
            .verify();
    }

    private static class StubResolver implements RequestPropertyResolverBase<Object, Object> {

        @Override
        public boolean supports(BindingProperty bindingProperty) {
            return false;
        }

        @Override
        @Nullable
        public Object resolve(BindingProperty bindingProperty, Object request) {
            return null;
        }
    }

    @SuppressWarnings("unused")
    private static class TestingClass {
        @Nullable
        private String property;

        @Nullable
        private String another;

        @Nullable
        public String getProperty() {
            return property;
        }

        public void setProperty(String property) {
            this.property = property;
        }

        @Nullable
        public String getAnother() {
            return another;
        }

        public void setAnother(String another) {
            this.another = another;
        }
    }
}
