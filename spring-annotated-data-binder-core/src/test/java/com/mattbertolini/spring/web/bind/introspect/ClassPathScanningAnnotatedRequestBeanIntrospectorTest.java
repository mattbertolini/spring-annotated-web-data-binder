/*
 * Copyright 2019-2021 the original author or authors.
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

package com.mattbertolini.spring.web.bind.introspect;

import com.mattbertolini.spring.web.bind.introspect.scan.IgnoredBean;
import com.mattbertolini.spring.web.bind.introspect.scan.ScannedBean;
import com.mattbertolini.spring.web.bind.introspect.scan.subbackage.SubpackageBean;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class ClassPathScanningAnnotatedRequestBeanIntrospectorTest {

    private AnnotatedRequestBeanIntrospector delegateIntrospector;

    @BeforeEach
    void setUp() {
        delegateIntrospector = mock(AnnotatedRequestBeanIntrospector.class);
    }

    @Test
    void scansBeansAnnotatedWithRequestBean() {
        Set<String> packages = Collections.singleton("com.mattbertolini.spring.web.bind.introspect.scan");
        ClassPathScanningAnnotatedRequestBeanIntrospector introspector = new ClassPathScanningAnnotatedRequestBeanIntrospector(delegateIntrospector, packages);
        introspector.afterPropertiesSet();
        verify(delegateIntrospector).getResolversFor(ScannedBean.class);
        verify(delegateIntrospector).getResolversFor(SubpackageBean.class);
        verify(delegateIntrospector, never()).getResolversFor(IgnoredBean.class);
    }

    @Test
    void scansNonExistentPackage() {
        Set<String> packages = Collections.singleton("com.mattbertolini.spring.nonexistent");
        ClassPathScanningAnnotatedRequestBeanIntrospector introspector = new ClassPathScanningAnnotatedRequestBeanIntrospector(delegateIntrospector, packages);
        introspector.afterPropertiesSet();
        verifyNoInteractions(delegateIntrospector);
    }

    @Test
    void throwsExceptionWhenResolving() {
        when(delegateIntrospector.getResolversFor(any())).thenThrow(RuntimeException.class);
        Set<String> packages = Collections.singleton("com.mattbertolini.spring.web.bind.introspect.scan");
        ClassPathScanningAnnotatedRequestBeanIntrospector introspector = new ClassPathScanningAnnotatedRequestBeanIntrospector(delegateIntrospector, packages);
        assertThatThrownBy(introspector::afterPropertiesSet).isInstanceOf(RequestBeanIntrospectionException.class);
    }

    @Test
    void delegateIsCalledForGetResolversForMethod() {
        Set<String> packages = Collections.singleton("com.mattbertolini.spring.web.bind.introspect.scan");
        ClassPathScanningAnnotatedRequestBeanIntrospector introspector = new ClassPathScanningAnnotatedRequestBeanIntrospector(delegateIntrospector, packages);
        introspector.afterPropertiesSet();
        List<ResolvedPropertyData> resolversFor = introspector.getResolversFor(ScannedBean.class);
        assertThat(resolversFor).isNotNull();
        verify(delegateIntrospector).getResolversFor(ScannedBean.class);
    }
}
