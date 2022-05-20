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
 * <p>Annotation for fetching Spring path variable values.</p>
 *
 * <p>This annotation can be used on fields:
 * <pre>{@code
 *     @PathParameter("pathVar")
 *     private String pathVariable;
 * }</pre>
 * or on getter/setter methods:
 * <pre>{@code
 *     @PathParameter("pathVar")
 *     public void setPathVariable(String pathVariable) {
 *         this.pathVariable = pathVariable;
 *     }
 * }</pre>
 * </p>
 *
 * <p>Setting this annotation without a value on a {@link java.util.Map Map} binds all path variables to a Map.
 * <pre>{@code
 *     @PathParameter
 *     private Map<String, String> pathVariables;
 * }</pre>
 * </p>
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PathParameter {
    String value() default "";
}
