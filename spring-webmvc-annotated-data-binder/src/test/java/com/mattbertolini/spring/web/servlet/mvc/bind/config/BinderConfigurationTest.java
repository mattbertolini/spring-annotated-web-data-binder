package com.mattbertolini.spring.web.servlet.mvc.bind.config;

import com.mattbertolini.spring.web.servlet.mvc.bind.PropertyResolverRegistry;
import com.mattbertolini.spring.web.servlet.mvc.bind.resolver.RequestContextRequestPropertyResolver;
import com.mattbertolini.spring.web.servlet.mvc.bind.resolver.RequestPropertyResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class BinderConfigurationTest {
    private BinderConfiguration config;
    private RequestMappingHandlerAdapter adapter;

    @BeforeEach
    void setUp() {
        config = new BinderConfiguration();
        adapter = mock(RequestMappingHandlerAdapter.class);
    }

    @Test
    void returnsBeanIfNotHandlerAdapter() throws Exception {
        Object obj = config.postProcessBeforeInitialization(new Object(), "irrelevant");
        assertThat(obj).isNotNull();
        assertThat(obj).isInstanceOf(Object.class);
    }

    @Test
    void returnsAdapterBeanIfCorrectType() throws Exception {
        Object obj = config.postProcessBeforeInitialization(adapter, "irrelevant");
        assertThat(obj).isInstanceOf(RequestMappingHandlerAdapter.class);
    }

    @Test
    void setsCustomerResolver() throws Exception {
        config.postProcessBeforeInitialization(adapter, "irrelevant");
        verify(adapter).setCustomArgumentResolvers(anyList());
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
