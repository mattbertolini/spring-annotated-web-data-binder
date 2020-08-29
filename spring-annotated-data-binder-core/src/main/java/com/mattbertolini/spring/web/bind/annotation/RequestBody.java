package com.mattbertolini.spring.web.bind.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Annotation for fetching the HTTP request body.</p>
 *
 * <p>This annotation leverages the same {@link org.springframework.http.converter.HttpMessageConverter}s in Spring MVC
 * and {@link org.springframework.http.codec.HttpMessageReader}s in Spring WebFlux to convert an HTTP request body into
 * a Java representation.</p>
 *
 * <p>This annotation can be used on fields:
 * <pre>
 *     &#64;RequestBody
 *     private JsonBody requestBody;
 * </pre>
 * or on getter/setter methods of the property:
 * <pre>
 *     &#64;RequestBody
 *     public void setRequestBody(JsonBody requestBody) {
 *         this.requestBody = requestBody;
 *     }
 * </pre>
 * </p>
 *
 * <p>This annotation can only be used once per request and cannot be combined with the Spring
 * {@link org.springframework.web.bind.annotation.RequestBody} annotation. This is because the request body InputStream
 * can only be read once per request.</p>
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestBody {}
