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

import com.mattbertolini.spring.web.bind.annotation.RequestBean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ClassPathScanningAnnotatedRequestBeanIntrospector implements AnnotatedRequestBeanIntrospector, InitializingBean {
    private static final Log LOGGER = LogFactory.getLog(ClassPathScanningAnnotatedRequestBeanIntrospector.class);

    private final ClassPathScanningCandidateComponentProvider scanner;
    private final CachedAnnotatedRequestBeanIntrospector introspectorCache;
    private final Set<String> basePackages;
    
    public ClassPathScanningAnnotatedRequestBeanIntrospector(@NonNull AnnotatedRequestBeanIntrospector delegate, @Nullable Set<String> basePackages) {
        this.basePackages = new HashSet<>();
        if (basePackages != null) {
            this.basePackages.addAll(basePackages);
        }
        this.introspectorCache = new CachedAnnotatedRequestBeanIntrospector(delegate);
        scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(RequestBean.class));
    }

    @Override
    @NonNull
    public Map<String, ResolvedPropertyData> getResolverMapFor(@NonNull Class<?> targetType) {
        return introspectorCache.getResolverMapFor(targetType);
    }

    @Override
    public void afterPropertiesSet() {
        for (String basePackage : basePackages) {
            scanAndLoadRequestBeans(basePackage);
        }
    }

    private void scanAndLoadRequestBeans(@NonNull String basePackage) {
        ClassLoader classLoader = ClassPathScanningAnnotatedRequestBeanIntrospector.class.getClassLoader();
        LOGGER.debug("Searching for @RequestBean annotated classes in package [" + basePackage + "]");
        Set<BeanDefinition> candidateComponents = scanner.findCandidateComponents(basePackage);
        LOGGER.debug("Found " + candidateComponents.size() + " annotated classes in package [" + basePackage + "]");
        for (BeanDefinition candidateComponent : candidateComponents) {
            String beanClassName = candidateComponent.getBeanClassName();
            if (!StringUtils.hasText(beanClassName)) {
                continue;
            }
            try {
                LOGGER.debug("Introspecting request bean " + beanClassName);
                Class<?> clazz = ClassUtils.forName(beanClassName, classLoader);
                // Invoking cache getResolverMapFor will trigger the delegate introspector and save data into the cache.
                introspectorCache.getResolverMapFor(clazz);
            } catch (Exception e) {
                throw new RequestBeanIntrospectionException("Unable to introspect request bean of type " + beanClassName + ": " + e.getMessage(), e);
            }

        }
    }
}
