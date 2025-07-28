/*
 * Copyright 2025 the original author or authors.
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
package com.mattbertolini.spring.web.reactive.bind.resolver;

import com.mattbertolini.spring.web.bind.annotation.RequestContext;
import com.mattbertolini.spring.web.bind.introspect.BindingProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.i18n.TimeZoneAwareLocaleContext;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.lang.Nullable;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.mock.web.server.MockWebSession;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.time.Duration;
import java.time.ZoneId;
import java.util.Locale;
import java.util.TimeZone;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RequestContextRequestPropertyResolverTest {
    private RequestContextRequestPropertyResolver resolver;

    @BeforeEach
    void setUp() {
        resolver = new RequestContextRequestPropertyResolver();
    }

    @Test
    void doesNotSupportTypesNotAnnotatedWithRequestContext() throws Exception {
        assertThat(resolver.supports(bindingProperty("notAnnotated"))).isFalse();
    }

    @Test
    void doesNotSupportUnknownType() throws Exception {
        assertThat(resolver.supports(bindingProperty("notKnown"))).isFalse();
    }

    @Test
    void throwsExceptionOnUnknownType() throws Exception {
        BindingProperty bindingProperty = bindingProperty("notKnown");
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/irrelevant").build());
        assertThatThrownBy(() -> resolver.resolve(bindingProperty, exchange))
            .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void resolvesServerWebExchange() throws Exception {
        BindingProperty bindingProperty = bindingProperty("serverWebExchange");
        assertThat(resolver.supports(bindingProperty)).isTrue();

        MockServerHttpRequest request = MockServerHttpRequest.get("/irrelevant").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        Mono<Object> objectMono = resolver.resolve(bindingProperty, exchange);
        Object actual = objectMono.block(Duration.ofSeconds(5));
        assertThat(actual).isNotNull().isInstanceOf(ServerWebExchange.class);
    }

    @Test
    void resolvesServerHttpRequest() throws Exception {
        BindingProperty bindingProperty = bindingProperty("serverHttpRequest");
        assertThat(resolver.supports(bindingProperty)).isTrue();

        MockServerHttpRequest request = MockServerHttpRequest.get("/irrelevant").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        Mono<Object> objectMono = resolver.resolve(bindingProperty, exchange);
        Object actual = objectMono.block(Duration.ofSeconds(5));
        assertThat(actual).isNotNull().isInstanceOf(ServerHttpRequest.class);
    }

    @Test
    void resolvesWebSession() throws Exception {
        BindingProperty bindingProperty = bindingProperty("webSession");
        assertThat(resolver.supports(bindingProperty)).isTrue();

        MockServerHttpRequest request = MockServerHttpRequest.get("/irrelevant").build();
        MockWebSession webSession = new MockWebSession();
        webSession.getAttributes().put("foo", "bar");
        MockServerWebExchange exchange = MockServerWebExchange.builder(request)
            .session(webSession)
            .build();

        Mono<Object> objectMono = resolver.resolve(bindingProperty, exchange);
        Object actual = objectMono.block(Duration.ofSeconds(5));
        assertThat(actual).isNotNull().isInstanceOf(WebSession.class);
    }

    @Test
    void resolvesHttpMethod() throws Exception {
        BindingProperty bindingProperty = bindingProperty("httpMethod");
        assertThat(resolver.supports(bindingProperty)).isTrue();

        MockServerHttpRequest request = MockServerHttpRequest.post("/irrelevant").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        
        Mono<Object> objectMono = resolver.resolve(bindingProperty, exchange);
        Object actual = objectMono.block(Duration.ofSeconds(5));
        assertThat(actual).isNotNull()
            .isInstanceOf(HttpMethod.class)
            .isEqualTo(HttpMethod.POST);
    }

    @Test
    void resolvesLocale() throws Exception {
        BindingProperty bindingProperty = bindingProperty("locale");
        assertThat(resolver.supports(bindingProperty)).isTrue();

        MockServerHttpRequest request = MockServerHttpRequest.get("/irrelevant")
            .acceptLanguageAsLocales(Locale.US)
            .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        Mono<Object> objecMono = resolver.resolve(bindingProperty, exchange);
        Object actual = objecMono.block(Duration.ofSeconds(5));
        assertThat(actual).isNotNull()
            .isInstanceOf(Locale.class)
            .isEqualTo(Locale.US);
    }

    @Test
    void resolvesTimeZone() throws Exception {
        BindingProperty bindingProperty = bindingProperty("timeZone");
        TimeZone expected = TimeZone.getTimeZone("America/New_York");

        TimeZoneAwareLocaleContext localeContext = mock(TimeZoneAwareLocaleContext.class);
        when(localeContext.getTimeZone()).thenReturn(expected);
        ServerWebExchange serverWebExchange = mock(ServerWebExchange.class);
        when(serverWebExchange.getLocaleContext()).thenReturn(localeContext);

        Mono<Object> objectMono = resolver.resolve(bindingProperty, serverWebExchange);
        Object actual = objectMono.block(Duration.ofSeconds(5));
        assertThat(actual).isNotNull()
            .isInstanceOf(TimeZone.class)
            .isEqualTo(expected);
    }

    @Test
    void resolvesTimeZoneWithDefault() throws Exception {
        BindingProperty bindingProperty = bindingProperty("timeZone");
        assertThat(resolver.supports(bindingProperty)).isTrue();

        MockServerHttpRequest request = MockServerHttpRequest.get("/irrelevant").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        Mono<Object> objectMono = resolver.resolve(bindingProperty, exchange);
        Object actual = objectMono.block(Duration.ofSeconds(5));
        assertThat(actual).isNotNull()
            .isInstanceOf(TimeZone.class)
            .isEqualTo(TimeZone.getDefault());
    }

    @Test
    void resolvesZoneId() throws Exception {
        BindingProperty bindingProperty = bindingProperty("zoneId");
        TimeZone timeZone = TimeZone.getTimeZone("America/New_York");
        ZoneId expected = timeZone.toZoneId();

        TimeZoneAwareLocaleContext localeContext = mock(TimeZoneAwareLocaleContext.class);
        when(localeContext.getTimeZone()).thenReturn(timeZone);
        ServerWebExchange serverWebExchange = mock(ServerWebExchange.class);
        when(serverWebExchange.getLocaleContext()).thenReturn(localeContext);

        Mono<Object> objectMono = resolver.resolve(bindingProperty, serverWebExchange);
        Object actual = objectMono.block(Duration.ofSeconds(5));
        assertThat(actual).isNotNull()
            .isInstanceOf(ZoneId.class)
            .isEqualTo(expected);
    }

    @Test
    void resolvesZoneIdWithDefault() throws Exception  {
        BindingProperty bindingProperty = bindingProperty("zoneId");
        assertThat(resolver.supports(bindingProperty)).isTrue();

        MockServerHttpRequest request = MockServerHttpRequest.get("/irrelevant").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        
        Mono<Object> objectMono = resolver.resolve(bindingProperty, exchange);
        Object actual = objectMono.block(Duration.ofSeconds(5));
        assertThat(actual).isNotNull()
            .isInstanceOf(ZoneId.class)
            .isEqualTo(ZoneId.systemDefault());
    }

    private BindingProperty bindingProperty(String property) throws IntrospectionException {
        return BindingProperty.forPropertyDescriptor(new PropertyDescriptor(property, TestingBean.class));
    }

    private static class NotKnown {}

    @SuppressWarnings("unused")
    private static class TestingBean {
        @Nullable
        private ServerWebExchange notAnnotated;

        @Nullable
        @RequestContext
        private NotKnown notKnown;

        @Nullable
        @RequestContext
        private ServerWebExchange serverWebExchange;

        @Nullable
        @RequestContext
        private ServerHttpRequest serverHttpRequest;

        @Nullable
        @RequestContext
        private WebSession webSession;

        @Nullable
        @RequestContext
        private HttpMethod httpMethod;

        @Nullable
        @RequestContext
        private Locale locale;

        @Nullable
        @RequestContext
        private TimeZone timeZone;

        @Nullable
        @RequestContext
        private ZoneId zoneId;

        @Nullable
        public ServerWebExchange getNotAnnotated() {
            return notAnnotated;
        }

        public void setNotAnnotated(ServerWebExchange notAnnotated) {
            this.notAnnotated = notAnnotated;
        }

        @Nullable
        public NotKnown getNotKnown() {
            return notKnown;
        }

        public void setNotKnown(NotKnown notKnown) {
            this.notKnown = notKnown;
        }

        @Nullable
        public ServerWebExchange getServerWebExchange() {
            return serverWebExchange;
        }

        public void setServerWebExchange(ServerWebExchange serverWebExchange) {
            this.serverWebExchange = serverWebExchange;
        }

        @Nullable
        public ServerHttpRequest getServerHttpRequest() {
            return serverHttpRequest;
        }

        public void setServerHttpRequest(ServerHttpRequest serverHttpRequest) {
            this.serverHttpRequest = serverHttpRequest;
        }

        @Nullable
        public WebSession getWebSession() {
            return webSession;
        }

        public void setWebSession(WebSession webSession) {
            this.webSession = webSession;
        }

        @Nullable
        public HttpMethod getHttpMethod() {
            return httpMethod;
        }

        public void setHttpMethod(HttpMethod httpMethod) {
            this.httpMethod = httpMethod;
        }

        @Nullable
        public Locale getLocale() {
            return locale;
        }

        public void setLocale(Locale locale) {
            this.locale = locale;
        }

        @Nullable
        public TimeZone getTimeZone() {
            return timeZone;
        }

        public void setTimeZone(TimeZone timeZone) {
            this.timeZone = timeZone;
        }

        @Nullable
        public ZoneId getZoneId() {
            return zoneId;
        }

        public void setZoneId(ZoneId zoneId) {
            this.zoneId = zoneId;
        }
    }
}
