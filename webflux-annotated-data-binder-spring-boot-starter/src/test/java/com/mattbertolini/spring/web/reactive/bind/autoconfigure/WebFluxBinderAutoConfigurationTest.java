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
package com.mattbertolini.spring.web.reactive.bind.autoconfigure;

import com.mattbertolini.spring.web.reactive.bind.config.BinderConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.runner.ReactiveWebApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

import static org.assertj.core.api.Assertions.assertThat;

class WebFluxBinderAutoConfigurationTest {
    private final ReactiveWebApplicationContextRunner contextRunner = new ReactiveWebApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(WebFluxBinderAutoConfiguration.class));

    @Test
    void autoConfiguredWithPackagesToScan() {
        contextRunner.withUserConfiguration(EmptyConfig.class).run(context -> {
            assertThat(context).hasSingleBean(BinderConfiguration.class);
            BinderConfiguration binderConfiguration = context.getBean(BinderConfiguration.class);
            assertThat(binderConfiguration.getPackagesToScan()).hasSize(1)
                .contains(EmptyConfig.class.getPackage().getName());
        });
    }

    @Test
    void noPackagesToScanWhenAutoConfigurationNotEnabled() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(BinderConfiguration.class);
            BinderConfiguration binderConfiguration = context.getBean(BinderConfiguration.class);
            assertThat(binderConfiguration.getPackagesToScan()).isEmpty();
        });
    }

    @Test
    void overridesAutoConfigurationWhenBeanIsDefined() {
        contextRunner.withUserConfiguration(OverrideBeanDefinition.class).run(context -> {
            assertThat(context).hasSingleBean(BinderConfiguration.class);
            assertThat(context).getBean("customBinderConfig")
                .isEqualTo(context.getBean(BinderConfiguration.class));
            BinderConfiguration binderConfiguration = context.getBean(BinderConfiguration.class);
            assertThat(binderConfiguration.getPackagesToScan())
                .containsOnly("com.mattbertolini.override");
        });
    }

    @SpringBootApplication(proxyBeanMethods = false)
    @ComponentScan(excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = OverrideBeanDefinition.class)
    })
    private static class EmptyConfig {}

    @SpringBootApplication(proxyBeanMethods = false)
    private static class OverrideBeanDefinition {
        @Bean
        public BinderConfiguration customBinderConfig() {
            return new BinderConfiguration().addPackageToScan("com.mattbertolini.override");
        }
    }
}
