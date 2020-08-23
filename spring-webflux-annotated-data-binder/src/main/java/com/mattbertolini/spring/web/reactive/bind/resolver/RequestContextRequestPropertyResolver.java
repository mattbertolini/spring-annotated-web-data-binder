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

package com.mattbertolini.spring.web.reactive.bind.resolver;

import com.mattbertolini.spring.web.bind.annotation.RequestContext;
import com.mattbertolini.spring.web.bind.introspect.BindingProperty;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.TimeZoneAwareLocaleContext;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

import java.time.ZoneId;
import java.util.Locale;
import java.util.TimeZone;

public class RequestContextRequestPropertyResolver implements RequestPropertyResolver {
    @Override
    public boolean supports(@NonNull BindingProperty bindingProperty) {
        Class<?> type = bindingProperty.getType();
        return bindingProperty.hasAnnotation(RequestContext.class) && (
            ServerWebExchange.class.isAssignableFrom(type) ||
                ServerHttpRequest.class.isAssignableFrom(type) ||
                WebSession.class.isAssignableFrom(type) ||
                HttpMethod.class.isAssignableFrom(type)  ||
                Locale.class.isAssignableFrom(type) ||
                TimeZone.class.isAssignableFrom(type) ||
                ZoneId.class.isAssignableFrom(type)
        );
    }

    @NonNull
    @Override
    public Mono<Object> resolve(@NonNull TypeDescriptor typeDescriptor, @NonNull ServerWebExchange exchange) {
        Class<?> type = typeDescriptor.getType();

        if (ServerWebExchange.class.isAssignableFrom(type)) {
            return Mono.just(exchange);
        }

        ServerHttpRequest request = exchange.getRequest();
        if (ServerHttpRequest.class.isAssignableFrom(type)) {
            return Mono.justOrEmpty(request);
        } else if (HttpMethod.class.isAssignableFrom(type)) {
            return Mono.justOrEmpty(request.getMethod());
        } else if (WebSession.class.isAssignableFrom(type)) {
            return Mono.from(exchange.getSession());
        } else if (Locale.class.isAssignableFrom(type)) {
            return Mono.justOrEmpty(exchange.getLocaleContext().getLocale());
        } else if (TimeZone.class.isAssignableFrom(type)) {
            TimeZone timeZone = getTimeZone(exchange);
            return Mono.justOrEmpty(timeZone != null ? timeZone : TimeZone.getDefault());
        } else if (ZoneId.class.isAssignableFrom(type)) {
            TimeZone timeZone = getTimeZone(exchange);
            return Mono.justOrEmpty(timeZone != null ? timeZone.toZoneId() : ZoneId.systemDefault());
        }

        // This should not be thrown if the supports method is correct and in line with this method.
        throw new UnsupportedOperationException("Unable to resolve type " + type);
    }

    @Nullable
    private TimeZone getTimeZone(@NonNull ServerWebExchange exchange) {
        LocaleContext localeContext = exchange.getLocaleContext();
        if (localeContext instanceof TimeZoneAwareLocaleContext) {
            TimeZoneAwareLocaleContext timeZoneAwareLocaleContext = (TimeZoneAwareLocaleContext) localeContext;
            return timeZoneAwareLocaleContext.getTimeZone();
        }
        return null;
    }
}
