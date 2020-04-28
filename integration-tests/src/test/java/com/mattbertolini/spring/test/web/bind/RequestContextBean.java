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

package com.mattbertolini.spring.test.web.bind;

import com.mattbertolini.spring.web.bind.annotation.RequestContext;
import org.springframework.http.HttpMethod;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;

import javax.servlet.http.HttpSession;
import java.time.ZoneId;
import java.util.Locale;
import java.util.TimeZone;

@SuppressWarnings("unused")
public class RequestContextBean {
    @RequestContext
    private NativeWebRequest webRequestField;

    private NativeWebRequest webRequestSetter;

    private NativeWebRequest webRequestGetter;

    @RequestContext
    private ServerWebExchange exchangeField;

    private ServerWebExchange exchangeSetter;

    private ServerWebExchange exchangeGetter;

    @RequestContext
    private Locale locale;

    @RequestContext
    private TimeZone timeZone;

    @RequestContext
    private ZoneId zoneId;

    @RequestContext
    private HttpMethod method;

    @RequestContext
    private HttpSession httpSession;

    @RequestContext
    private WebSession webSession;

    public NativeWebRequest getWebRequestField() {
        return webRequestField;
    }

    public void setWebRequestField(NativeWebRequest webRequestField) {
        this.webRequestField = webRequestField;
    }

    public NativeWebRequest getWebRequestSetter() {
        return webRequestSetter;
    }

    @RequestContext
    public void setWebRequestSetter(NativeWebRequest webRequestSetter) {
        this.webRequestSetter = webRequestSetter;
    }

    @RequestContext
    public NativeWebRequest getWebRequestGetter() {
        return webRequestGetter;
    }

    public void setWebRequestGetter(NativeWebRequest webRequestGetter) {
        this.webRequestGetter = webRequestGetter;
    }

    public ServerWebExchange getExchangeField() {
        return exchangeField;
    }

    public void setExchangeField(ServerWebExchange exchangeField) {
        this.exchangeField = exchangeField;
    }

    public ServerWebExchange getExchangeSetter() {
        return exchangeSetter;
    }

    @RequestContext
    public void setExchangeSetter(ServerWebExchange exchangeSetter) {
        this.exchangeSetter = exchangeSetter;
    }

    @RequestContext
    public ServerWebExchange getExchangeGetter() {
        return exchangeGetter;
    }

    public void setExchangeGetter(ServerWebExchange exchangeGetter) {
        this.exchangeGetter = exchangeGetter;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public TimeZone getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
    }

    public ZoneId getZoneId() {
        return zoneId;
    }

    public void setZoneId(ZoneId zoneId) {
        this.zoneId = zoneId;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public void setMethod(HttpMethod method) {
        this.method = method;
    }

    public HttpSession getHttpSession() {
        return httpSession;
    }

    public void setHttpSession(HttpSession httpSession) {
        this.httpSession = httpSession;
    }

    public WebSession getWebSession() {
        return webSession;
    }

    public void setWebSession(WebSession webSession) {
        this.webSession = webSession;
    }
}
