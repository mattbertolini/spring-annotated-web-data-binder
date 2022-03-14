/*
 * Copyright 2019-2022 the original author or authors.
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

import com.mattbertolini.spring.web.bind.AbstractPropertyResolverRegistry;
import com.mattbertolini.spring.web.bind.annotation.BeanParameter;
import com.mattbertolini.spring.web.bind.annotation.RequestParameter;
import com.mattbertolini.spring.web.bind.resolver.RequestPropertyResolverBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.lang.NonNull;

import java.lang.annotation.Annotation;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DefaultAnnotatedRequestBeanIntrospectorTest {
    private DefaultAnnotatedRequestBeanIntrospector introspector;
    private FakeRegistry registry;

    @BeforeEach
    void setUp() {
        registry = new FakeRegistry();
        introspector = new DefaultAnnotatedRequestBeanIntrospector(registry);
    }

    @Test
    void throwsExceptionOnCircularReference() {
        assertThatThrownBy(() -> introspector.getResolversFor(CircularReference.class))
            .isInstanceOf(CircularReferenceException.class);
    }

    @Test
    void returnsResolversForType() {
        FakeResolver resolver = new FakeResolver(RequestParameter.class);
        registry.addResolver(resolver);
        Collection<ResolvedPropertyData> resolvers = introspector.getResolversFor(SimpleType.class);
        assertThat(resolvers.size()).isEqualTo(1);
        ResolvedPropertyData data = resolvers.iterator().next();
        assertThat(data.getPropertyName()).isEqualTo("data");
    }

    @Test
    void returnsResolversUsingNestedTypes() {
        FakeResolver resolver = new FakeResolver(RequestParameter.class);
        registry.addResolver(resolver);
        Collection<ResolvedPropertyData> resolvers = introspector.getResolversFor(OuterBean.class);
        assertThat(resolvers.size()).isEqualTo(1);
        ResolvedPropertyData data = resolvers.iterator().next();
        assertThat(data.getPropertyName()).isEqualTo("innerBean.inner");
    }

    private static class FakeResolver implements RequestPropertyResolverBase<Void, Object> {
        private final Class<? extends Annotation> annotationType;

        public FakeResolver(Class<? extends Annotation> annotationType) {
            this.annotationType = annotationType;
        }

        @Override
        public boolean supports(@NonNull BindingProperty bindingProperty) {
            return bindingProperty.hasAnnotation(annotationType);
        }

        @Override
        public Object resolve(@NonNull BindingProperty bindingProperty, @NonNull Void request) {
            // Don't need to worry about this method. No used in the introspector.
            return null;
        }
    }

    private static class FakeRegistry extends AbstractPropertyResolverRegistry<FakeResolver> {
        public FakeRegistry() {
            super();
        }
    }

    @SuppressWarnings("unused")
    private static class SimpleType {
        @RequestParameter("data_param")
        private String data;

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }
    }

    @SuppressWarnings("unused")
    private static class OuterBean {
        @BeanParameter
        private InnerBean innerBean;

        public InnerBean getInnerBean() {
            return innerBean;
        }

        public void setInnerBean(InnerBean innerBean) {
            this.innerBean = innerBean;
        }
    }

    @SuppressWarnings("unused")
    private static class InnerBean {
        @RequestParameter("inner_param")
        private String inner;

        public String getInner() {
            return inner;
        }

        public void setInner(String inner) {
            this.inner = inner;
        }
    }

    @SuppressWarnings("unused")
    private static class CircularReference {
        @BeanParameter
        private CircularReference circularReference;

        public CircularReference getCircularReference() {
            return circularReference;
        }

        public void setCircularReference(CircularReference circularReference) {
            this.circularReference = circularReference;
        }
    }
}
