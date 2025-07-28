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

import com.mattbertolini.spring.web.bind.annotation.RequestParameter;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.lang.Nullable;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BindingPropertyTest {

    @DisplayName("getAnnotation")
    @Nested
    class GetAnnotation {
        @Test
        void getsAnnotationOnProperty() throws Exception {
            BindingProperty bindingProperty = BindingProperty.forPropertyDescriptor(aPropertyDescriptor("annotatedProperty"));
            RequestParameter annotation = bindingProperty.getAnnotation(RequestParameter.class);
            assertThat(annotation)
                .isNotNull()
                .isInstanceOf(RequestParameter.class);
        }

        @Test
        void returnsNullOnNoAnnotationFound() throws Exception {
            BindingProperty bindingProperty = BindingProperty.forPropertyDescriptor(aPropertyDescriptor("stringProperty"));
            assertThat(bindingProperty.getAnnotation(RequestParameter.class)).isNull();
        }
    }

    @DisplayName("GetMethodParameter")
    @Nested
    class getMethodParameter {
        @Test
        void propertyWithGetterAndSetterUsesSetterMethod() throws Exception {
            Method expected = TestingType.class.getMethod("setStringProperty", String.class);
            BindingProperty bindingProperty = BindingProperty.forPropertyDescriptor(aPropertyDescriptor("stringProperty"));
            MethodParameter methodParameter = bindingProperty.getMethodParameter();
            assertThat(methodParameter).isNotNull().extracting(MethodParameter::getMethod).isEqualTo(expected);
        }

        @Test
        void propertyWithOnlyGetterUsesGetterMethod() throws Exception {
            Method expected = TestingType.class.getMethod("getGetterOnly");
            BindingProperty bindingProperty = BindingProperty.forPropertyDescriptor(aPropertyDescriptor("getterOnly", "getGetterOnly"));
            MethodParameter methodParameter = bindingProperty.getMethodParameter();
            assertThat(methodParameter).isNotNull().extracting(MethodParameter::getMethod).isEqualTo(expected);
        }

        @Test
        void propertyWithOnlySetterUsesSetterMethod() throws Exception {
            Method expected = TestingType.class.getMethod("setSetterOnly", String.class);
            BindingProperty bindingProperty = BindingProperty.forPropertyDescriptor(aPropertyDescriptor("setterOnly", null, "setSetterOnly"));
            MethodParameter methodParameter = bindingProperty.getMethodParameter();
            assertThat(methodParameter).isNotNull().extracting(MethodParameter::getMethod).isEqualTo(expected);
        }

        @Test
        void throwsIllegalStateExceptionWhenNoGetterOrSetterPresent() throws Exception {
            PropertyDescriptor propertyDescriptor = aPropertyDescriptor("noGettersOrSetters", null, null);
            assertThatThrownBy(() -> BindingProperty.forPropertyDescriptor(propertyDescriptor)).isInstanceOf(IllegalStateException.class);
        }
    }

    @DisplayName("getObjectType")
    @Nested
    class GetObjectType {
        @Test
        void returnsType() throws Exception {
            BindingProperty bindingProperty = BindingProperty.forPropertyDescriptor(aPropertyDescriptor("stringProperty"));
            assertThat(bindingProperty.getObjectType()).isEqualTo(String.class);
        }

        @Test
        void returnsWrapperTypeForPrimitive() throws Exception {
            BindingProperty bindingProperty = BindingProperty.forPropertyDescriptor(aPropertyDescriptor("primitiveProperty"));
            assertThat(bindingProperty.getObjectType()).isEqualTo(Integer.class);
        }
    }

    @DisplayName("getType")
    @Nested
    class GetType {
        @Test
        void returnsType() throws Exception {
            BindingProperty bindingProperty = BindingProperty.forPropertyDescriptor(aPropertyDescriptor("stringProperty"));
            assertThat(bindingProperty.getType()).isEqualTo(String.class);
        }

        @Test
        void returnsPrimitiveType() throws Exception {
            BindingProperty bindingProperty = BindingProperty.forPropertyDescriptor(aPropertyDescriptor("primitiveProperty"));
            assertThat(bindingProperty.getType()).isEqualTo(int.class);
        }
    }

    @DisplayName("hasAnnotation")
    @Nested
    class HasAnnotation {
        @Test
        void returnsTrueOnAnnotationPresent() throws Exception {
            BindingProperty bindingProperty = BindingProperty.forPropertyDescriptor(aPropertyDescriptor("annotatedProperty"));
            assertThat(bindingProperty.hasAnnotation(RequestParameter.class)).isTrue();
        }

        @Test
        void returnsFalseOnNoAnnotationPresent() throws Exception {
            BindingProperty bindingProperty = BindingProperty.forPropertyDescriptor(aPropertyDescriptor("stringProperty"));
            assertThat(bindingProperty.hasAnnotation(RequestParameter.class)).isFalse();
        }
    }

    @Test
    void equalsContract() throws Exception {
        EqualsVerifier.forClass(BindingProperty.class)
            .withPrefabValues(TypeDescriptor.class, TypeDescriptor.valueOf(String.class), TypeDescriptor.valueOf(Integer.class))
            .withPrefabValues(MethodParameter.class,
                new MethodParameter(BindingProperty.class.getMethod("getType"), -1),
                new MethodParameter(BindingProperty.class.getMethod("getObjectType"), -1))
            .verify();
    }

    private PropertyDescriptor aPropertyDescriptor(String propertyName) throws IntrospectionException {
        return new PropertyDescriptor(propertyName, TestingType.class);
    }

    @SuppressWarnings("SameParameterValue")
    private PropertyDescriptor aPropertyDescriptor(String propertyName, String getterMethodName) throws IntrospectionException {
        return aPropertyDescriptor(propertyName, getterMethodName, null);
    }

    private PropertyDescriptor aPropertyDescriptor(String propertyName, @Nullable String getterName, @Nullable String setterName) throws IntrospectionException {
        return new PropertyDescriptor(propertyName, TestingType.class, getterName, setterName);
    }

    @SuppressWarnings("unused")
    private static class TestingType {
        @Nullable
        private String stringProperty;

        private int primitiveProperty;

        @Nullable
        @RequestParameter("irrelevant")
        private String annotatedProperty;

        @Nullable
        private String getterOnly;

        @SuppressWarnings("FieldCanBeLocal")
        @Nullable
        private String setterOnly;

        @Nullable
        private String noGetterOrSetter;

        @Nullable
        public String getStringProperty() {
            return stringProperty;
        }

        public void setStringProperty(String stringProperty) {
            this.stringProperty = stringProperty;
        }

        public int getPrimitiveProperty() {
            return primitiveProperty;
        }

        public void setPrimitiveProperty(int primitiveProperty) {
            this.primitiveProperty = primitiveProperty;
        }

        @Nullable
        public String getAnnotatedProperty() {
            return annotatedProperty;
        }

        public void setAnnotatedProperty(String annotatedProperty) {
            this.annotatedProperty = annotatedProperty;
        }

        @Nullable
        public String getGetterOnly() {
            return getterOnly;
        }

        public void setSetterOnly(String setterOnly) {
            this.setterOnly = setterOnly;
        }
    }
}
