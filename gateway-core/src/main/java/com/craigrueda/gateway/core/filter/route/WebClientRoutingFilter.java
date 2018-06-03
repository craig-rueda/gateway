package com.craigrueda.gateway.core.filter.route;

import com.craigrueda.gateway.core.filter.ctx.FilteringContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.reactive.ClientHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;

import static com.craigrueda.gateway.core.filter.DefaultGatewayFilterOrder.WebClientRoutingFilter;
import static org.springframework.web.reactive.function.BodyInserters.fromDataBuffers;
import static reactor.core.publisher.Mono.empty;

/**
 * Created by Craig Rueda
 */
@Slf4j
public class WebClientRoutingFilter extends BaseRoutingFilter {
    private final WebClient webClient;

    public WebClientRoutingFilter(WebClient webClient) {
        super(WebClientRoutingFilter.getFilterType(), WebClientRoutingFilter.getOrder(), "http");
        this.webClient = webClient;
    }

    @Override
    public Mono<Void> doHandleRequest(URI upstreamUri, FilteringContext ctx) {
        ServerHttpRequest request = ctx.getExchange().getRequest();
        HttpMethod method = request.getMethod();

        log.trace("Forwarding upstream request [{}] -> {}", method, upstreamUri);

        BodyInserter<?, ? super ClientHttpRequest> bodyInserter =
                ctx.isRequestHasBody() ? fromDataBuffers(request.getBody()) : null;
        WebClient.RequestHeadersSpec<?> requestSpec =
                webClient
                    .method(method)
                    .uri(upstreamUri)
                    .headers(httpHeaders -> httpHeaders.addAll(ctx.getUpstreamRequestHeaders()))
                    .body(bodyInserter);

        return requestSpec.exchange()
            .flatMap(res -> {
                ctx.setUpstreamResponse(res);
                return empty();
            });
    }
}
