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
import org.springframework.core.ResolvableType;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.mock.web.server.MockWebSession;
import reactor.core.publisher.Mono;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;

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
        boolean result = resolver.supports(bindingProperty("annotated", TestingBean.class));
        assertThat(result).isTrue();
    }

    @Test
    void supportsReturnsFalseOnMissingAnnotation() throws Exception {
        boolean result = resolver.supports(bindingProperty("notAnnotated", TestingBean.class));
        assertThat(result).isFalse();
    }

    @Test
    void throwsExceptionIfResolveCalledWithNoAnnotation() {
        // Unlikely to happen as the library always checks the supports method.
        MockServerHttpRequest request = MockServerHttpRequest.get("/irrelevant")
            .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        assertThatExceptionOfType(IllegalStateException.class)
            .isThrownBy(() -> resolver.resolve(typeDescriptor(String.class), exchange));
    }

    @Test
    void returnsValueFromSession() {
        String expected = "expectedValue";
        String sessionKey = "sessionKey";

        MockServerHttpRequest request = MockServerHttpRequest.get("/irrelevant").build();
        MockWebSession webSession = new MockWebSession();
        webSession.getAttributes().put(sessionKey, expected);
        MockServerWebExchange exchange = MockServerWebExchange.builder(request)
            .session(webSession)
            .build();

        Mono<Object> actual = resolver.resolve(typeDescriptor(String.class, new StubbingAnnotation(sessionKey)), exchange);
        assertThat(actual.block()).isEqualTo(expected);
    }

    @Test
    void returnsNullWhenNoKeyFound() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/irrelevant").build();
        MockWebSession webSession = new MockWebSession();
        webSession.getAttributes().clear();
        MockServerWebExchange exchange = MockServerWebExchange.builder(request).session(webSession).build();
        Mono<Object> actual = resolver.resolve(typeDescriptor(Integer.class, new StubbingAnnotation("not_found")), exchange);
        assertThat(actual.block()).isNull();
    }

    @Test
    void returnsNullWhenNoSessionExists() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/irrelevant").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        Mono<Object> actual = resolver.resolve(typeDescriptor(String.class, new StubbingAnnotation("no_session")), exchange);
        assertThat(actual.block()).isNull();
    }

    private TypeDescriptor typeDescriptor(Class<?> clazz, Annotation... annotations) {
        return new TypeDescriptor(ResolvableType.forClass(clazz), null, annotations);
    }

    private BindingProperty bindingProperty(String propertyName, Class<?> clazz) throws IntrospectionException {
        return BindingProperty.forPropertyDescriptor(new PropertyDescriptor(propertyName, clazz));
    }

    @SuppressWarnings("ClassExplicitlyAnnotation")
    private static class StubbingAnnotation implements SessionParameter {
        private final String value;

        private StubbingAnnotation(String value) {
            this.value = value;
        }

        @Override
        public String value() {
            return value;
        }

        @Override
        public Class<? extends Annotation> annotationType() {
            return SessionParameter.class;
        }
    }

    @SuppressWarnings("unused")
    private static class TestingBean {
        @SessionParameter("irrelevant")
        private String annotated;

        private String notAnnotated;

        public String getAnnotated() {
            return annotated;
        }

        public void setAnnotated(String annotated) {
            this.annotated = annotated;
        }

        public String getNotAnnotated() {
            return notAnnotated;
        }

        public void setNotAnnotated(String notAnnotated) {
            this.notAnnotated = notAnnotated;
        }
    }
}
