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

import static org.assertj.core.api.Assertions.assertThat;

class ResolvedPropertyDataTest {
    private ResolvedPropertyData propertyData;

    @BeforeEach
    void setUp() {
        propertyData = new ResolvedPropertyData("name", TypeDescriptor.valueOf(String.class), new StubResolver());
    }

    @Test
    void returnsPropertyName() {
        assertThat(propertyData.getPropertyName()).isEqualTo("name");
    }

    @Test
    void returnsTypeDescriptor() {
        assertThat(propertyData.getTypeDescriptor()).isEqualTo(TypeDescriptor.valueOf(String.class));
    }

    @Test
    void returnsResolver() {
        assertThat(propertyData.getResolver()).isNotNull();
        assertThat(propertyData.getResolver()).isInstanceOf(StubResolver.class);
    }

    @Test
    void equalsContract() {
        EqualsVerifier.forClass(ResolvedPropertyData.class)
            .withPrefabValues(TypeDescriptor.class, TypeDescriptor.valueOf(String.class), TypeDescriptor.valueOf(Integer.class))
            .verify();
    }

    private static class StubResolver implements RequestPropertyResolverBase<Object, Object> {

        @Override
        public boolean supports(@NonNull TypeDescriptor typeDescriptor) {
            return false;
        }

        @Override
        public Object resolve(@NonNull TypeDescriptor typeDescriptor, @NonNull Object request) {
            return null;
        }
    }
}
