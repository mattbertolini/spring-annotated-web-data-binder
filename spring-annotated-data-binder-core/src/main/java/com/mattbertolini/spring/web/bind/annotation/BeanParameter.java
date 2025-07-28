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
 * <p>This annotation has two purposes. The first is to mark controller method arguments for binding using this library.
 * <pre>{@code
 *     @GetMapping(value = "/example", produces = MediaType.TEXT_PLAIN_VALUE)
 *     public String handleRequest(@BeanParameter requestBean) {
 *         return someService.doSomethingWith(requestBean);
 *     }
 * }</pre>
 * The second is to mark a Java bean property as a nested object that should also be scanned for additional request
 * annotations.
 * <pre>{@code
 *     public class OuterBean {
 *         @RequestParameter("some_parameter")
 *         private String someParameter;
 *
 *         @BeanParameter
 *         private NestedBean nestedBean;
 *
 *         // Getters/Setters
 *     }
 *
 *     public class NestedBean {
 *         @RequestParameter("another_parameter")
 *         private String anotherParameter;
 *
 *         // Getters/Setters
 *     }
 * }</pre>
 * <strong>Note:</strong> It's important that no circular dependencies are created using this annotation. The
 * introspection process will fail if a circular reference is found. This is done to prevent stack overflow errors at
 * runtime.</p>
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BeanParameter {}
