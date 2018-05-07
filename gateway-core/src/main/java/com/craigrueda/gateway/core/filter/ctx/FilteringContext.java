package com.craigrueda.gateway.core.filter.ctx;

import org.springframework.http.HttpHeaders;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.server.ServerWebExchange;

import java.net.URI;

/**
 * Created by Craig Rueda
   */
public interface FilteringContext {
    static final String CTX_UPSTREAM_RESP = "CTX_UPSTREAM_RESP";
    static final String CTX_UPSTREAM_REQ_URI = "CTX_UPSTREAM_REQ_URI";
    static final String CTX_UPSTREAM_REQ_HEADERS = "CTX_UPSTREAM_REQ_HEADERS";


    ServerWebExchange getExchange();

    void setExchange(ServerWebExchange exchange);

    void setAttribute(String key);

    void setAttribute(String key, Object val);

    <T> T getAttribute(String key);

    void setOriginalUri(String uri);

    String getOriginalUri();

    boolean shouldSendResponse();

    ClientResponse getUpstreamResponse();
    void setUpstreamResponse(ClientResponse response);

    HttpHeaders getUpstreamRequestHeaders();
    void setUpstreamRequestHeaders(HttpHeaders headers);

    URI getUpstreamRequestUrl();
    void setUpstreamRequestUrl(URI uri);

    void setAlreadyRouted();

    boolean isAlreadyRouted();
}
