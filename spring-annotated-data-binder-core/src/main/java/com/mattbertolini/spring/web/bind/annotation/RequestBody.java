/*
 * Copyright 2024 the original author or authors.
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
package com.mattbertolini.spring.web.bind.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Annotation for fetching the HTTP request body.</p>
 *
 * <p>This annotation leverages the same {@link org.springframework.http.converter.HttpMessageConverter
 * HttpMessageConverters} in Spring MVC and {@link org.springframework.http.codec.HttpMessageReader HttpMessageReaders}
 * in Spring WebFlux to convert an HTTP request body into a Java representation.</p>
 *
 * <p>This annotation can be used on fields:
 * <pre>{@code
 *     @RequestBody
 *     private JsonBody requestBody;
 * }</pre>
 * or on getter/setter methods of the property:
 * <pre>{@code
 *     @RequestBody
 *     public void setRequestBody(JsonBody requestBody) {
 *         this.requestBody = requestBody;
 *     }
 * }</pre>
 * </p>
 *
 * <p>This annotation can only be used once per request and cannot be combined with the Spring
 * {@link org.springframework.web.bind.annotation.RequestBody RequestBody} annotation. This is because the request body
 * InputStream can only be read once per request.</p>
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestBody {}
