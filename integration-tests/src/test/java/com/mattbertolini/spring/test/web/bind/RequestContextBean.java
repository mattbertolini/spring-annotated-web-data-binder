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
package com.mattbertolini.spring.test.web.bind;

import com.mattbertolini.spring.web.bind.annotation.RequestContext;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpMethod;
import org.springframework.lang.Nullable;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;

import java.time.ZoneId;
import java.util.Locale;
import java.util.TimeZone;

@SuppressWarnings("unused")
public class RequestContextBean {
    @Nullable
    @RequestContext
    private NativeWebRequest webRequestField;

    @Nullable
    private NativeWebRequest webRequestSetter;

    @Nullable
    private NativeWebRequest webRequestGetter;

    @Nullable
    @RequestContext
    private ServerWebExchange exchangeField;

    @Nullable
    private ServerWebExchange exchangeSetter;

    @Nullable
    private ServerWebExchange exchangeGetter;

    @Nullable
    @RequestContext
    private Locale locale;

    @Nullable
    @RequestContext
    private TimeZone timeZone;

    @Nullable
    @RequestContext
    private ZoneId zoneId;

    @Nullable
    @RequestContext
    private HttpMethod method;

    @Nullable
    @RequestContext
    private HttpSession httpSession;

    @Nullable
    @RequestContext
    private WebSession webSession;

    @Nullable
    public NativeWebRequest getWebRequestField() {
        return webRequestField;
    }

    public void setWebRequestField(NativeWebRequest webRequestField) {
        this.webRequestField = webRequestField;
    }

    @Nullable
    public NativeWebRequest getWebRequestSetter() {
        return webRequestSetter;
    }

    @RequestContext
    public void setWebRequestSetter(NativeWebRequest webRequestSetter) {
        this.webRequestSetter = webRequestSetter;
    }

    @Nullable
    @RequestContext
    public NativeWebRequest getWebRequestGetter() {
        return webRequestGetter;
    }

    public void setWebRequestGetter(NativeWebRequest webRequestGetter) {
        this.webRequestGetter = webRequestGetter;
    }

    @Nullable
    public ServerWebExchange getExchangeField() {
        return exchangeField;
    }

    public void setExchangeField(ServerWebExchange exchangeField) {
        this.exchangeField = exchangeField;
    }

    @Nullable
    public ServerWebExchange getExchangeSetter() {
        return exchangeSetter;
    }

    @RequestContext
    public void setExchangeSetter(ServerWebExchange exchangeSetter) {
        this.exchangeSetter = exchangeSetter;
    }

    @Nullable
    @RequestContext
    public ServerWebExchange getExchangeGetter() {
        return exchangeGetter;
    }

    public void setExchangeGetter(ServerWebExchange exchangeGetter) {
        this.exchangeGetter = exchangeGetter;
    }

    @Nullable
    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    @Nullable
    public TimeZone getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
    }

    @Nullable
    public ZoneId getZoneId() {
        return zoneId;
    }

    public void setZoneId(ZoneId zoneId) {
        this.zoneId = zoneId;
    }

    @Nullable
    public HttpMethod getMethod() {
        return method;
    }

    public void setMethod(HttpMethod method) {
        this.method = method;
    }

    @Nullable
    public HttpSession getHttpSession() {
        return httpSession;
    }

    public void setHttpSession(HttpSession httpSession) {
        this.httpSession = httpSession;
    }

    @Nullable
    public WebSession getWebSession() {
        return webSession;
    }

    public void setWebSession(WebSession webSession) {
        this.webSession = webSession;
    }
}
