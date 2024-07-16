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

package com.mattbertolini.spring.web.servlet.mvc.bind.autoconfigure;

import com.mattbertolini.spring.web.servlet.mvc.bind.PropertyResolverRegistry;
import com.mattbertolini.spring.web.servlet.mvc.bind.config.BinderConfiguration;
import com.mattbertolini.spring.web.servlet.mvc.bind.resolver.RequestPropertyResolver;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Role;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@AutoConfiguration
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
@ConditionalOnMissingBean(BinderConfiguration.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class WebMvcBinderAutoConfiguration {
    private final List<String> packagesToScan = new LinkedList<>();
    private final Set<RequestPropertyResolver> customResolvers = new LinkedHashSet<>();
    private final Set<PropertyResolverRegistry> propertyResolverRegistries = new LinkedHashSet<>();
    
    public WebMvcBinderAutoConfiguration(BeanFactory beanFactory,
                                         ObjectProvider<List<RequestPropertyResolver>> customResolvers,
                                         ObjectProvider<List<PropertyResolverRegistry>> propertyResolverRegistries) {
        if (AutoConfigurationPackages.has(beanFactory)) {
            packagesToScan.addAll(AutoConfigurationPackages.get(beanFactory));
        }
        customResolvers.ifAvailable(this.customResolvers::addAll);
        propertyResolverRegistries.ifAvailable(this.propertyResolverRegistries::addAll);
    }

    @Bean
    public BinderConfiguration binderConfiguration() {
        BinderConfiguration binderConfiguration = new BinderConfiguration();
        packagesToScan.forEach(binderConfiguration::addPackageToScan);
        binderConfiguration.addResolvers(customResolvers);
        propertyResolverRegistries.forEach(binderConfiguration::addResolvers);
        return binderConfiguration;
    }
}
