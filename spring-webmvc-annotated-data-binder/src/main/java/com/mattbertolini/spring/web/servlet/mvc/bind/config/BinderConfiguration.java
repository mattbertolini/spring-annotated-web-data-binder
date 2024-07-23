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
package com.mattbertolini.spring.web.servlet.mvc.bind.config;

import com.mattbertolini.spring.web.bind.introspect.AnnotatedRequestBeanIntrospector;
import com.mattbertolini.spring.web.bind.introspect.ClassPathScanningAnnotatedRequestBeanIntrospector;
import com.mattbertolini.spring.web.bind.introspect.DefaultAnnotatedRequestBeanIntrospector;
import com.mattbertolini.spring.web.servlet.mvc.bind.BeanParameterMethodArgumentResolver;
import com.mattbertolini.spring.web.servlet.mvc.bind.PropertyResolverRegistry;
import com.mattbertolini.spring.web.servlet.mvc.bind.resolver.CookieParameterRequestPropertyResolver;
import com.mattbertolini.spring.web.servlet.mvc.bind.resolver.FormParameterMapRequestPropertyResolver;
import com.mattbertolini.spring.web.servlet.mvc.bind.resolver.FormParameterRequestPropertyResolver;
import com.mattbertolini.spring.web.servlet.mvc.bind.resolver.HeaderParameterMapRequestPropertyResolver;
import com.mattbertolini.spring.web.servlet.mvc.bind.resolver.HeaderParameterRequestPropertyResolver;
import com.mattbertolini.spring.web.servlet.mvc.bind.resolver.PathParameterMapRequestPropertyResolver;
import com.mattbertolini.spring.web.servlet.mvc.bind.resolver.PathParameterRequestPropertyResolver;
import com.mattbertolini.spring.web.servlet.mvc.bind.resolver.RequestBodyRequestPropertyResolver;
import com.mattbertolini.spring.web.servlet.mvc.bind.resolver.RequestContextRequestPropertyResolver;
import com.mattbertolini.spring.web.servlet.mvc.bind.resolver.RequestParameterMapRequestPropertyResolver;
import com.mattbertolini.spring.web.servlet.mvc.bind.resolver.RequestParameterRequestPropertyResolver;
import com.mattbertolini.spring.web.servlet.mvc.bind.resolver.RequestPropertyResolver;
import com.mattbertolini.spring.web.servlet.mvc.bind.resolver.SessionParameterRequestPropertyResolver;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.lang.Nullable;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@SuppressWarnings("UnusedReturnValue")
public class BinderConfiguration implements BeanPostProcessor {
    private final Set<String> packagesToScan;
    private final PropertyResolverRegistry propertyResolverRegistry;

    public BinderConfiguration() {
        this(new PropertyResolverRegistry());
    }

    public BinderConfiguration(PropertyResolverRegistry propertyResolverRegistry) {
        packagesToScan = new LinkedHashSet<>();
        this.propertyResolverRegistry = propertyResolverRegistry;
    }

    public BinderConfiguration addPackageToScan(String packageToScan) {
        packagesToScan.add(packageToScan);
        return this;
    }

    public BinderConfiguration setPackagesToScan(Set<String> packagesToScan) {
        this.packagesToScan.addAll(packagesToScan);
        return this;
    }

    public BinderConfiguration addResolver(RequestPropertyResolver resolver) {
        propertyResolverRegistry.addResolver(resolver);
        return this;
    }

    public BinderConfiguration addResolvers(Set<RequestPropertyResolver> resolvers) {
        propertyResolverRegistry.addResolvers(resolvers);
        return this;
    }

    public BinderConfiguration addResolvers(PropertyResolverRegistry propertyResolverRegistry) {
        this.propertyResolverRegistry.addResolvers(propertyResolverRegistry);
        return this;
    }

    public Set<String> getPackagesToScan() {
        return Collections.unmodifiableSet(packagesToScan);
    }

    @Override
    @Nullable
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        if (!(bean instanceof RequestMappingHandlerAdapter adapter)) {
            return bean;
        }

        PropertyResolverRegistry resolverRegistry = createPropertyResolverRegistry(adapter);
        AnnotatedRequestBeanIntrospector introspector = createIntrospector(resolverRegistry);
        BeanParameterMethodArgumentResolver resolver = createResolver(introspector);

        addCustomResolverToHandlerAdapter(adapter, resolver);

        return adapter;
    }

    private PropertyResolverRegistry createPropertyResolverRegistry(RequestMappingHandlerAdapter adapter) {
        PropertyResolverRegistry registry = new PropertyResolverRegistry();

        registry.addResolver(new RequestParameterRequestPropertyResolver());
        registry.addResolver(new RequestParameterMapRequestPropertyResolver());
        registry.addResolver(new FormParameterRequestPropertyResolver());
        registry.addResolver(new FormParameterMapRequestPropertyResolver());
        registry.addResolver(new PathParameterRequestPropertyResolver());
        registry.addResolver(new PathParameterMapRequestPropertyResolver());
        registry.addResolver(new CookieParameterRequestPropertyResolver());
        registry.addResolver(new HeaderParameterRequestPropertyResolver());
        registry.addResolver(new HeaderParameterMapRequestPropertyResolver());
        registry.addResolver(new SessionParameterRequestPropertyResolver());
        registry.addResolver(new RequestContextRequestPropertyResolver());
        registry.addResolver(new RequestBodyRequestPropertyResolver(adapter.getMessageConverters()));
        
        registry.addResolvers(propertyResolverRegistry);

        return registry;
    }

    private AnnotatedRequestBeanIntrospector createIntrospector(PropertyResolverRegistry registry) {
        DefaultAnnotatedRequestBeanIntrospector defaultIntrospector = new DefaultAnnotatedRequestBeanIntrospector(registry);
        ClassPathScanningAnnotatedRequestBeanIntrospector introspector = new ClassPathScanningAnnotatedRequestBeanIntrospector(defaultIntrospector, packagesToScan);
        try {
            introspector.afterPropertiesSet();
        } catch (Exception e) {
            throw new BeanInitializationException("Unable to create introspector", e);
        }
        return introspector;
    }

    private BeanParameterMethodArgumentResolver createResolver(AnnotatedRequestBeanIntrospector introspector) {
        return new BeanParameterMethodArgumentResolver(introspector);
    }

    private void addCustomResolverToHandlerAdapter(RequestMappingHandlerAdapter adapter, BeanParameterMethodArgumentResolver resolver) {
        List<HandlerMethodArgumentResolver> currentResolvers = adapter.getCustomArgumentResolvers();
        if (currentResolvers == null) {
            currentResolvers = Collections.emptyList();
        }
        List<HandlerMethodArgumentResolver> newResolvers = new ArrayList<>(currentResolvers.size() + 1);
        newResolvers.addAll(currentResolvers);
        newResolvers.add(resolver);
        adapter.setCustomArgumentResolvers(newResolvers);
    }
}
