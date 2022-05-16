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
 * <p>Annotation for fetching HTTP header values.</p>
 *
 * <p>This annotation can be used on fields:
 * <pre>{@code
 *     @HeaderParameter("X-Header-Name")
 *     private String headerValue;
 * }</pre>
 * or on getter/setter methods of the property:
 * <pre>{@code
 *     @HeaderParameter("X-Header-Name")
 *     public void setHeaderValue(String headerValue) {
 *         this.headerValue = headerValue;
 *     }
 * }</pre>
 * </p>
 *
 * <p>Setting this annotation without a value on a {@link java.util.Map} or
 * {@link org.springframework.util.MultiValueMap} binds all of the headers to a map.
 * <pre>{@code
 *     @HeaderParameter
 *     private MultiValueMap<String, String> headers;
 *
 *     // Map of first values only
 *     @HeaderParameter
 *     private Map<String, String> firstValueHeaders;
 * }</pre>
 * </p>
 *
 * <p>This can also be done with a {@link org.springframework.http.HttpHeaders} object as well.
 * <pre>{@code
 *     @HeaderParameter
 *     private HttpHeaders httpHeaders;
 * }</pre>
 * </p>
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface HeaderParameter {
    String value() default "";
}
