/*
 * Copyright 2025 the original author or authors.
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
package com.mattbertolini.spring.web.servlet.mvc.bind.resolver;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Part;
import org.springframework.mock.web.MockMultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

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
