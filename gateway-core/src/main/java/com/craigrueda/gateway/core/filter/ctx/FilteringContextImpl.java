package com.craigrueda.gateway.core.filter.ctx;

import org.springframework.http.HttpHeaders;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.server.ServerWebExchange;

import java.net.URI;

/**
 * Created by Craig Rueda
   */
public class FilteringContextImpl implements FilteringContext {
    private ServerWebExchange exchange;

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

    }

    @Override
    public String getOriginalUri() {
        return null;
    }

    @Override
    public boolean shouldSendResponse() {
        return true;
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
    public void setAlreadyRouted() {

    }

    @Override
    public boolean isAlreadyRouted() {
        return false;
    }
}
