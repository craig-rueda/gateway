package com.craigrueda.gateway.core.filter;

import com.craigrueda.gateway.core.filter.ctx.FilteringContext;
import org.springframework.web.server.ServerWebExchange;

/**
 * Created by Craig Rueda
   */
public abstract class AbstractGatewayFilter implements GatewayFilter {
    private final GatewayFilterType filterType;
    private final int order;

    public AbstractGatewayFilter(GatewayFilterType filterType, int order) {
        this.filterType = filterType;
        this.order = order;
    }

    @Override
    public boolean shouldFilter(FilteringContext ctx) {
        return true;
    }

    @Override
    public int getOrder() {
        return order;
    }

    @Override
    public GatewayFilterType getFilterType() {
        return filterType;
    }
}
