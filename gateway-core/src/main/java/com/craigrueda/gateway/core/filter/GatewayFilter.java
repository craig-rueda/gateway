package com.craigrueda.gateway.core.filter;

import com.craigrueda.gateway.core.filter.ctx.FilteringContext;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Created by Craig Rueda
   */
public interface GatewayFilter extends Comparable<GatewayFilter> {
    boolean shouldFilter(ServerWebExchange exchange);
    int getOrder();
    GatewayFilterType getFilterType();
    Mono<Void> doFilter(FilteringContext ctx);

    @Override
    default int compareTo(GatewayFilter o) {
        GatewayFilterType otherType = o.getFilterType(),
                thisType = getFilterType();
        int typeDiff = thisType.getFilterOrdinal() - otherType.getFilterOrdinal();

        if (typeDiff != 0) {
            return typeDiff;
        }

        return getOrder() - o.getOrder();
    }
}
