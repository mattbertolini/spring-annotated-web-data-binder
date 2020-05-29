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

package com.mattbertolini.spring.web.bind.docs;

import com.mattbertolini.spring.web.bind.annotation.RequestParameter;

public class NestedBean {
    @RequestParameter("nested_request_param")
    private String nestedRequestParameter;

    public String getNestedRequestParameter() {
        return nestedRequestParameter;
    }

    public void setNestedRequestParameter(String nestedRequestParameter) {
        this.nestedRequestParameter = nestedRequestParameter;
    }
}
