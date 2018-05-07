package com.craigrueda.gateway.core.handler;

import com.craigrueda.gateway.core.filter.GatewayFilterSource;
import org.springframework.http.server.PathContainer;
import org.springframework.web.reactive.handler.AbstractUrlHandlerMapping;
import org.springframework.web.server.ServerWebExchange;

import java.util.function.Function;

/**
 * Created by Craig Rueda
   */
public class GatewayHandlerMapping extends AbstractUrlHandlerMapping {
    private FilterChainingWebHandler webHandler;

    public GatewayHandlerMapping(GatewayFilterSource gatewayFilterSource) {
        this.webHandler = doConstructWebHandler(gatewayFilterSource);
        gatewayFilterSource.registerSourceUpdatedCallback(this::onFiltersUpdated);
    }

    @Override
    protected Object lookupHandler(PathContainer lookupPath, ServerWebExchange exchange) {
        return webHandler;
    }

    protected FilterChainingWebHandler doConstructWebHandler(GatewayFilterSource filterSource) {
        return new FilterChainingWebHandler(filterSource.getMergedFilters());
    }

    protected void onFiltersUpdated(GatewayFilterSource source) {
        this.webHandler = doConstructWebHandler(source);
    }
}
