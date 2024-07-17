/*
 * Copyright 2024 the original author or authors.
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

import org.springframework.core.MethodParameter;
import org.springframework.core.convert.Property;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.lang.Nullable;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.util.Objects;

/**
 * An abstraction around a Java bean property that contains reflection information used by the data binder. This class
 * is needed because the spring web framework need both the {@link MethodParameter} type and the {@link TypeDescriptor}
 * type. This class contains both types derived from the same Java {@link PropertyDescriptor}.
 */
public final class BindingProperty {
    private final TypeDescriptor typeDescriptor;
    private final MethodParameter methodParameter;

    private BindingProperty(TypeDescriptor typeDescriptor, MethodParameter methodParameter) {
        this.typeDescriptor = typeDescriptor;
        this.methodParameter = methodParameter;
    }

    @Nullable
    public <T extends Annotation> T getAnnotation(Class<T> annotationType) {
        return typeDescriptor.getAnnotation(annotationType);
    }

    public MethodParameter getMethodParameter() {
        return methodParameter;
    }

    public Class<?> getObjectType() {
        return typeDescriptor.getObjectType();
    }

    public Class<?> getType() {
        return typeDescriptor.getType();
    }

    public boolean hasAnnotation(Class<? extends Annotation> annotationType) {
        return typeDescriptor.hasAnnotation(annotationType);
    }

    /**
     * Factory method for creating a BindingProperty for the given {@link PropertyDescriptor} type. This is used for
     * bridging the Java beans world to the Spring world.
     *
     * @param propertyDescriptor The Java Beans PropertyDescriptor to create a BindingProperty for.
     * @return A new BindingProperty object.
     */
    public static BindingProperty forPropertyDescriptor(PropertyDescriptor propertyDescriptor) {
        Property property = new Property(
            propertyDescriptor.getPropertyType(),
            propertyDescriptor.getReadMethod(),
            propertyDescriptor.getWriteMethod()
        );
        TypeDescriptor typeDescriptor = new TypeDescriptor(property);
        MethodParameter methodParameter = resolveMethodParameter(property);

        return new BindingProperty(typeDescriptor, methodParameter);
    }

    /**
     * This method is more or less the same as found in {@link Property} but those are not exposed publicly so I
     * needed to replicate it.
     */
    private static MethodParameter resolveMethodParameter(Property property) {
        MethodParameter readMethodParameter = resolveReadMethodParameter(property);
        MethodParameter writeMethodParameter = resolveWriteMethodParameter(property);
        if (writeMethodParameter == null) {
            if (readMethodParameter == null) {
                throw new IllegalStateException("Property does not have a getter or setter method");
            }
            return readMethodParameter;
        }
        if (readMethodParameter != null) {
            Class<?> readType = readMethodParameter.getParameterType();
            Class<?> writeType = writeMethodParameter.getParameterType();
            if (!writeType.equals(readType) && writeType.isAssignableFrom(readType)) {
                return readMethodParameter;
            }
        }
        return writeMethodParameter;
    }

    @Nullable
    private static MethodParameter resolveReadMethodParameter(Property property) {
        if (property.getReadMethod() == null) {
            return null;
        }
        return new MethodParameter(property.getReadMethod(), -1).withContainingClass(property.getObjectType());
    }

    @Nullable
    private static MethodParameter resolveWriteMethodParameter(Property property) {
        if (property.getWriteMethod() == null) {
            return null;
        }
        return new MethodParameter(property.getWriteMethod(), 0).withContainingClass(property.getObjectType());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BindingProperty)) return false;
        BindingProperty that = (BindingProperty) o;
        return Objects.equals(typeDescriptor, that.typeDescriptor) &&
            Objects.equals(methodParameter, that.methodParameter);
    }

    @Override
    public int hashCode() {
        return Objects.hash(typeDescriptor, methodParameter);
    }
}
