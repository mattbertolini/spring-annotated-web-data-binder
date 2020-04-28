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

package com.mattbertolini.spring.web.reactive.bind.config;

import com.mattbertolini.spring.web.bind.introspect.ClassPathScanningAnnotatedRequestBeanIntrospector;
import com.mattbertolini.spring.web.bind.introspect.DefaultAnnotatedRequestBeanIntrospector;
import com.mattbertolini.spring.web.reactive.bind.BeanParameterMethodArgumentResolver;
import com.mattbertolini.spring.web.reactive.bind.DefaultPropertyResolverRegistry;
import com.mattbertolini.spring.web.reactive.bind.PropertyResolverRegistry;
import com.mattbertolini.spring.web.reactive.bind.resolver.RequestPropertyResolver;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.lang.NonNull;
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer;
import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerAdapter;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Main configuration class for annotated data binder using Spring WebFlux.
 */
@SuppressWarnings("UnusedReturnValue")
public class BinderConfiguration implements InitializingBean, BeanPostProcessor {
    private final Set<String> packagesToScan;
    private final PropertyResolverRegistry propertyResolverRegistry;
    
    private ClassPathScanningAnnotatedRequestBeanIntrospector introspector;

    /**
     * Construct a configuration with the default settings.
     */
    public BinderConfiguration() {
        this(new DefaultPropertyResolverRegistry());
    }

    /**
     * Construct a configuration with the given registry. Using this constructor means the caller is required to add
     * all default resolvers manually. This is considered an advanced configuration.
     *
     * @param propertyResolverRegistry The resolver registry to use.
     */
    public BinderConfiguration(@NonNull PropertyResolverRegistry propertyResolverRegistry) {
        packagesToScan = new LinkedHashSet<>();
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
    public void afterPropertiesSet() {
        DefaultAnnotatedRequestBeanIntrospector defaultIntrospector = new DefaultAnnotatedRequestBeanIntrospector(propertyResolverRegistry);
        introspector = new ClassPathScanningAnnotatedRequestBeanIntrospector(defaultIntrospector, packagesToScan);
        try {
            introspector.afterPropertiesSet();
        } catch (Exception e) {
            throw new BeanInitializationException("Unable to create a introspector.", e);
        }
    }

    @Override
    public Object postProcessBeforeInitialization(@NonNull Object bean, @NonNull String beanName) throws BeansException {
        if (!(bean instanceof RequestMappingHandlerAdapter)) {
            return bean;
        }

        if (introspector == null) {
            throw new IllegalStateException("Introspector is null. Perhaps the afterPropertiesSet method was not called?");
        }
        RequestMappingHandlerAdapter adapter = (RequestMappingHandlerAdapter) bean;
        ArgumentResolverConfigurer resolverConfigurer = adapter.getArgumentResolverConfigurer();
        if (resolverConfigurer != null) {
            ReactiveAdapterRegistry reactiveAdapterRegistry = adapter.getReactiveAdapterRegistry();
            if (reactiveAdapterRegistry == null) {
                throw new BeanInitializationException("Unable to initialize BeanParameterMethodArgumentResolver. ReactiveAdapterRegistry is null.");
            }
            BeanParameterMethodArgumentResolver resolver = new BeanParameterMethodArgumentResolver(reactiveAdapterRegistry, introspector);
            resolverConfigurer.addCustomResolver(resolver);
        }
        return adapter;
    }
}
