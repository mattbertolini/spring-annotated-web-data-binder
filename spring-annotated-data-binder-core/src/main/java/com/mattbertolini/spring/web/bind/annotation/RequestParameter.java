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
 * <p>Annotation for fetching HTTP request parameters.</p>
 *
 * <p>In Spring WebFlux this annotation is used for fetching query parameter data from the request. In Spring MVC this
 * annotation fetches both query parameter and form data. This is due to a limitation in the Java Servlet framework
 * where both query and form data are merged into one. This means that this annotation and the {@link FormParameter}
 * annotation are essentially the same in Spring MVC.</p>
 *
 * <p>This annotation can be used on fields:
 * <pre>{@code
 *     @RequestParameter("query_param")
 *     private String queryParam;
 * }</pre>
 * or on getter/setter methods of the property:
 * <pre>{@code
 *     @RequestParameter("query_param")
 *     public void setQueryParam(String queryParam) {
 *          this.queryParam = queryParam;
 *     }
 * }</pre>
 * </p>
 *
 * <p>Setting this annotation without a value on a {@link java.util.Map Map} or
 * {@link org.springframework.util.MultiValueMap MultiValueMap} binds all of the query parameters to a map.
 * <pre>{@code
 *     @RequestParameter
 *     private MultiValueMap<String, String> queryParameters;
 *
 *     // Map of the first values only
 *     @RequestParameter
 *     private Map<String, String> firstParamValues;
 * }</pre>
 * </p>
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestParameter {
    String value() default "";
}
