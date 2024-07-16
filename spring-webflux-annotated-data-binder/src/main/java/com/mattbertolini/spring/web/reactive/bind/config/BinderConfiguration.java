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

package com.mattbertolini.spring.web.reactive.bind.config;

import com.mattbertolini.spring.web.bind.introspect.AnnotatedRequestBeanIntrospector;
import com.mattbertolini.spring.web.bind.introspect.ClassPathScanningAnnotatedRequestBeanIntrospector;
import com.mattbertolini.spring.web.bind.introspect.DefaultAnnotatedRequestBeanIntrospector;
import com.mattbertolini.spring.web.reactive.bind.BeanParameterMethodArgumentResolver;
import com.mattbertolini.spring.web.reactive.bind.PropertyResolverRegistry;
import com.mattbertolini.spring.web.reactive.bind.resolver.CookieParameterRequestPropertyResolver;
import com.mattbertolini.spring.web.reactive.bind.resolver.FormParameterMapRequestPropertyResolver;
import com.mattbertolini.spring.web.reactive.bind.resolver.FormParameterRequestPropertyResolver;
import com.mattbertolini.spring.web.reactive.bind.resolver.HeaderParameterMapRequestPropertyResolver;
import com.mattbertolini.spring.web.reactive.bind.resolver.HeaderParameterRequestPropertyResolver;
import com.mattbertolini.spring.web.reactive.bind.resolver.PathParameterMapRequestPropertyResolver;
import com.mattbertolini.spring.web.reactive.bind.resolver.PathParameterRequestPropertyResolver;
import com.mattbertolini.spring.web.reactive.bind.resolver.RequestBodyRequestPropertyResolver;
import com.mattbertolini.spring.web.reactive.bind.resolver.RequestContextRequestPropertyResolver;
import com.mattbertolini.spring.web.reactive.bind.resolver.RequestParameterMapRequestPropertyResolver;
import com.mattbertolini.spring.web.reactive.bind.resolver.RequestParameterRequestPropertyResolver;
import com.mattbertolini.spring.web.reactive.bind.resolver.RequestPropertyResolver;
import com.mattbertolini.spring.web.reactive.bind.resolver.SessionParameterRequestPropertyResolver;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.lang.NonNull;
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer;
import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerAdapter;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Main configuration class for annotated data binder using Spring WebFlux.
 */
@SuppressWarnings("UnusedReturnValue")
public class BinderConfiguration implements BeanPostProcessor {
    private final Set<String> packagesToScan;
    private final PropertyResolverRegistry propertyResolverRegistry;

    /**
     * Construct a configuration with the default settings.
     */
    public BinderConfiguration() {
        this(new PropertyResolverRegistry());
    }

    /**
     * Construct a configuration with the given registry. Using this constructor means the caller is required to add
     * all default resolvers manually. This is considered an advanced configuration.
     *
     * @param propertyResolverRegistry The resolver registry to use.
     */
    public BinderConfiguration(@NonNull PropertyResolverRegistry propertyResolverRegistry) {
        packagesToScan = new HashSet<>();
        this.propertyResolverRegistry = propertyResolverRegistry;
    }

    /**
     * Add a package to introspector to scan at startup.
     *
     * @param packageToScan The package to scan. Required.
     * @return This instance of the configuration.
     */
    public BinderConfiguration addPackageToScan(String packageToScan) {
        packagesToScan.add(packageToScan);
        return this;
    }

    /**
     * Add the set of packages to the introspector to scan.
     *
     * @param packagesToScan The packages to scan. Required.
     * @return This instance of the configuration.
     */
    public BinderConfiguration setPackagesToScan(Set<String> packagesToScan) {
        this.packagesToScan.addAll(packagesToScan);
        return this;
    }

    /**
     * Add a custom {@link RequestPropertyResolver}.
     *
     * @param resolver The resolver to add. Required.
     * @return This instance of the configuration.
     */
    public BinderConfiguration addResolver(RequestPropertyResolver resolver) {
        propertyResolverRegistry.addResolver(resolver);
        return this;
    }

    /**
     * Add a set of custom {@link RequestPropertyResolver} instances.
     *
     * @param resolvers The resolvers to add. Required.
     * @return This instance of the configuration.
     */
    public BinderConfiguration addResolvers(Set<RequestPropertyResolver> resolvers) {
        propertyResolverRegistry.addResolvers(resolvers);
        return this;
    }

    /**
     * Add all of the resolvers from the given registry into this registry.
     *
     * @param propertyResolverRegistry The registry to add resolvers from. Required.
     * @return This instance of the configuration.
     */
    public BinderConfiguration addResolvers(PropertyResolverRegistry propertyResolverRegistry) {
        this.propertyResolverRegistry.addResolvers(propertyResolverRegistry);
        return this;
    }

    /**
     * Get an unmodifiable set of the packages to be scanned by the introspector.
     *
     * @return The set of packages to scan.
     */
    public Set<String> getPackagesToScan() {
        return Collections.unmodifiableSet(packagesToScan);
    }

    @Override
    public Object postProcessBeforeInitialization(@NonNull Object bean, @NonNull String beanName) {
        if (!(bean instanceof RequestMappingHandlerAdapter adapter)) {
            return bean;
        }

        ArgumentResolverConfigurer resolverConfigurer = adapter.getArgumentResolverConfigurer();
        if (resolverConfigurer != null) {
            ReactiveAdapterRegistry reactiveAdapterRegistry = adapter.getReactiveAdapterRegistry();
            if (reactiveAdapterRegistry == null) {
                throw new BeanInitializationException("Unable to initialize BeanParameterMethodArgumentResolver. ReactiveAdapterRegistry is null.");
            }

            PropertyResolverRegistry resolverRegistry = createPropertyResolverRegistry(adapter, reactiveAdapterRegistry);
            AnnotatedRequestBeanIntrospector introspector = createIntrospector(resolverRegistry);
            BeanParameterMethodArgumentResolver resolver = createResolver(introspector, reactiveAdapterRegistry);

            resolverConfigurer.addCustomResolver(resolver);
        }
        return adapter;
    }
    
    private PropertyResolverRegistry createPropertyResolverRegistry(RequestMappingHandlerAdapter adapter, ReactiveAdapterRegistry reactiveAdapterRegistry) {
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
        registry.addResolver(new RequestBodyRequestPropertyResolver(adapter.getMessageReaders(), reactiveAdapterRegistry));

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

    private BeanParameterMethodArgumentResolver createResolver(AnnotatedRequestBeanIntrospector introspector, ReactiveAdapterRegistry reactiveAdapterRegistry) {
        return new BeanParameterMethodArgumentResolver(reactiveAdapterRegistry, introspector);
    }
}
