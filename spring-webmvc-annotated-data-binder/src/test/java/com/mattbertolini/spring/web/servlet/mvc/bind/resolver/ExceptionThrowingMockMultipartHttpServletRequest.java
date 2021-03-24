package com.mattbertolini.spring.web.servlet.mvc.bind.resolver;

import org.springframework.mock.web.MockMultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

public class ExceptionThrowingMockMultipartHttpServletRequest extends MockMultipartHttpServletRequest {
    @Override
    public MultipartFile getFile(String name) {
        throw new RuntimeException("Failure");
    }
}
