package com.craigrueda.gateway.core.filter.route;

import com.craigrueda.gateway.core.filter.AbstractGatewayFilter;
import com.craigrueda.gateway.core.filter.GatewayFilterType;
import com.craigrueda.gateway.core.filter.ctx.FilteringContext;
import com.craigrueda.gateway.core.routing.Route;
import com.craigrueda.gateway.core.routing.resolve.MalformedRouteUrlException;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.URISyntaxException;

import static java.net.URLEncoder.encode;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.joining;
import static org.springframework.util.StringUtils.hasText;

/**
 * Created by Craig Rueda
 */
public abstract class BaseRoutingFilter extends AbstractGatewayFilter {
    private final String schemeToHandle;

    protected BaseRoutingFilter(GatewayFilterType filterType, int order, String schemeToHandle) {
        super(filterType, order);
        this.schemeToHandle = schemeToHandle;
    }

    @Override
    public boolean shouldFilter(FilteringContext ctx) {
        String scheme = ctx.getRequestUri().getScheme();
        return ctx.getShouldSendResponse() && !ctx.isAlreadyRouted() && scheme.startsWith(schemeToHandle);
    }

    @Override
    public Mono<Void> doFilter(FilteringContext ctx) {
        URI upstreamUri = buildRequestUri(ctx);
        ctx.setAlreadyRouted(true);

        return doHandleRequest(upstreamUri, ctx);
    }

    protected abstract Mono<Void> doHandleRequest(URI upstreamUri, FilteringContext ctx);

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
