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

import com.mattbertolini.spring.web.bind.annotation.RequestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.i18n.TimeZoneAwareLocaleContext;
import org.springframework.core.ResolvableType;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.mock.web.server.MockWebSession;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

import java.lang.annotation.Annotation;
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
    void doesNotSupportTypesNotAnnotatedWithRequestContext() {
        TypeDescriptor typeDescriptor = new TypeDescriptor(ResolvableType.forClass(ServerWebExchange.class), null, null);
        assertThat(resolver.supports(typeDescriptor)).isFalse();
    }

    @Test
    void doesNotSupportUnknownType() {
        assertThat(resolver.supports(typeDescriptor(NotKnown.class))).isFalse();
    }

    @Test
    void throwsExceptionOnUnknownType() {
        assertThatThrownBy(() -> resolver.resolve(typeDescriptor(NotKnown.class), MockServerWebExchange.from(MockServerHttpRequest.get("/irrelevant").build())))
            .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void resolvesServerWebExchange() {
        assertThat(resolver.supports(typeDescriptor(ServerWebExchange.class))).isTrue();

        MockServerHttpRequest request = MockServerHttpRequest.get("/irrelevant").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        Mono<Object> objectMono = resolver.resolve(typeDescriptor(ServerWebExchange.class), exchange);
        Object actual = objectMono.block(Duration.ofSeconds(5));
        assertThat(actual).isNotNull();
        assertThat(actual).isInstanceOf(ServerWebExchange.class);
    }

    @Test
    void resolvesServerHttpRequest() {
        assertThat(resolver.supports(typeDescriptor(ServerHttpRequest.class))).isTrue();

        MockServerHttpRequest request = MockServerHttpRequest.get("/irrelevant").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        Mono<Object> objectMono = resolver.resolve(typeDescriptor(ServerHttpRequest.class), exchange);
        Object actual = objectMono.block(Duration.ofSeconds(5));
        assertThat(actual).isNotNull();
        assertThat(actual).isInstanceOf(ServerHttpRequest.class);
    }

    @Test
    void resolvesWebSession() {
        assertThat(resolver.supports(typeDescriptor(WebSession.class))).isTrue();

        MockServerHttpRequest request = MockServerHttpRequest.get("/irrelevant").build();
        MockWebSession webSession = new MockWebSession();
        webSession.getAttributes().put("foo", "bar");
        MockServerWebExchange exchange = MockServerWebExchange.builder(request)
            .session(webSession)
            .build();

        Mono<Object> objectMono = resolver.resolve(typeDescriptor(WebSession.class), exchange);
        Object actual = objectMono.block(Duration.ofSeconds(5));
        assertThat(actual).isNotNull();
        assertThat(actual).isInstanceOf(WebSession.class);
    }

    @Test
    void resolvesHttpMethod() {
        assertThat(resolver.supports(typeDescriptor(HttpMethod.class))).isTrue();

        MockServerHttpRequest request = MockServerHttpRequest.post("/irrelevant").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        
        Mono<Object> objectMono = resolver.resolve(typeDescriptor(HttpMethod.class), exchange);
        Object actual = objectMono.block(Duration.ofSeconds(5));
        assertThat(actual).isNotNull();
        assertThat(actual).isInstanceOf(HttpMethod.class);
        assertThat(actual).isEqualTo(HttpMethod.POST);
    }

    @Test
    void resolvesLocale() {
        assertThat(resolver.supports(typeDescriptor(Locale.class))).isTrue();

        MockServerHttpRequest request = MockServerHttpRequest.get("/irrelevant")
            .acceptLanguageAsLocales(Locale.US)
            .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        Mono<Object> objecMono = resolver.resolve(typeDescriptor(Locale.class), exchange);
        Object actual = objecMono.block(Duration.ofSeconds(5));
        assertThat(actual).isNotNull();
        assertThat(actual).isInstanceOf(Locale.class);
        assertThat(actual).isEqualTo(Locale.US);
    }

    @Test
    void resolvesTimeZone() {
        TimeZone expected = TimeZone.getTimeZone("America/New_York");

        TimeZoneAwareLocaleContext localeContext = mock(TimeZoneAwareLocaleContext.class);
        when(localeContext.getTimeZone()).thenReturn(expected);
        ServerWebExchange serverWebExchange = mock(ServerWebExchange.class);
        when(serverWebExchange.getLocaleContext()).thenReturn(localeContext);

        Mono<Object> objectMono = resolver.resolve(typeDescriptor(TimeZone.class), serverWebExchange);
        Object actual = objectMono.block(Duration.ofSeconds(5));
        assertThat(actual).isNotNull();
        assertThat(actual).isInstanceOf(TimeZone.class);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void resolvesTimeZoneWithDefault() {
        assertThat(resolver.supports(typeDescriptor(TimeZone.class))).isTrue();

        MockServerHttpRequest request = MockServerHttpRequest.get("/irrelevant").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        Mono<Object> objectMono = resolver.resolve(typeDescriptor(TimeZone.class), exchange);
        Object actual = objectMono.block(Duration.ofSeconds(5));
        assertThat(actual).isNotNull();
        assertThat(actual).isInstanceOf(TimeZone.class);
        assertThat(actual).isEqualTo(TimeZone.getDefault());
    }

    @Test
    void resolvesZoneId() {
        TimeZone timeZone = TimeZone.getTimeZone("America/New_York");
        ZoneId expected = timeZone.toZoneId();

        TimeZoneAwareLocaleContext localeContext = mock(TimeZoneAwareLocaleContext.class);
        when(localeContext.getTimeZone()).thenReturn(timeZone);
        ServerWebExchange serverWebExchange = mock(ServerWebExchange.class);
        when(serverWebExchange.getLocaleContext()).thenReturn(localeContext);

        Mono<Object> objectMono = resolver.resolve(typeDescriptor(ZoneId.class), serverWebExchange);
        Object actual = objectMono.block(Duration.ofSeconds(5));
        assertThat(actual).isNotNull();
        assertThat(actual).isInstanceOf(ZoneId.class);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void resolvesZoneIdWithDefault() {
        assertThat(resolver.supports(typeDescriptor(ZoneId.class))).isTrue();

        MockServerHttpRequest request = MockServerHttpRequest.get("/irrelevant").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        
        Mono<Object> objectMono = resolver.resolve(typeDescriptor(ZoneId.class), exchange);
        Object actual = objectMono.block(Duration.ofSeconds(5));
        assertThat(actual).isNotNull();
        assertThat(actual).isInstanceOf(ZoneId.class);
        assertThat(actual).isEqualTo(ZoneId.systemDefault());
    }

    private TypeDescriptor typeDescriptor(Class<?> clazz) {
        return new TypeDescriptor(ResolvableType.forClass(clazz), null, new StubbingAnnotation[]{new StubbingAnnotation()});
    }

    private static class NotKnown {}

    @SuppressWarnings("ClassExplicitlyAnnotation")
    private static class StubbingAnnotation implements RequestContext {
        @Override
        public Class<? extends Annotation> annotationType() {
            return RequestContext.class;
        }
    }
}
