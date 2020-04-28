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
 * <p>Marker annotation that enables introspection at startup when combined with the
 * {@link com.mattbertolini.spring.web.bind.introspect.ClassPathScanningAnnotatedRequestBeanIntrospector}. Any Java
 * bean with this annotation found in a package scanned by the introspector will have it's resolved property data
 * pre-loaded and cached so it can be retrieved at request time without the need for additional introspection. This is
 * a performance improvement so introspection is not done at request time.
 * <pre>
 *     &#64;RequestBean
 *     public class ExampleRequestBean {
 *         &#64RequestParameter("some_parameter")
 *         private String someParameter;
 *         
 *         // Getters/Setters
 *     }
 * </pre>
 * </p>
 * @see com.mattbertolini.spring.web.bind.introspect.ClassPathScanningAnnotatedRequestBeanIntrospector
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestBean {}
