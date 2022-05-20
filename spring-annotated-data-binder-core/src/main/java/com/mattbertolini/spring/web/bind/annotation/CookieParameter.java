/*
 * Copyright 2019-2022 the original author or authors.
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

package com.mattbertolini.spring.web.bind.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Annotation for fetching HTTP cookie data.</p>
 *
 * <p>This annotation can be used on fields:
 * <pre>{@code
 *     @CookieParameter("cookie_name")
 *     private String cookieValue;
 * }</pre>
 * or on getter/setter methods:
 * <pre>{@code
 *     @CookieParameter("cookie_name")
 *     public void setCookieValue(String cookieValue) {
 *         this.cookieValue = cookieValue;
 *     }
 * }</pre>
 * </p>
 *
 * <p>If you need access to all attributes of a cookie (e.g. expiration date, domain, etc) you can bind to cookie
 * objects. In Spring MVC you and bind directly to a {@link javax.servlet.http.Cookie}:
 * <pre>{@code
 *     @CookieParameter("cookie_name")
 *     private Cookie cookie;
 * }</pre>
 * In Spring WebFlux you can bind directly to a {@link org.springframework.http.HttpCookie}:
 * <pre>{@code
 *     @CookieParameter("cookie_name")
 *     private HttpCookie cookie;
 * }</pre>
 * </p>
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CookieParameter {
    String value();
}
