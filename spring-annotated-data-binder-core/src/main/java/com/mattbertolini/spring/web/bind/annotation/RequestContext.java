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

package com.mattbertolini.spring.web.bind.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Annotation for binding special objects found in the Spring request context. Binds the following:
 * <ul>
 *     <li>{@link java.util.Locale} - The Spring-resolved locale based on the {@code Accept-Language} HTTP header.</li>
 *     <li>{@link java.util.TimeZone} - The Spring-resolved time zone.</li>
 *     <li>{@link java.time.ZoneId} - The Spring-resolved time zone as a Java 8+ Zone ID.</li>
 *     <li>{@link org.springframework.http.HttpMethod} - The HTTP request method.</li>
 * </ul>
 * In Spring MVC the following additional objects are available:
 * <ul>
 *     <li>{@link org.springframework.web.context.request.WebRequest} - The Spring WebRequest.</li>
 *     <li>{@link javax.servlet.http.HttpServletRequest} - The underlying Servlet request.</li>
 *     <li>{@link javax.servlet.http.HttpSession} - The Servlet session object.</li>
 * </ul>
 * In Spring WebFlux the following additional objects are available:
 * <ul>
 *     <li>{@link org.springframework.web.server.ServerWebExchange} - The Spring reactive web exchange.</li>
 *     <li>{@link org.springframework.http.server.reactive.ServerHttpRequest} - The Spring reactive HTTP request.</li>
 *     <li>{@link org.springframework.web.server.WebSession} - The Spring session object.</li>
 * </ul>
 * </p>
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestContext {}
