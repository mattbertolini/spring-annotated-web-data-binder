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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.ResolvableType;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;

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
    void supportsReturnsTrueOnPresenceOfAnnotation() {
        boolean result = resolver.supports(typeDescriptor(String.class, new StubbingAnnotation("name")));
        assertThat(result).isTrue();
    }

    @Test
    void supportsReturnsFalseOnMissingAnnotation() {
        boolean result = resolver.supports(typeDescriptor(String.class));
        assertThat(result).isFalse();
    }

    @Test
    void throwsExceptionIfResolveCalledWithNoAnnotation() {
        // Unlikely to happen as the library always checks the supports method.
        assertThatExceptionOfType(IllegalStateException.class)
            .isThrownBy(() -> resolver.resolve(typeDescriptor(String.class), request));
    }

    @Test
    void throwsExceptionWhenNativeRequestDoesNotWrapServletRequest() {
        NativeWebRequest webRequest = mock(NativeWebRequest.class);
        when(webRequest.getNativeRequest()).thenReturn(null);
        assertThatThrownBy(() -> resolver.resolve(typeDescriptor(HttpServletRequest.class), webRequest))
            .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void returnsValueFromCookie() {
        String expected = "expectedValue";
        String cookieName = "cookie_name";
        Cookie cookie = new Cookie(cookieName, expected);
        servletRequest.setCookies(cookie);
        Object actual = resolver.resolve(typeDescriptor(String.class, new StubbingAnnotation(cookieName)), request);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void returnsCookieObjectWhenTypeMatches() {
        String cookieName = "the_cookie";
        Cookie expected = new Cookie(cookieName, "aValue");
        servletRequest.setCookies(expected);
        Object actual = resolver.resolve(typeDescriptor(Cookie.class, new StubbingAnnotation(cookieName)), request);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void returnsNullWhenNoCookieFound() {
        Object notFound = resolver.resolve(typeDescriptor(Integer.class, new StubbingAnnotation("not_found")), request);
        assertThat(notFound).isNull();
    }

    private TypeDescriptor typeDescriptor(Class<?> clazz, Annotation... annotations) {
        return new TypeDescriptor(ResolvableType.forClass(clazz), null, annotations);
    }


    @SuppressWarnings("ClassExplicitlyAnnotation")
    private static class StubbingAnnotation implements CookieParameter {
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
            return CookieParameter.class;
        }
    }
}
