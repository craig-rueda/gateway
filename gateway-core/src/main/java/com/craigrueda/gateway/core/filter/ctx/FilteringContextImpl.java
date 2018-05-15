package com.craigrueda.gateway.core.filter.ctx;

import org.springframework.http.HttpHeaders;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.ipc.netty.http.client.HttpClientResponse;

import java.net.URI;

import static java.lang.Boolean.TRUE;

/**
 * Created by Craig Rueda
   */
public class FilteringContextImpl implements FilteringContext {
    private static long requestCnt = 0;
    private ServerWebExchange exchange;
    private long requestNum = ++requestCnt;

    public FilteringContextImpl(ServerWebExchange exchange) {
        this.exchange = exchange;
    }

    @Override
    public ServerWebExchange getExchange() {
        return exchange;
    }

    @Override
    public void setExchange(ServerWebExchange exchange) {
        this.exchange = exchange;
    }

    @Override
    public void setAttribute(String key) {
        exchange.getAttributes().putIfAbsent(key, true);
    }

    @Override
    public void setAttribute(String key, Object val) {
        exchange.getAttributes().put(key, val);
    }

    @Override
    public <T> T getAttribute(String key) {
        return (T) exchange.getAttributes().get(key);
    }

    @Override
    public void setOriginalUri(String uri) {
        setAttribute(CTX_REQ_ORIG_URI, uri);
    }

    @Override
    public String getOriginalUri() {
        return getAttribute(CTX_REQ_ORIG_URI);
    }

    @Override
    public boolean getShouldSendResponse() {
        return TRUE.equals(getAttribute(CTX_SHOULD_SEND_RESP));
    }

    @Override
    public void setShouldSendResponse(boolean shouldSend) {
        setAttribute(CTX_SHOULD_SEND_RESP, shouldSend);
    }

    @Override
    public ClientResponse getUpstreamResponse() {
        return getAttribute(CTX_UPSTREAM_RESP);
    }

    @Override
    public void setUpstreamResponse(ClientResponse response) {
        setAttribute(CTX_UPSTREAM_RESP, response);
    }

    @Override
    public HttpHeaders getUpstreamRequestHeaders() {
        return getAttribute(CTX_UPSTREAM_REQ_HEADERS);
    }

    @Override
    public void setUpstreamRequestHeaders(HttpHeaders headers) {
        setAttribute(CTX_UPSTREAM_REQ_HEADERS, headers);
    }

    @Override
    public URI getUpstreamRequestUrl() {
        return getAttribute(CTX_UPSTREAM_REQ_URI);
    }

    @Override
    public void setUpstreamRequestUrl(URI uri) {
        setAttribute(CTX_UPSTREAM_REQ_URI, uri);
    }

    @Override
    public void setAlreadyRouted(boolean alreadyRouted) {
        setAttribute(CTX_REQ_ROUTED, alreadyRouted);
    }

    @Override
    public boolean isAlreadyRouted() {
        return TRUE.equals(getAttribute(CTX_REQ_ROUTED));
    }

    @Override
    public long getRequestNum() {
        return requestNum;
    }

    @Override
    public void setError(Throwable throwable) {
        setAttribute(CTX_REQ_ERROR, throwable);
    }

    @Override
    public Throwable getError() {
        return getAttribute(CTX_REQ_ERROR);
    }
}
