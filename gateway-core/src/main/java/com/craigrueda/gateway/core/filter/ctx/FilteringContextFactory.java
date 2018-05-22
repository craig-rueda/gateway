package com.craigrueda.gateway.core.filter.ctx;

import org.springframework.web.server.ServerWebExchange;

/**
 * Created by Craig Rueda
 */
public interface FilteringContextFactory {
    default FilteringContext buildContext(ServerWebExchange exchange) {
        return new DefaultFilteringContext(exchange);
    }
}
