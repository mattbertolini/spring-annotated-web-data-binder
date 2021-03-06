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
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.lang.NonNull;

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
        assertThat(propertyData.getPropertyName()).isEqualTo("name");
    }

    @Test
    void returnsBindingProperty() throws Exception {
        BindingProperty expected = BindingProperty.forPropertyDescriptor(
            new PropertyDescriptor("property", TestingClass.class));
        assertThat(propertyData.getBindingProperty()).isEqualTo(expected);
    }

    @Test
    void returnsResolver() {
        assertThat(propertyData.getResolver()).isNotNull();
        assertThat(propertyData.getResolver()).isInstanceOf(StubResolver.class);
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
        public boolean supports(@NonNull BindingProperty bindingProperty) {
            return false;
        }

        @Override
        public Object resolve(@NonNull BindingProperty bindingProperty, @NonNull Object request) {
            return null;
        }
    }

    @SuppressWarnings("unused")
    private static class TestingClass {
        private String property;

        private String another;

        public String getProperty() {
            return property;
        }

        public void setProperty(String property) {
            this.property = property;
        }

        public String getAnother() {
            return another;
        }

        public void setAnother(String another) {
            this.another = another;
        }
    }
}
