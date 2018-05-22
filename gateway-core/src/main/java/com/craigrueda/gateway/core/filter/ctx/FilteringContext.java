package com.craigrueda.gateway.core.filter.ctx;

import com.craigrueda.gateway.core.routing.Route;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.ipc.netty.http.client.HttpClientResponse;

import java.net.URI;

/**
 * Created by Craig Rueda
   */
public interface FilteringContext {
    String
        CTX_HTTP_STATUS = "CTX_HTTP_STATUS",
        CTX_REQ_ERROR = "CTX_REQ_ERROR",
        CTX_REQ_ROUTED = "CTX_REQ_ROUTED",
        CTX_REQ_ORIG_URI = "CTX_REQ_ORIG_URI",
        CTX_SHOULD_SEND_RESP = "CTX_SHOULD_SEND_RESP",
        CTX_UPSTREAM_RESP = "CTX_UPSTREAM_RESP",
        CTX_UPSTREAM_RESP_BODY = "CTX_UPSTREAM_RESP_BODY",
        CTX_UPSTREAM_RESP_HEADERS = "CTX_UPSTREAM_RESP_HEADERS",
        CTX_UPSTREAM_REQ_QUERY = "CTX_UPSTREAM_REQ_QUERY",
        CTX_UPSTREAM_REQ_ROUTE = "CTX_UPSTREAM_REQ_ROUTE",
        CTX_UPSTREAM_REQ_HEADERS = "CTX_UPSTREAM_REQ_HEADERS";


    ServerWebExchange getExchange();
    void setExchange(ServerWebExchange exchange);

    void setAttribute(String key);
    void setAttribute(String key, Object val);
    <T> T getAttribute(String key);

    void setOriginalUri(URI uri);
    URI getOriginalUri();

    boolean getShouldSendResponse();
    void setShouldSendResponse(boolean shouldSend);

    ClientResponse getUpstreamResponse();
    void setUpstreamResponse(ClientResponse response);

    HttpHeaders getUpstreamRequestHeaders();
    void setUpstreamRequestHeaders(HttpHeaders headers);

    HttpHeaders getClientResponseHeaders();
    void setClientResponseHeaders(HttpHeaders headers);

    Route getUpstreamRequestRoute();
    void setUpstreamRequestRoute(Route route);

    void setAlreadyRouted(boolean alreadyRouted);
    boolean isAlreadyRouted();

    long getRequestNum();
    long getStartTimeNs();

    HttpStatus getResponseStatus();
    void setResponseStatus(HttpStatus status);

    void setError(Throwable throwable);
    Throwable getError();

    void setUpstreamQueryParams(MultiValueMap<String, String> queryParams);
    MultiValueMap<String, String> getUpstreamQueryParams();

    void setUpstreamResponseBody(Flux<DataBuffer> body);
    Flux<DataBuffer> getUpstreamResponseBody();
}
