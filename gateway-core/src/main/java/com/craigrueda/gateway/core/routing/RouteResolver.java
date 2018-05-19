package com.craigrueda.gateway.core.routing;

import java.net.URISyntaxException;

/**
 * Created by Craig Rueda
 */
public interface RouteResolver {
    Route resolveRoute(String path, String verb);
}
