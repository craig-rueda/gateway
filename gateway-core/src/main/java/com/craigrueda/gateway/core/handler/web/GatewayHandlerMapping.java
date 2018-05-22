package com.craigrueda.gateway.core.handler.web;

import com.craigrueda.gateway.core.filter.GatewayFilterSource;
import com.craigrueda.gateway.core.filter.ctx.FilteringContextFactory;
import com.craigrueda.gateway.core.handler.web.FilterAssemblingWebHandler;
import org.springframework.web.reactive.handler.AbstractHandlerMapping;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static reactor.core.publisher.Mono.just;

/**
 * Created by Craig Rueda
 */
public class GatewayHandlerMapping extends AbstractHandlerMapping {
    private FilterAssemblingWebHandler webHandler;
    private final FilteringContextFactory filteringContextFactory;

    public GatewayHandlerMapping(GatewayFilterSource gatewayFilterSource, FilteringContextFactory filteringContextFactory) {
        gatewayFilterSource.registerSourceUpdatedCallback(this::onFiltersUpdated);
        this.filteringContextFactory = filteringContextFactory;
    }

    protected FilterAssemblingWebHandler doConstructWebHandler(GatewayFilterSource filterSource) {
        return new FilterAssemblingWebHandler(filterSource.getMergedFilters(), filteringContextFactory);
    }

    protected void onFiltersUpdated(GatewayFilterSource source) {
        this.webHandler = doConstructWebHandler(source);
    }

    @Override
    protected Mono<?> getHandlerInternal(ServerWebExchange exchange) {
        return just(webHandler);
    }
}
