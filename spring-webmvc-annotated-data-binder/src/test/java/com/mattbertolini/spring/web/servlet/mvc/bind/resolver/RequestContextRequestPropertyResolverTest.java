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
import com.mattbertolini.spring.web.bind.introspect.BindingProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
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
    void doesNotSupportTypesNotAnnotatedWithRequestContext() throws Exception {
        assertThat(resolver.supports(bindingProperty("notAnnotated", TestingBean.class))).isFalse();
    }

    @Test
    void doesNotSupportUnknownType() throws Exception {
        assertThat(resolver.supports(bindingProperty("notKnown", TestingBean.class))).isFalse();
    }

    @Test
    void throwsExceptionOnUnknownType() throws Exception {
        BindingProperty bindingProperty = bindingProperty("notKnown", TestingBean.class);
        assertThatThrownBy(() -> resolver.resolve(typeDescriptor(NotKnown.class), bindingProperty, request))
            .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void throwsExceptionWhenNativeRequestDoesNotWrapServletRequest() throws Exception {
        BindingProperty bindingProperty = bindingProperty("nativeWebRequest", TestingBean.class);
        NativeWebRequest webRequest = mock(NativeWebRequest.class);
        when(webRequest.getNativeRequest()).thenReturn(null);
        assertThatThrownBy(() -> resolver.resolve(typeDescriptor(HttpServletRequest.class), bindingProperty, webRequest))
            .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void resolvesWebRequestType() throws Exception {
        BindingProperty bindingProperty = bindingProperty("webRequest", TestingBean.class);
        assertThat(resolver.supports(bindingProperty)).isTrue();

        Object actual = resolver.resolve(typeDescriptor(WebRequest.class), bindingProperty, request);
        assertThat(actual).isNotNull();
        assertThat(actual).isInstanceOf(WebRequest.class);
    }

    @Test
    void resolvesServletRequest() throws Exception {
        BindingProperty bindingProperty = bindingProperty("servletRequest", TestingBean.class);
        assertThat(resolver.supports(bindingProperty)).isTrue();

        Object actual = resolver.resolve(typeDescriptor(ServletRequest.class), bindingProperty, request);
        assertThat(actual).isNotNull();
        assertThat(actual).isInstanceOf(ServletRequest.class);
    }

    @Test
    void resolvesHttpSession() throws Exception {
        BindingProperty bindingProperty = bindingProperty("httpSession", TestingBean.class);
        assertThat(resolver.supports(bindingProperty)).isTrue();

        servletRequest.setSession(new MockHttpSession());
        Object actual = resolver.resolve(typeDescriptor(HttpSession.class), bindingProperty, request);
        assertThat(actual).isNotNull();
        assertThat(actual).isInstanceOf(HttpSession.class);
    }

    @Test
    void resolvesHttpMethod() throws Exception {
        BindingProperty bindingProperty = bindingProperty("httpMethod", TestingBean.class);
        assertThat(resolver.supports(bindingProperty)).isTrue();

        servletRequest.setMethod("POST");
        Object actual = resolver.resolve(typeDescriptor(HttpMethod.class), bindingProperty, request);
        assertThat(actual).isNotNull();
        assertThat(actual).isInstanceOf(HttpMethod.class);
        assertThat(actual).isEqualTo(HttpMethod.POST);
    }

    @Test
    void resolvesLocale() throws Exception {
        BindingProperty bindingProperty = bindingProperty("locale", TestingBean.class);
        assertThat(resolver.supports(bindingProperty)).isTrue();

        servletRequest.addHeader("Accept-Language", "en-US");
        Object actual = resolver.resolve(typeDescriptor(Locale.class), bindingProperty, request);
        assertThat(actual).isNotNull();
        assertThat(actual).isInstanceOf(Locale.class);
        assertThat(actual).isEqualTo(Locale.US);
    }

    @Test
    void resolvesTimeZone() throws Exception {
        BindingProperty bindingProperty = bindingProperty("timeZone", TestingBean.class);
        assertThat(resolver.supports(bindingProperty)).isTrue();

        TimeZone expected = TimeZone.getTimeZone("America/New_York");
        servletRequest.setAttribute(DispatcherServlet.LOCALE_RESOLVER_ATTRIBUTE, new FixedLocaleResolver(Locale.US, expected));
        Object actual = resolver.resolve(typeDescriptor(TimeZone.class), bindingProperty, request);
        assertThat(actual).isNotNull();
        assertThat(actual).isInstanceOf(TimeZone.class);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void resolvesTimeZoneWithDefault() throws Exception {
        BindingProperty bindingProperty = bindingProperty("timeZone", TestingBean.class);
        TimeZone expected = TimeZone.getDefault();
        Object actual = resolver.resolve(typeDescriptor(TimeZone.class), bindingProperty, request);
        assertThat(actual).isNotNull();
        assertThat(actual).isInstanceOf(TimeZone.class);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void resolvesZoneId() throws Exception {
        BindingProperty bindingProperty = bindingProperty("zoneId", TestingBean.class);
        assertThat(resolver.supports(bindingProperty)).isTrue();

        TimeZone timeZone = TimeZone.getTimeZone("America/New_York");
        ZoneId expected = timeZone.toZoneId();
        servletRequest.setAttribute(DispatcherServlet.LOCALE_RESOLVER_ATTRIBUTE, new FixedLocaleResolver(Locale.US, timeZone));
        Object actual = resolver.resolve(typeDescriptor(ZoneId.class), bindingProperty, request);
        assertThat(actual).isNotNull();
        assertThat(actual).isInstanceOf(ZoneId.class);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void resolvesZoneIdWithDefault() throws Exception {
        BindingProperty bindingProperty = bindingProperty("zoneId", TestingBean.class);
        TimeZone timeZone = TimeZone.getDefault();
        ZoneId expected = timeZone.toZoneId();
        Object actual = resolver.resolve(typeDescriptor(ZoneId.class), bindingProperty, request);
        assertThat(actual).isNotNull();
        assertThat(actual).isInstanceOf(ZoneId.class);
        assertThat(actual).isEqualTo(expected);
    }

    private TypeDescriptor typeDescriptor(Class<?> clazz) {
        return new TypeDescriptor(ResolvableType.forClass(clazz), null, new StubbingAnnotation[]{new StubbingAnnotation()});
    }

    private BindingProperty bindingProperty(String property, Class<?> clazz) throws IntrospectionException {
        return BindingProperty.forPropertyDescriptor(new PropertyDescriptor(property, clazz));
    }

    private static class NotKnown {}

    @SuppressWarnings("ClassExplicitlyAnnotation")
    private static class StubbingAnnotation implements RequestContext {
        @Override
        public Class<? extends Annotation> annotationType() {
            return RequestContext.class;
        }
    }

    @SuppressWarnings("unused")
    private static class TestingBean {
        private WebRequest notAnnotated;

        @RequestContext
        private NotKnown notKnown;

        @RequestContext
        private NativeWebRequest nativeWebRequest;

        @RequestContext
        private ServletRequest servletRequest;

        @RequestContext
        private WebRequest webRequest;

        @RequestContext
        private HttpSession httpSession;

        @RequestContext
        private HttpMethod httpMethod;

        @RequestContext
        private Locale locale;

        @RequestContext
        private TimeZone timeZone;

        @RequestContext
        private ZoneId zoneId;

        public WebRequest getNotAnnotated() {
            return notAnnotated;
        }

        public void setNotAnnotated(WebRequest notAnnotated) {
            this.notAnnotated = notAnnotated;
        }

        public NotKnown getNotKnown() {
            return notKnown;
        }

        public void setNotKnown(NotKnown notKnown) {
            this.notKnown = notKnown;
        }

        public NativeWebRequest getNativeWebRequest() {
            return nativeWebRequest;
        }

        public void setNativeWebRequest(NativeWebRequest nativeWebRequest) {
            this.nativeWebRequest = nativeWebRequest;
        }

        public ServletRequest getServletRequest() {
            return servletRequest;
        }

        public void setServletRequest(ServletRequest servletRequest) {
            this.servletRequest = servletRequest;
        }

        public WebRequest getWebRequest() {
            return webRequest;
        }

        public void setWebRequest(WebRequest webRequest) {
            this.webRequest = webRequest;
        }

        public HttpSession getHttpSession() {
            return httpSession;
        }

        public void setHttpSession(HttpSession httpSession) {
            this.httpSession = httpSession;
        }

        public HttpMethod getHttpMethod() {
            return httpMethod;
        }

        public void setHttpMethod(HttpMethod httpMethod) {
            this.httpMethod = httpMethod;
        }

        public Locale getLocale() {
            return locale;
        }

        public void setLocale(Locale locale) {
            this.locale = locale;
        }

        public TimeZone getTimeZone() {
            return timeZone;
        }

        public void setTimeZone(TimeZone timeZone) {
            this.timeZone = timeZone;
        }

        public ZoneId getZoneId() {
            return zoneId;
        }

        public void setZoneId(ZoneId zoneId) {
            this.zoneId = zoneId;
        }
    }
}
