package com.mattbertolini.spring.web.bind.introspect;

import com.mattbertolini.spring.web.bind.annotation.RequestParameter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
        void throwsIllegalStateExceptionWhenNoGetterOrSetterPresent() {
            assertThrows(IllegalStateException.class, () -> BindingProperty.forPropertyDescriptor(aPropertyDescriptor("noGettersOrSetters", null, null)));
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

    private PropertyDescriptor aPropertyDescriptor(String propertyName) throws IntrospectionException {
        return new PropertyDescriptor(propertyName, TestingType.class);
    }

    @SuppressWarnings("SameParameterValue")
    private PropertyDescriptor aPropertyDescriptor(String propertyName, String getterMethodName) throws IntrospectionException {
        return aPropertyDescriptor(propertyName, getterMethodName, null);
    }

    private PropertyDescriptor aPropertyDescriptor(String propertyName, String getterName, String setterName) throws IntrospectionException {
        return new PropertyDescriptor(propertyName, TestingType.class, getterName, setterName);
    }

    @SuppressWarnings("unused")
    static class TestingType {
        private String stringProperty;

        private int primitiveProperty;

        @RequestParameter("irrelevant")
        private String annotatedProperty;

        private String getterOnly;

        @SuppressWarnings("FieldCanBeLocal")
        private String setterOnly;

        private String noGetterOrSetter;

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

        public String getAnnotatedProperty() {
            return annotatedProperty;
        }

        public void setAnnotatedProperty(String annotatedProperty) {
            this.annotatedProperty = annotatedProperty;
        }

        public String getGetterOnly() {
            return getterOnly;
        }

        public void setSetterOnly(String setterOnly) {
            this.setterOnly = setterOnly;
        }
    }
}
