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

import com.mattbertolini.spring.web.bind.annotation.CookieParameter;
import com.mattbertolini.spring.web.bind.introspect.BindingProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;

import javax.servlet.http.Cookie;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CookieParameterRequestPropertyResolverTest {

    private CookieParameterRequestPropertyResolver resolver;
    private MockHttpServletRequest servletRequest;
    private ServletWebRequest request;

    @BeforeEach
    void setUp() {
        resolver = new CookieParameterRequestPropertyResolver();
        servletRequest = new MockHttpServletRequest();
        request = new ServletWebRequest(servletRequest);
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
    void throwsExceptionIfResolveCalledWithNoAnnotation() {
        // Unlikely to happen as the library always checks the supports method.
        assertThatExceptionOfType(IllegalStateException.class)
            .isThrownBy(() -> resolver.resolve(bindingProperty("notAnnotated"), request));
    }

    @Test
    void throwsExceptionWhenNativeRequestDoesNotWrapServletRequest() {
        NativeWebRequest webRequest = mock(NativeWebRequest.class);
        when(webRequest.getNativeRequest()).thenReturn(null);
        assertThatThrownBy(() -> resolver.resolve(bindingProperty("annotated"), webRequest))
            .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void returnsValueFromCookie() throws Exception{
        String expected = "expectedValue";
        String cookieName = "the_cookie";
        Cookie cookie = new Cookie(cookieName, expected);
        servletRequest.setCookies(cookie);
        Object actual = resolver.resolve(bindingProperty("annotated"), request);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void returnsCookieObjectWhenTypeMatches() throws Exception {
        String cookieName = "the_cookie";
        Cookie expected = new Cookie(cookieName, "aValue");
        servletRequest.setCookies(expected);
        Object actual = resolver.resolve(bindingProperty("cookieObject"), request);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void returnsNullWhenNoCookieFound() throws Exception {
        Object notFound = resolver.resolve(bindingProperty("annotated"), request);
        assertThat(notFound).isNull();
    }

    private BindingProperty bindingProperty(String property) throws IntrospectionException {
        return BindingProperty.forPropertyDescriptor(new PropertyDescriptor(property, TestingBean.class));
    }

    @SuppressWarnings("unused")
    private static class TestingBean {
        @CookieParameter("the_cookie")
        private String annotated;

        private String notAnnotated;

        @CookieParameter("the_cookie")
        private Cookie cookieObject;

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

        public Cookie getCookieObject() {
            return cookieObject;
        }

        public void setCookieObject(Cookie cookieObject) {
            this.cookieObject = cookieObject;
        }
    }
}
