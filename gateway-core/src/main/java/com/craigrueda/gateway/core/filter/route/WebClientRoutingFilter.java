package com.craigrueda.gateway.core.filter.route;

import com.craigrueda.gateway.core.filter.AbstractGatewayFilter;
import com.craigrueda.gateway.core.filter.ctx.FilteringContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;

import static com.craigrueda.gateway.core.filter.GatewayFilterType.ROUTE;
import static org.springframework.http.HttpMethod.TRACE;
import static org.springframework.web.reactive.function.BodyInserters.fromDataBuffers;
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
