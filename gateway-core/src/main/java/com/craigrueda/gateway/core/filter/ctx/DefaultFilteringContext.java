package com.craigrueda.gateway.core.filter.ctx;

import com.craigrueda.gateway.core.routing.Route;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;

import java.net.URI;

import static java.lang.Boolean.TRUE;
import static java.lang.System.nanoTime;

/**
 * Created by Craig Rueda
   */
public class DefaultFilteringContext implements FilteringContext {
    private static long requestCnt = 0;
    private ServerWebExchange exchange;
    private long requestNum = ++requestCnt;
    private long startTimeNs = nanoTime();

    public DefaultFilteringContext(ServerWebExchange exchange) {
        this.exchange = exchange;
        setOriginalUri(exchange.getRequest() != null ? exchange.getRequest().getURI() : null);
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
    public void setOriginalUri(URI uri) {
        setAttribute(CTX_REQ_ORIG_URI, uri);
    }

    @Override
    public URI getOriginalUri() {
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
    public HttpHeaders getClientResponseHeaders() {
        return getAttribute(CTX_UPSTREAM_RESP_HEADERS);
    }

    @Override
    public void setClientResponseHeaders(HttpHeaders headers) {
        setAttribute(CTX_UPSTREAM_RESP_HEADERS, headers);
    }

    @Override
    public Route getUpstreamRequestRoute() {
        return getAttribute(CTX_UPSTREAM_REQ_ROUTE);
    }

    @Override
    public void setUpstreamRequestRoute(Route route) {
        setAttribute(CTX_UPSTREAM_REQ_ROUTE, route);
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
    public long getStartTimeNs() {
        return startTimeNs;
    }

    @Override
    public void setError(Throwable throwable) {
        setAttribute(CTX_REQ_ERROR, throwable);
    }

    @Override
    public Throwable getError() {
        return getAttribute(CTX_REQ_ERROR);
    }

    @Override
    public void setUpstreamQueryParams(MultiValueMap<String, String> queryParams) {
        setAttribute(CTX_UPSTREAM_REQ_QUERY, queryParams);
    }

    @Override
    public MultiValueMap<String, String> getUpstreamQueryParams() {
        return getAttribute(CTX_UPSTREAM_REQ_QUERY);
    }

    @Override
    public HttpStatus getResponseStatus() {
        return getAttribute(CTX_HTTP_STATUS);
    }

    @Override
    public void setResponseStatus(HttpStatus status) {
        setAttribute(CTX_HTTP_STATUS, status);
    }

    @Override
    public void setUpstreamResponseBody(Flux<DataBuffer> body) {
        setAttribute(CTX_UPSTREAM_RESP_BODY, body);
    }

    @Override
    public Flux<DataBuffer> getUpstreamResponseBody() {
        return getAttribute(CTX_UPSTREAM_RESP_BODY);
    }
}
