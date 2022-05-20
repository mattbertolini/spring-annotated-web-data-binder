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

import org.springframework.lang.NonNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public interface AnnotatedRequestBeanIntrospector {

    /**
     * Creates a map of resolved property data for the given target class. This method traverses the object graph for
     * the given type recursively. Circular references are not allowed as they will cause stack overflow errors.
     *
     * @param targetType The class or type to get property resolver data for. Required.
     * @return A map of resolved property data. This map is never null but may be empty.
     * @throws CircularReferenceException If a circular reference is found while traversing the object graph.
     */
    @NonNull
    Map<String, ResolvedPropertyData> getResolverMapFor(@NonNull Class<?> targetType);

    /**
     * Creates a list of resolved property data for the given target class. This method traverses the object graph for
     * the given type recursively. Circular references are not allowed as they will cause stack overflow errors.
     *
     * @param targetType The class or type to get property resolver data for. Required.
     * @return A list of resolved property data. This list is never null but may be empty.
     * @throws CircularReferenceException If a circular reference is found while traversing the object graph.
     */
    @NonNull
    default Collection<ResolvedPropertyData> getResolversFor(@NonNull Class<?> targetType) {
        Map<String, ResolvedPropertyData> propertyData = getResolverMapFor(targetType);
        return Collections.unmodifiableCollection(propertyData.values());
    }
}
