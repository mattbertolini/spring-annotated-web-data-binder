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
package com.mattbertolini.spring.web.reactive.bind.config;

import com.mattbertolini.spring.web.reactive.bind.BeanParameterMethodArgumentResolver;
import com.mattbertolini.spring.web.reactive.bind.PropertyResolverRegistry;
import com.mattbertolini.spring.web.reactive.bind.resolver.RequestContextRequestPropertyResolver;
import com.mattbertolini.spring.web.reactive.bind.resolver.RequestPropertyResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.http.codec.FormHttpMessageReader;
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer;
import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerAdapter;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BinderConfigurationTest {
    private BinderConfiguration config;
    private RequestMappingHandlerAdapter adapter;

    @BeforeEach
    void setUp() {
        config = new BinderConfiguration();
        adapter = mock(RequestMappingHandlerAdapter.class);
        when(adapter.getMessageReaders()).thenReturn(Collections.singletonList(new FormHttpMessageReader()));
        when(adapter.getReactiveAdapterRegistry()).thenReturn(new ReactiveAdapterRegistry());
    }

    @Test
    void returnsBeanIfNotHandlerAdapter() {
        Object obj = config.postProcessBeforeInitialization(new Object(), "irrelevant");
        assertThat(obj).isNotNull().isInstanceOf(Object.class);
    }

    @Test
    void returnsAdapterBeanIfCorrectType() {
        Object obj = config.postProcessBeforeInitialization(adapter, "irrelevant");
        assertThat(obj).isInstanceOf(RequestMappingHandlerAdapter.class);
    }

    @Test
    void addsMethodArgumentResolverToConfigurer() {
        ArgumentResolverConfigurer resolverConfigurer = mock(ArgumentResolverConfigurer.class);
        when(adapter.getArgumentResolverConfigurer()).thenReturn(resolverConfigurer);
        config.postProcessBeforeInitialization(adapter, "irrelevant");
        verify(resolverConfigurer).addCustomResolver(any(BeanParameterMethodArgumentResolver.class));
    }

    @Test
    void addsResolversViaSet() {
        PropertyResolverRegistry registry = mock(PropertyResolverRegistry.class);
        BinderConfiguration configuration = new BinderConfiguration(registry);
        configuration.addResolvers(Collections.singleton(new RequestContextRequestPropertyResolver()));
        verify(registry).addResolvers(anySet());
    }

    @Test
    void addsSingleResolver() {
        PropertyResolverRegistry registry = mock(PropertyResolverRegistry.class);
        BinderConfiguration configuration = new BinderConfiguration(registry);
        configuration.addResolver(new RequestContextRequestPropertyResolver());
        verify(registry).addResolver(any(RequestPropertyResolver.class));
    }

    @Test
    void addsResolversFromAnotherRegistry() {
        PropertyResolverRegistry registry = mock(PropertyResolverRegistry.class);
        BinderConfiguration configuration = new BinderConfiguration(registry);
        configuration.addResolvers(new PropertyResolverRegistry());
        verify(registry).addResolvers(any(PropertyResolverRegistry.class));
    }

    @Test
    void addsSinglePackage() {
        BinderConfiguration binderConfiguration = new BinderConfiguration();
        assertThat(binderConfiguration.getPackagesToScan()).doesNotContain("com.example.test");
        binderConfiguration.addPackageToScan("com.example.test");
        assertThat(binderConfiguration.getPackagesToScan()).contains("com.example.test");
    }

    @Test
    void addsSetOfPackages() {
        BinderConfiguration binderConfiguration = new BinderConfiguration();
        assertThat(binderConfiguration.getPackagesToScan()).isEmpty();
        binderConfiguration.setPackagesToScan(new HashSet<>(Arrays.asList("com.example.test", "com.example.another.test")));
        assertThat(binderConfiguration.getPackagesToScan()).contains("com.example.test", "com.example.another.test");
    }
}
