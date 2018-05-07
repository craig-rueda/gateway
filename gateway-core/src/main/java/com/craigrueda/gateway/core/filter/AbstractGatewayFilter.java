package com.craigrueda.gateway.core.filter;

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
    public boolean shouldFilter(ServerWebExchange exchange) {
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
