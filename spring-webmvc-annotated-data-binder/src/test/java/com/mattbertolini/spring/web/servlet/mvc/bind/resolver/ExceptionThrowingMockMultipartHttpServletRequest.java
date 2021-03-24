package com.mattbertolini.spring.web.servlet.mvc.bind.resolver;

import org.springframework.mock.web.MockMultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletException;
import javax.servlet.http.Part;
import java.io.IOException;
import java.util.Collection;

public class ExceptionThrowingMockMultipartHttpServletRequest extends MockMultipartHttpServletRequest {
    @Override
    public MultipartFile getFile(String name) {
        throw new RuntimeException("Failure in getFile");
    }

    @Override
    public Collection<Part> getParts() throws IOException, ServletException {
        throw new ServletException("Failure in getParts");
    }
}
