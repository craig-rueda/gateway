package com.craigrueda.gateway.core.filter;

import static com.craigrueda.gateway.core.filter.GatewayFilterType.*;
import static java.lang.Integer.MAX_VALUE;

/**
 * Created by Craig Rueda
 *
 * Basically a one-stop-shop for filter ordering...
 */
public enum DefaultGatewayFilterOrder {
    RouteMappingPreFilter(PRE, 50),
    ForwardedForPreFilter(PRE, 60),
    HopByHopPreFilter(PRE, 70),

    WebClientRoutingFilter(ROUTE, 50),

    UpstreamResponseHandlingPostFilter(POST, 50),
    HopByHopPostFilter(POST, 60),

    WriteResponseFilter(RESPONSE, 50),

    WebExceptionHandlingErrorFilter(ERROR, MAX_VALUE);

    private GatewayFilterType filterType;
    private int order;

    DefaultGatewayFilterOrder(GatewayFilterType filterType, int order) {
        this.filterType = filterType;
        this.order = order;
    }

    public GatewayFilterType getFilterType() {
        return filterType;
    }

    public int getOrder() {
        return order;
    }
}
