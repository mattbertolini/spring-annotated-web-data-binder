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

package com.mattbertolini.spring.web.servlet.mvc.bind.resolver;

import com.mattbertolini.spring.web.bind.annotation.RequestContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.ResolvableType;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.i18n.FixedLocaleResolver;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.lang.annotation.Annotation;
import java.time.ZoneId;
import java.util.Locale;
import java.util.TimeZone;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RequestContextRequestPropertyResolverTest {
    private RequestContextRequestPropertyResolver resolver;
    private ServletWebRequest request;
    private MockHttpServletRequest servletRequest;

    @BeforeEach
    void setUp() {
        resolver = new RequestContextRequestPropertyResolver();
        servletRequest = new MockHttpServletRequest();
        request = new ServletWebRequest(servletRequest);
    }

    @Test
    void doesNotSupportTypesNotAnnotatedWithRequestContext() {
        TypeDescriptor typeDescriptor = new TypeDescriptor(ResolvableType.forClass(WebRequest.class), null, null);
        assertThat(resolver.supports(typeDescriptor)).isFalse();
    }

    @Test
    void doesNotSupportUnknownType() {
        assertThat(resolver.supports(typeDescriptor(NotKnown.class))).isFalse();
    }

    @Test
    void throwsExceptionOnUnknownType() {
        assertThatThrownBy(() -> resolver.resolve(typeDescriptor(NotKnown.class), request))
            .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void throwsExceptionWhenNativeRequestDoesNotWrapServletRequest() {
        NativeWebRequest webRequest = mock(NativeWebRequest.class);
        when(webRequest.getNativeRequest()).thenReturn(null);
        assertThatThrownBy(() -> resolver.resolve(typeDescriptor(HttpServletRequest.class), webRequest))
            .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void resolvesWebRequestType() {
        assertThat(resolver.supports(typeDescriptor(WebRequest.class))).isTrue();

        Object actual = resolver.resolve(typeDescriptor(WebRequest.class), request);
        assertThat(actual).isNotNull();
        assertThat(actual).isInstanceOf(WebRequest.class);
    }

    @Test
    void resolvesServletRequest() {
        assertThat(resolver.supports(typeDescriptor(ServletRequest.class))).isTrue();

        Object actual = resolver.resolve(typeDescriptor(ServletRequest.class), request);
        assertThat(actual).isNotNull();
        assertThat(actual).isInstanceOf(ServletRequest.class);
    }

    @Test
    void resolvesHttpSession() {
        assertThat(resolver.supports(typeDescriptor(HttpSession.class))).isTrue();

        servletRequest.setSession(new MockHttpSession());
        Object actual = resolver.resolve(typeDescriptor(HttpSession.class), request);
        assertThat(actual).isNotNull();
        assertThat(actual).isInstanceOf(HttpSession.class);
    }

    @Test
    void resolvesHttpMethod() {
        assertThat(resolver.supports(typeDescriptor(HttpMethod.class))).isTrue();

        servletRequest.setMethod("POST");
        Object actual = resolver.resolve(typeDescriptor(HttpMethod.class), request);
        assertThat(actual).isNotNull();
        assertThat(actual).isInstanceOf(HttpMethod.class);
        assertThat(actual).isEqualTo(HttpMethod.POST);
    }

    @Test
    void resolvesLocale() {
        assertThat(resolver.supports(typeDescriptor(Locale.class))).isTrue();

        servletRequest.addHeader("Accept-Language", "en-US");
        Object actual = resolver.resolve(typeDescriptor(Locale.class), request);
        assertThat(actual).isNotNull();
        assertThat(actual).isInstanceOf(Locale.class);
        assertThat(actual).isEqualTo(Locale.US);
    }

    @Test
    void resolvesTimeZone() {
        assertThat(resolver.supports(typeDescriptor(TimeZone.class))).isTrue();

        TimeZone expected = TimeZone.getTimeZone("America/New_York");
        servletRequest.setAttribute(DispatcherServlet.LOCALE_RESOLVER_ATTRIBUTE, new FixedLocaleResolver(Locale.US, expected));
        Object actual = resolver.resolve(typeDescriptor(TimeZone.class), request);
        assertThat(actual).isNotNull();
        assertThat(actual).isInstanceOf(TimeZone.class);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void resolvesTimeZoneWithDefault() {
        TimeZone expected = TimeZone.getDefault();
        Object actual = resolver.resolve(typeDescriptor(TimeZone.class), request);
        assertThat(actual).isNotNull();
        assertThat(actual).isInstanceOf(TimeZone.class);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void resolvesZoneId() {
        assertThat(resolver.supports(typeDescriptor(ZoneId.class))).isTrue();

        TimeZone timeZone = TimeZone.getTimeZone("America/New_York");
        ZoneId expected = timeZone.toZoneId();
        servletRequest.setAttribute(DispatcherServlet.LOCALE_RESOLVER_ATTRIBUTE, new FixedLocaleResolver(Locale.US, timeZone));
        Object actual = resolver.resolve(typeDescriptor(ZoneId.class), request);
        assertThat(actual).isNotNull();
        assertThat(actual).isInstanceOf(ZoneId.class);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void resolvesZoneIdWithDefault() {
        TimeZone timeZone = TimeZone.getDefault();
        ZoneId expected = timeZone.toZoneId();
        Object actual = resolver.resolve(typeDescriptor(ZoneId.class), request);
        assertThat(actual).isNotNull();
        assertThat(actual).isInstanceOf(ZoneId.class);
        assertThat(actual).isEqualTo(expected);
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
