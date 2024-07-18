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

package com.mattbertolini.spring.web.reactive.bind.resolver;

import com.mattbertolini.spring.web.bind.annotation.SessionParameter;
import com.mattbertolini.spring.web.bind.introspect.BindingProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.lang.Nullable;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.mock.web.server.MockWebSession;
import reactor.core.publisher.Mono;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class SessionParameterRequestPropertyResolverTest {
    private SessionParameterRequestPropertyResolver resolver;

    @BeforeEach
    void setUp() {
        resolver = new SessionParameterRequestPropertyResolver();
    }

    @Test
    void supportsReturnsTrueOnPresenceOfAnnotation() throws Exception {
        boolean result = resolver.supports(bindingProperty("annotated"));
        assertThat(result).isTrue();
    }

    @Test
    void supportsReturnsFalseOnMissingAnnotation() throws Exception {
        boolean result = resolver.supports(bindingProperty("notAnnotated"));
        assertThat(result).isFalse();
    }

    @Test
    void throwsExceptionIfResolveCalledWithNoAnnotation() throws Exception {
        // Unlikely to happen as the library always checks the supports method.
        MockServerHttpRequest request = MockServerHttpRequest.get("/irrelevant")
            .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        BindingProperty bindingProperty = bindingProperty("notAnnotated");
        assertThatExceptionOfType(IllegalStateException.class)
            .isThrownBy(() -> resolver.resolve(bindingProperty, exchange));
    }

    @Test
    void returnsValueFromSession() throws Exception {
        String expected = "expectedValue";
        String sessionKey = "sessionKey";

        MockServerHttpRequest request = MockServerHttpRequest.get("/irrelevant").build();
        MockWebSession webSession = new MockWebSession();
        webSession.getAttributes().put(sessionKey, expected);
        MockServerWebExchange exchange = MockServerWebExchange.builder(request)
            .session(webSession)
            .build();

        Mono<Object> actual = resolver.resolve(bindingProperty("annotated"), exchange);
        assertThat(actual.block()).isEqualTo(expected);
    }

    @Test
    void returnsNullWhenNoKeyFound() throws Exception {
        MockServerHttpRequest request = MockServerHttpRequest.get("/irrelevant").build();
        MockWebSession webSession = new MockWebSession();
        webSession.getAttributes().clear();
        MockServerWebExchange exchange = MockServerWebExchange.builder(request).session(webSession).build();
        Mono<Object> actual = resolver.resolve(bindingProperty("annotated"), exchange);
        assertThat(actual.block()).isNull();
    }

    @Test
    void returnsNullWhenNoSessionExists() throws Exception {
        MockServerHttpRequest request = MockServerHttpRequest.get("/irrelevant").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        Mono<Object> actual = resolver.resolve(bindingProperty("annotated"), exchange);
        assertThat(actual.block()).isNull();
    }

    private BindingProperty bindingProperty(String propertyName) throws IntrospectionException {
        return BindingProperty.forPropertyDescriptor(new PropertyDescriptor(propertyName, TestingBean.class));
    }

    @SuppressWarnings("unused")
    private static class TestingBean {
        @Nullable
        @SessionParameter("sessionKey")
        private String annotated;

        @Nullable
        private String notAnnotated;

        @Nullable
        public String getAnnotated() {
            return annotated;
        }

        public void setAnnotated(String annotated) {
            this.annotated = annotated;
        }

        @Nullable
        public String getNotAnnotated() {
            return notAnnotated;
        }

        public void setNotAnnotated(String notAnnotated) {
            this.notAnnotated = notAnnotated;
        }
    }
}
