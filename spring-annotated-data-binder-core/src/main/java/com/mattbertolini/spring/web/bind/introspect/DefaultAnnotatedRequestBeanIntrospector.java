/*
 * Copyright 2019-2022 the original author or authors.
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

import com.mattbertolini.spring.web.bind.AbstractPropertyResolverRegistry;
import com.mattbertolini.spring.web.bind.annotation.BeanParameter;
import com.mattbertolini.spring.web.bind.resolver.RequestPropertyResolverBase;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.beans.PropertyDescriptor;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * The main request bean introspector. This contains the logic on determining which request resolvers are attached to
 * which bean properties.
 */
public class DefaultAnnotatedRequestBeanIntrospector implements AnnotatedRequestBeanIntrospector {
    
    private final AbstractPropertyResolverRegistry<?> registry;

    public DefaultAnnotatedRequestBeanIntrospector(@NonNull AbstractPropertyResolverRegistry<?> registry) {
        this.registry = registry;
    }

    /**
     * Creates a list of resolved property data for the given target class. This method traverses the object graph for
     * the given type recursively. Circular references are not allowed as they will cause stack overflow errors.
     *
     * @param targetType The class or type to get property resolver data for. Required.
     * @return A list of resolved property data. This list is never null but may be empty.
     * @throws CircularReferenceException If a circular reference is found while traversing the object graph.
     */
    @Override
    @NonNull
    public List<ResolvedPropertyData> getResolversFor(@NonNull Class<?> targetType) {
        Set<Class<?>> cycleClasses = new LinkedHashSet<>();
        List<ResolvedPropertyData> propertyData = new LinkedList<>();
        recursiveGetResolversFor(targetType, null, propertyData, cycleClasses);
        return propertyData;
    }

    private void recursiveGetResolversFor(@NonNull final Class<?> targetType,
                                          @Nullable final String prefix,
                                          @NonNull final List<ResolvedPropertyData> propertyData,
                                          @NonNull final Set<Class<?>> cycleClasses) {
        PropertyDescriptor[] propertyDescriptors;
        try {
            propertyDescriptors = BeanUtils.getPropertyDescriptors(targetType);
        } catch (BeansException e) {
            throw new RequestBeanIntrospectionException("Unable to introspect request bean of type " +
                targetType.getName() + ": " + e.getMessage(), e);
        }
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            String propertyName = getPropertyName(prefix, propertyDescriptor);
            BindingProperty bindingProperty = BindingProperty.forPropertyDescriptor(propertyDescriptor);
            Class<?> type = bindingProperty.getType();
            if (bindingProperty.hasAnnotation(BeanParameter.class) && !BeanUtils.isSimpleProperty(type)) {
                if (!cycleClasses.add(type)) {
                    throw new CircularReferenceException("Aborting finding resolvers. Circular reference found. Circular " +
                        "references not supported as they can cause stack overflow errors. Cycle: " +
                        cycleClasses.toString());
                }
                recursiveGetResolversFor(type, propertyName, propertyData, cycleClasses);
                cycleClasses.remove(type);
            } else {
                RequestPropertyResolverBase<?, ?> resolver = registry.findResolverFor(bindingProperty);
                if (resolver == null) {
                    continue;
                }
                propertyData.add(new ResolvedPropertyData(propertyName, bindingProperty, resolver));
            }
        }
    }

    /**
     * Creates a JavaBeans property name based on the given prefix and property descriptor. If no prefix is given the
     * property descriptor is returned. The prefix represents the parent bean the given property descriptor is found in.
     * These property names are used later by the Spring data binder to find the property and set request data.
     *
     * @param prefix The prefix to return before the property descriptor name. Separated by a dot(.). Optional.
     * @param propertyDescriptor The property descriptor to find a name for. Required.
     * @return The full property name path
     */
    private String getPropertyName(@Nullable String prefix, @NonNull PropertyDescriptor propertyDescriptor) {
        if (prefix == null) {
            return propertyDescriptor.getName();
        }
        return prefix + "." + propertyDescriptor.getName();
    }
}
