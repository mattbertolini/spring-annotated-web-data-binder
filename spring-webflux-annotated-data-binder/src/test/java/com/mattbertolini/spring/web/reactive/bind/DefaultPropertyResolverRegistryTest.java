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

package com.mattbertolini.spring.web.reactive.bind;

import com.mattbertolini.spring.web.reactive.bind.resolver.CookieParameterRequestPropertyResolver;
import com.mattbertolini.spring.web.reactive.bind.resolver.FormParameterMapRequestPropertyResolver;
import com.mattbertolini.spring.web.reactive.bind.resolver.FormParameterRequestPropertyResolver;
import com.mattbertolini.spring.web.reactive.bind.resolver.HeaderParameterMapRequestPropertyResolver;
import com.mattbertolini.spring.web.reactive.bind.resolver.HeaderParameterRequestPropertyResolver;
import com.mattbertolini.spring.web.reactive.bind.resolver.PathParameterMapRequestPropertyResolver;
import com.mattbertolini.spring.web.reactive.bind.resolver.PathParameterRequestPropertyResolver;
import com.mattbertolini.spring.web.reactive.bind.resolver.RequestContextRequestPropertyResolver;
import com.mattbertolini.spring.web.reactive.bind.resolver.RequestParameterMapRequestPropertyResolver;
import com.mattbertolini.spring.web.reactive.bind.resolver.RequestParameterRequestPropertyResolver;
import com.mattbertolini.spring.web.reactive.bind.resolver.SessionParameterRequestPropertyResolver;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultPropertyResolverRegistryTest {
    @Test
    void registryShouldHaveRequiredResolvers() {
        // I'm maintaining order here. Not sure if it's necessary, but I like it right now.
        DefaultPropertyResolverRegistry registry = new DefaultPropertyResolverRegistry();
        assertThat(registry.getPropertyResolvers())
            .extractingResultOf("getClass", Class.class)
            .containsExactly(
                RequestParameterRequestPropertyResolver.class,
                RequestParameterMapRequestPropertyResolver.class,
                FormParameterRequestPropertyResolver.class,
                FormParameterMapRequestPropertyResolver.class,
                PathParameterRequestPropertyResolver.class,
                PathParameterMapRequestPropertyResolver.class,
                CookieParameterRequestPropertyResolver.class,
                HeaderParameterRequestPropertyResolver.class,
                HeaderParameterMapRequestPropertyResolver.class,
                SessionParameterRequestPropertyResolver.class,
                RequestContextRequestPropertyResolver.class
            );
    }
}
