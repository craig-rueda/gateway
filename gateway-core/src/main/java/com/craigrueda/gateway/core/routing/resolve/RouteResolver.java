package com.craigrueda.gateway.core.routing.resolve;

import com.craigrueda.gateway.core.routing.Route;

/**
 * Created by Craig Rueda
 */
public interface RouteResolver {
    Route resolveRoute(String path);
}
