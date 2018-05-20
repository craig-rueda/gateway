package com.craigrueda.gateway.core.filter.pre;

import com.craigrueda.gateway.core.config.GatewayConfiguration;
import com.craigrueda.gateway.core.filter.AbstractGatewayFilter;
import com.craigrueda.gateway.core.filter.ctx.FilteringContext;
import com.craigrueda.gateway.core.filter.route.RouteNotFoundException;
import com.craigrueda.gateway.core.routing.HeaderFilter;
import com.craigrueda.gateway.core.routing.Route;
import com.craigrueda.gateway.core.routing.RouteResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import reactor.core.publisher.Mono;

import static com.craigrueda.gateway.core.filter.GatewayFilterType.PRE;
import static reactor.core.publisher.Mono.empty;

/**
 * Created by Craig Rueda
*/
@Slf4j
public class RouteMappingPreFilter extends AbstractGatewayFilter {
    private RouteResolver routeResolver;
    private final boolean preserveHostHeader;
    private final HeaderFilter headerFilter;

    public RouteMappingPreFilter(RouteResolver routeResolver, GatewayConfiguration gatewayConfiguration,
                                 HeaderFilter headerFilter) {
        super(PRE, 5);
        this.routeResolver = routeResolver;
        this.preserveHostHeader = gatewayConfiguration.isPreserveHostHeader();
        this.headerFilter = headerFilter;
    }

    @Override
    public Mono<Void> doFilter(FilteringContext ctx) {
        ServerHttpRequest request = ctx.getExchange().getRequest();
        String requestPath = request.getPath().value();
        Route route = routeResolver.resolveRoute(requestPath);
        if (route == null) {
            log.warn("Failed to match path {} to any routes", requestPath);
            throw new RouteNotFoundException(requestPath);
        }
        else {
            ctx.setUpstreamRequestRoute(route);
            HttpHeaders upstreamHeaders = headerFilter.filterUpstreamRequestHeaders(request.getHeaders(), route);

            if (!preserveHostHeader) {
                upstreamHeaders.remove("host");
            }

            ctx.setUpstreamRequestHeaders(upstreamHeaders);
            ctx.setUpstreamQueryParams(request.getQueryParams());
            ctx.setShouldSendResponse(true);
            ctx.setOriginalUri(request.getURI());

            log.debug("Mapping upstream request {} num:{} to route {}", request.getPath(), ctx.getRequestNum(), route);
        }

        return empty();
    }
}
