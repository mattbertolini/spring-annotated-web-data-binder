# Spring Annotated Web Data Binder

[![Build Status](https://travis-ci.com/mattbertolini/spring-annotated-web-data-binder.svg?branch=main)](https://travis-ci.com/mattbertolini/spring-annotated-web-data-binder)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=mattbertolini_spring-annotated-web-data-binder&metric=coverage)](https://sonarcloud.io/dashboard?id=mattbertolini_spring-annotated-web-data-binder)
[![Spring MVC Annotated Data Binder](https://img.shields.io/maven-central/v/com.mattbertolini/spring-webmvc-annotated-data-binder.svg?label=Spring%20MVC%20Annotated%20Data%20Binder)](https://search.maven.org/search?q=g:%22com.mattbertolini%22%20AND%20a:%22spring-webmvc-annotated-data-binder%22)
[![Spring WebFlux Annotated Data Binder](https://img.shields.io/maven-central/v/com.mattbertolini/spring-webflux-annotated-data-binder.svg?label=Spring%20WebFlux%20Annotated%20Data%20Binder)](https://search.maven.org/search?q=g:%22com.mattbertolini%22%20AND%20a:%22spring-webflux-annotated-data-binder%22)

JAX-RS style data binding for Spring MVC and Spring WebFlux. Bind query params, form data, headers, cookies, and 
session data to a Java bean. It has built-in support for nested beans, type conversion, and validation. It's basically 
a more advanced `@ModelAttribute`.  

```java
@RequestBean // Annotate class for introspection at application startup
public class CustomRequestBean {
    // Query parameters
    @RequestParameter("different_name")
    private String queryParam;

    // Form data
    @FormParameter("form_data")
    private String formData;
    
    // HTTP headers
    @HeaderParameter("X-Custom-Header")
    private String headerValues;
    
    // Spring MVC/WebFlux path variables
    @PathParameter("pathParam")
    private Integer pathParam;

    // HTTP cookie values
    @CookieParameter("cookie_value")
    private String cookieValue;

    // HTTP session attributes
    @SessionParameter("sessionAttribute")
    private String sessionAttribute;

    // Spring derived request scoped data like locale and time zone
    @RequestContext
    private Locale locale;
    
    @RequestContext    
    private ZoneId timeZone;

    // Request body parsed by Spring HTTP MessageReaders
    @RequestBody
    private JsonBody requestBody;

    // A nested Java bean with additional annotated properties
    @BeanParameter
    private NestedBean nestedBean;
    
    // Getters and Setters
}

@RestController
public class RequestController {

    @Autowired
    private SomeService someService; 

    @GetMapping(value = "/example", produces = MediaType.TEXT_PLAIN_VALUE)
    public String handleRequest(@BeanParameter CustomRequestBean customRequestBean) {
        return someService.doSomethingWithBean(customRequestBean);
    }
}
```

Full documentation is coming soon.

## Status

This library is tested and stable for use in a production environment. Until the version reaches 1.0.0 the API can 
change because I would like to see how the API works in the real world before it is considered fully stable. At 
version 1.0.0 I will do my best to keep breaking changes to major version changes only. If you encounter any bugs, 
please file them in the issue tracker above.

## Background

I've had the opportunity to build and maintain multiple RESTful APIs at various companies over the last 10 years. When 
selecting a web framework, it usually came down to either Spring MVC, or a JAX-RS implementation like Jersey. I've 
always enjoyed using Spring and Spring MVC and tended to prefer it over JAX-RS but one thing has always bothered me 
about it: the weak Java bean binding.

The Spring MVC `@ModelAttribute` provides some basic Java bean binding, but it relies on the query parameters directly 
matching the property names. For example, if the property name is `firstName` with camel case then the query parameter 
must be camel case as well. This is all fine when building an API from scratch but real life is far from that clean and 
tidy. Most times I was dealing with pre-existing query parameter names with a variety of naming strategies like snake 
case and abbreviated parameter names.

There are all sorts of workarounds and hacks to make these use cases work. Most of these workarounds involved modifying 
lower level Spring components. Nothing felt like it was designed to be exposed to the developer. It was often times 
easier to leverage JAX-RS and its *Param annotations (`@QueryParam`, `@FormParam`, etc) as they provided much-needed 
flexibility. It was frustrating as this feature was the only thing I really liked about JAX-RS. I much rather use 
Spring and Spring MVC.

This library is my attempt to fill the gap I see in Spring MVC.

## Requirements

* Java 8+
* Spring MVC or Spring WebFlux (5.3.8+)   

## Use

Add the appropriate artifact to your build.

### Spring MVC

Maven:
```xml
<dependency>
    <groupId>com.mattbertolini</groupId>
    <artifactId>spring-webmvc-annotated-data-binder</artifactId>
    <version>0.4.0</version>
</dependency>
```

Gradle:
```groovy
implementation 'com.mattbertolini:spring-webmvc-annotated-data-binder:0.4.0'
```

Ivy:
```xml
<dependency org="com.mattbertolini" name="spring-webmvc-annotated-data-binder" rev="0.4.0"/>
```

### Spring WebFlux

Maven:
```xml
<dependency>
    <groupId>com.mattbertolini</groupId>
    <artifactId>spring-webflux-annotated-data-binder</artifactId>
    <version>0.4.0</version>
</dependency>
```

Gradle:
```groovy
implementation 'com.mattbertolini:spring-webflux-annotated-data-binder:0.4.0'
```

Ivy:
```xml
<dependency org="com.mattbertolini" name="spring-webflux-annotated-data-binder" rev="0.4.0"/>
```

### Configuration 

Once the jar is on your classpath, add a `BinderConfiguration` bean to your Spring context:

```java
@Bean
public BinderConfiguration binderConfiguration() {
    return new BinderConfiguration();
}
```

## Build

### Build Requirements

* Java 17 or above to build. The release jars are compiled to Java 8 bytecode. Integration tests are compiled to Java 17 bytecode.

To build:

```shell
./gradlew build
```

Windows:
```shell
gradlew.bat build
```

## Design Goals

I had a few design goals when starting this project:

* Support both Spring MVC and Spring WebFlux.
* Have no dependencies other than Spring.
* Behave as close to the `@ModelAttribute` as possible.

I tried to stick to the philosophy "What do 90% of people need 90% of the time".

## License

This project is licensed under the Apache License 2.0. 
