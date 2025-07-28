/*
 * Copyright 2025 the original author or authors.
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
 * <p>Annotation for fetching POSTed form data of type {@code application/x-www-form-urlencoded}.</p>
 *
 * <p>In Spring WebFlux this annotation is used for fetching form data from the request. In Spring MVC this annotation
 * fetches both query parameter and form data. This is due to a limitation in the Java Servlet framework where both
 * query and form data are merged into one. This means that this annotation and the {@link RequestParameter} annotation
 * are essentially the same in Spring MVC.</p>
 *
 * <p>This annotation can be used on fields:
 * <pre>{@code
 *     @FormParameter("form_param")
 *     private String formParam;
 * }</pre>
 * or on getter/setter methods of the property:
 * <pre>{@code
 *     @FormParameter("form_param")
 *     public void setFormParam(String formParam) {
 *          this.formParam = formParam;
 *     }
 * }</pre>
 * </p>
 *
 * <p>Setting this annotation without a value on a {@link java.util.Map Map} or
 * {@link org.springframework.util.MultiValueMap MultiValueMap} binds all of the form data to a map.
 * <pre>{@code
 *     @FormParameter
 *     private MultiValueMap<String, String> formParameters;
 *
 *     // Map of the first values only
 *     @FormParameter
 *     private Map<String, String> firstParamValues;
 * }</pre>
 * </p>
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FormParameter {
    String value() default "";
}
