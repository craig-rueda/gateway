package com.craigrueda.gateway.core.filter.route;

import com.craigrueda.gateway.core.filter.AbstractGatewayFilter;
import com.craigrueda.gateway.core.filter.GatewayFilterType;
import com.craigrueda.gateway.core.filter.ctx.FilteringContext;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.NettyDataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.AbstractServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.ipc.netty.NettyPipeline;
import reactor.ipc.netty.http.client.HttpClient;
import reactor.ipc.netty.http.client.HttpClientRequest;

import java.net.URI;

import static com.craigrueda.gateway.core.filter.GatewayFilterType.ROUTE;
import static org.springframework.http.HttpMethod.TRACE;
import static org.springframework.web.reactive.function.BodyInserters.fromDataBuffers;
import static reactor.core.publisher.Mono.defer;
import static reactor.core.publisher.Mono.empty;

/**
 * Created by Craig Rueda
 */
@Slf4j
public class WebClientRoutingFilter extends AbstractGatewayFilter {
    private final WebClient webClient;

    public WebClientRoutingFilter(WebClient webClient) {
        super(ROUTE, 100);
        this.webClient = webClient;
    }

    @Override
    public Mono<Void> doFilter(FilteringContext ctx) {
        URI requestUrl = ctx.getUpstreamRequestUrl();
        if (requestUrl != null) {
            throw new RuntimeException("oops");
        }

        String scheme = requestUrl.getScheme();
        if (ctx.isAlreadyRouted() || (!"http".equals(scheme) && !"https".equals(scheme))) {
            return empty();
        }
        ctx.setAlreadyRouted(true);

        ServerHttpRequest request = ctx.getExchange().getRequest();
        HttpMethod method = request.getMethod();

        WebClient.RequestHeadersSpec<?> requestSpec =
                webClient.method(method)
                .uri(requestUrl)
                .headers(httpHeaders -> httpHeaders.addAll(ctx.getUpstreamRequestHeaders()))
                .body(
                    hasBody(method, request.getHeaders()) ?
                        fromDataBuffers(request.getBody()) : null
                );

        return requestSpec.exchange()
            .flatMap(res -> {
                ctx.setUpstreamResponse(res);
                return empty();
            });

        /*URI requestUrl = ctx.getUpstreamRequestUrl();

        String scheme = requestUrl.getScheme();
        if (ctx.isAlreadyRouted() || (!"http".equals(scheme) && !"https".equals(scheme))) {
            log.warn("Request already routed. Skipping...");
            return empty();
        }
        ctx.setAlreadyRouted();

        ServerHttpRequest request = ctx.getExchange().getRequest();

        final io.netty.handler.codec.http.HttpMethod method = io.netty.handler.codec.http.HttpMethod.valueOf(request.getMethod().toString());
        final String url = requestUrl.toString();

        HttpHeaders filtered = ctx.getUpstreamRequestHeaders();

        final DefaultHttpHeaders httpHeaders = new DefaultHttpHeaders();
        filtered.forEach(httpHeaders::set);

        String transferEncoding = request.getHeaders().getFirst(HttpHeaders.TRANSFER_ENCODING);
        boolean chunkedTransfer = "chunked".equalsIgnoreCase(transferEncoding);

        boolean preserveHost = true;//exchange.getAttributeOrDefault(PRESERVE_HOST_HEADER_ATTRIBUTE, false);

        return this.httpClient.request(method, url, req -> {
            final HttpClientRequest proxyRequest = req.options(NettyPipeline.SendOptions::flushOnEach)
                    .headers(httpHeaders)
                    .chunkedTransfer(chunkedTransfer)
                    .failOnServerError(false)
                    .failOnClientError(false);

            if (preserveHost) {
                String host = request.getHeaders().getFirst(HttpHeaders.HOST);
                proxyRequest.header(HttpHeaders.HOST, host);
            }
            log.debug("Sending upstream request for request {}", ctx.getRequestNum());

            return proxyRequest.sendHeaders() //I shouldn't need this
                    .send(request.getBody().map(dataBuffer ->
                            ((NettyDataBuffer)dataBuffer).getNativeBuffer()));
        }).doOnNext(res -> {
            log.debug("Received upstream response for request {}", ctx.getRequestNum());
            ServerHttpResponse response = ctx.getExchange().getResponse();
            // put headers and status so filters can modify the response
            HttpHeaders headers = new HttpHeaders();

            res.responseHeaders().forEach(entry -> headers.add(entry.getKey(), entry.getValue()));

            HttpHeaders filteredResponseHeaders = headers;

            response.getHeaders().putAll(filteredResponseHeaders);
            HttpStatus status = HttpStatus.resolve(res.status().code());
            if (status != null) {
                response.setStatusCode(status);
            } else if (response instanceof AbstractServerHttpResponse) {
                // https://jira.spring.io/browse/SPR-16748
                ((AbstractServerHttpResponse) response).setStatusCodeValue(res.status().code());
            } else {
                throw new IllegalStateException("Unable to set status code on response: " +res.status().code()+", "+response.getClass());
            }

            // Defer committing the response until all route filters have run
            // Put client response as ServerWebExchange attribute and write response later NettyWriteResponseFilter
            ctx.setUpstreamResponse(res);
        }).then();*/
    }

    private boolean hasBody(HttpMethod method, HttpHeaders headers) {
        if (method == TRACE) {
            // TRACE explicitly forbids a body
            return false;
        }
        else {
            // The presence of either a content length, or a given encoding signals the existence of a body...
            return headers.containsKey("Content-Length") || headers.containsKey("Transfer-Encoding");
        }
    }
}
