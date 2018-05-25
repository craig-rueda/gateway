package com.craigrueda.gateway.core.filter.route;

import com.craigrueda.gateway.core.filter.AbstractGatewayFilter;
import com.craigrueda.gateway.core.filter.ctx.FilteringContext;
import com.craigrueda.gateway.core.routing.MalformedRouteUrlException;
import com.craigrueda.gateway.core.routing.Route;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.reactive.ClientHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.URISyntaxException;

import static com.craigrueda.gateway.core.filter.DefaultGatewayFilterOrder.WebClientRoutingFilter;
import static java.net.URLEncoder.encode;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.joining;
import static org.springframework.util.StringUtils.hasText;
import static org.springframework.web.reactive.function.BodyInserters.fromDataBuffers;
import static reactor.core.publisher.Mono.empty;

/**
 * Created by Craig Rueda
 */
@Slf4j
public class WebClientRoutingFilter extends AbstractGatewayFilter {
    private final WebClient webClient;

    public WebClientRoutingFilter(WebClient webClient) {
        super(WebClientRoutingFilter.getFilterType(), WebClientRoutingFilter.getOrder());
        this.webClient = webClient;
    }

    @Override
    public boolean shouldFilter(FilteringContext ctx) {
        return ctx.getShouldSendResponse() && !ctx.isAlreadyRouted();
    }

    @Override
    public Mono<Void> doFilter(FilteringContext ctx) {
        URI requestUri = buildRequestUri(ctx);
        ctx.setAlreadyRouted(true);

        ServerHttpRequest request = ctx.getExchange().getRequest();
        HttpMethod method = request.getMethod();

        log.trace("Forwarding upstream request [{}] -> {}", method, requestUri);

        BodyInserter<?, ? super ClientHttpRequest> bodyInserter =
                ctx.isRequestHasBody() ? fromDataBuffers(request.getBody()) : null;
        WebClient.RequestHeadersSpec<?> requestSpec =
                webClient
                    .method(method)
                    .uri(requestUri)
                    .headers(httpHeaders -> httpHeaders.addAll(ctx.getUpstreamRequestHeaders()))
                    .body(bodyInserter);

        return requestSpec.exchange()
            .flatMap(res -> {
                ctx.setUpstreamResponse(res);
                return empty();
            });
    }

    URI buildRequestUri(FilteringContext ctx) {
        MultiValueMap<String, String> queryParams =
                ofNullable(ctx.getUpstreamQueryParams())
                        .orElseThrow(() -> new IllegalArgumentException("Query params must be set"));
        Route upstreamRoute = ctx.getUpstreamRequestRoute();
        String queryString =
            queryParams
                .entrySet()
                .stream()
                .flatMap(entry -> entry.getValue().stream().map(val -> entry.getKey() + "=" + encode(val)))
                .collect(joining("&"));
        String upstreamUri = upstreamRoute.getUpstreamUri().toString() + (hasText(queryString) ? "?" + queryString : "");

        try {
            return new URI(upstreamUri);
        }
        catch (URISyntaxException e) {
            throw new MalformedRouteUrlException("Failed to parse URI " + upstreamUri, e);
        }
    }
}
