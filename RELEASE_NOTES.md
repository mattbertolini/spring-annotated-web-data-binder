# Release Notes

## 0.4.0
Released 2021-06-20

- New Spring Boot starter modules for both Web MVC and WebFlux implementations. Allows quick and easy use of the data 
  binder in Spring Boot applications with no configuration.
- Add basic support for Multipart request data in WebFlux.
- Minimum supported Spring version: 5.3.8
- Minimum supported Spring Boot version: 2.4.7

## 0.3.0
Released 2021-03-24

- Add support for binding `MultipartFile` and Servlet API `Part` objects using `@FormParamter` and `@RequestParamter` 
  annotations. `MultipartFile` and `Part` are only for Spring MVC and do not work in Spring WebFlux. 

## 0.2.0
Released 2020-09-02

- Add support for binding request payloads using the `@RequestBody` annotation.

## 0.1.0
Released 2020-04-29

- Initial release
- Minimum supported Spring version: 5.2.6