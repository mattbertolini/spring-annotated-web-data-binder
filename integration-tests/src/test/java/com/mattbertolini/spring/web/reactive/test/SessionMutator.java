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

package com.mattbertolini.spring.web.reactive.test;

import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.lang.NonNull;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClientConfigurer;
import org.springframework.web.server.adapter.WebHttpHandlerBuilder;

import java.util.HashMap;
import java.util.Map;

class SessionMutator implements WebTestClientConfigurer {
    private final Map<String, String> attributes = new HashMap<>();

    public static SessionMutator session() {
        return new SessionMutator();
    }

    public SessionMutator attribute(String name, String value) {
        attributes.put(name, value);
        return this;
    }

    @Override
    public void afterConfigurerAdded(@NonNull WebTestClient.Builder builder, WebHttpHandlerBuilder httpHandlerBuilder, ClientHttpConnector connector) {
        httpHandlerBuilder.filter(new SessionFilter(attributes));
    }
}
