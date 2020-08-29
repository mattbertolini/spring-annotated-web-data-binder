package com.mattbertolini.spring.test.web.bind;

import com.mattbertolini.spring.web.bind.annotation.BeanParameter;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RequestBodyController {
    @PostMapping(value = "/annotatedField", produces = MediaType.TEXT_PLAIN_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public String annotatedField(@BeanParameter RequestBodyBean.AnnotatedField requestBodyBean) {
        JsonBody jsonBody = requestBodyBean.getJsonBody();
        return jsonBody.getProperty();
    }

    @PostMapping(value = "/annotatedSetter", produces = MediaType.TEXT_PLAIN_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public String annotatedSetter(@BeanParameter RequestBodyBean.AnnotatedSetter requestBodyBean) {
        JsonBody jsonBody = requestBodyBean.getJsonBody();
        return jsonBody.getProperty();
    }

    @PostMapping(value = "/annotatedGetter", produces = MediaType.TEXT_PLAIN_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public String annotatedGetter(@BeanParameter RequestBodyBean.AnnotatedGetter requestBodyBean) {
        JsonBody jsonBody = requestBodyBean.getJsonBody();
        return jsonBody.getProperty();
    }
}
