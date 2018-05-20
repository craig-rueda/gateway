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
import static com.google.common.collect.Lists.newArrayList;
import static reactor.core.publisher.Mono.empty;

/**
 * Created by Craig Rueda
 *
 * Injects a Forwarded-For header to be forwarded to upstreams
*/
@Slf4j
public class ForwardedForPreFilter extends AbstractGatewayFilter {
    private final boolean addForwardedForHeader;
    private final String forwardedForHeaderName;

    public ForwardedForPreFilter(GatewayConfiguration gatewayConfiguration) {
        super(PRE, 6);
        addForwardedForHeader = gatewayConfiguration.isAddForwardForHeader();
        forwardedForHeaderName = gatewayConfiguration.getForwardedForHeaderName();
    }

    @Override
    public boolean shouldFilter(FilteringContext ctx) {
        return addForwardedForHeader;
    }

    @Override
    public Mono<Void> doFilter(FilteringContext ctx) {
        ServerHttpRequest request = ctx.getExchange().getRequest();

        ctx.getUpstreamRequestHeaders().putIfAbsent(
            forwardedForHeaderName,
            newArrayList(request.getRemoteAddress().getHostName())
        );

        return empty();
    }
}
