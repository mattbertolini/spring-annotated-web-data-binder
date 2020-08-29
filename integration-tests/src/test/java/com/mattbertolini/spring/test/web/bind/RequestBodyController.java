package com.mattbertolini.spring.test.web.bind;

import com.mattbertolini.spring.web.bind.annotation.BeanParameter;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

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

    @PostMapping(value = "/bindingResult", produces = MediaType.TEXT_PLAIN_VALUE)
    public String bindingResult(@BeanParameter RequestBodyBean.BindingResult requestBodyBean, BindingResult bindingResult) {
        return Integer.toString(bindingResult.getErrorCount());
    }

    @PostMapping(value = "/validated", produces = MediaType.TEXT_PLAIN_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public String validated(@Valid @BeanParameter RequestBodyBean.Validation requestBodyBean) {
        return requestBodyBean.getJsonBody().getProperty();
    }

    @PostMapping(value = "/validatedWithBindingResult", produces = MediaType.TEXT_PLAIN_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public String validatedWithBindingResult(@Valid @BeanParameter RequestBodyBean.Validation requestBodyBean, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "notValid";
        }
        return "valid";
    }

    @PostMapping(value = "/nested", produces = MediaType.TEXT_PLAIN_VALUE)
    public String nestedBeanParameter(@BeanParameter RequestBodyBean.Nested requestBodyBean) {
        return requestBodyBean.getNestedBean().getRequestBody().getProperty();
    }
}
