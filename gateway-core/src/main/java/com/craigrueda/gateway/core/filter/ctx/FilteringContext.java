package com.craigrueda.gateway.core.filter.ctx;

import org.springframework.http.HttpHeaders;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.ipc.netty.http.client.HttpClientResponse;

import java.net.URI;

/**
 * Created by Craig Rueda
   */
public interface FilteringContext {
    String
        CTX_REQ_ERROR = "CTX_REQ_ERROR",
        CTX_REQ_ROUTED = "CTX_REQ_ROUTED",
        CTX_REQ_ORIG_URI = "CTX_REQ_ORIG_URI",
        CTX_SHOULD_SEND_RESP = "CTX_SHOULD_SEND_RESP",
        CTX_UPSTREAM_RESP = "CTX_UPSTREAM_RESP",
        CTX_UPSTREAM_REQ_URI = "CTX_UPSTREAM_REQ_URI",
        CTX_UPSTREAM_REQ_HEADERS = "CTX_UPSTREAM_REQ_HEADERS";


    ServerWebExchange getExchange();

    void setExchange(ServerWebExchange exchange);

    void setAttribute(String key);

    void setAttribute(String key, Object val);

    <T> T getAttribute(String key);

    void setOriginalUri(String uri);

    String getOriginalUri();

    boolean getShouldSendResponse();
    void setShouldSendResponse(boolean shouldSend);

    ClientResponse getUpstreamResponse();
    void setUpstreamResponse(ClientResponse response);

    HttpHeaders getUpstreamRequestHeaders();
    void setUpstreamRequestHeaders(HttpHeaders headers);

    URI getUpstreamRequestUrl();
    void setUpstreamRequestUrl(URI uri);

    void setAlreadyRouted(boolean alreadyRouted);
    boolean isAlreadyRouted();

    long getRequestNum();

    void setError(Throwable throwable);
    Throwable getError();
}
