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
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import reactor.core.publisher.Mono;

import static com.craigrueda.gateway.core.filter.DefaultGatewayFilterOrder.RouteMappingPreFilter;
import static org.springframework.http.HttpMethod.TRACE;
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
        super(RouteMappingPreFilter.getFilterType(), RouteMappingPreFilter.getOrder());
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
            ctx.setRequestHasBody(hasBody(request.getMethod(), request.getHeaders()));

            log.debug("Mapping upstream request {} num:{} to route {}", request.getPath(), ctx.getRequestNum(), route);
        }

        return empty();
    }

    boolean hasBody(HttpMethod method, HttpHeaders headers) {
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
