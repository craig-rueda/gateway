package com.craigrueda.gateway.core.routing;

import com.craigrueda.gateway.core.config.GatewayRoute;
import com.google.common.base.CharMatcher;
import org.springframework.core.NestedRuntimeException;
import org.springframework.util.AntPathMatcher;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Boolean.TRUE;

/**
 * Created by Craig Rueda
 */
public class DefaultRouteResolver implements RouteResolver {
    private Map<String, GatewayRoute> pathMap;
    private AntPathMatcher matcher = new AntPathMatcher();

    public DefaultRouteResolver(List<GatewayRoute> routeConfigurations) {
        reloadPaths(routeConfigurations);
    }

    public void reloadPaths(List<GatewayRoute> routeConfigurations) {
        Map<String, GatewayRoute> localPathMap = new LinkedHashMap<>();

        routeConfigurations.forEach(r -> localPathMap.put(r.getPath(), r));

        this.pathMap = localPathMap;
    }

    @Override
    public Route resolveRoute(String path, String verb) {
        // Make sure paths are root-based
        if (path == null) {
            path = "/";
        }
        else if (!path.startsWith("/")) {
            path = "/" + path;
        }

        for (Map.Entry<String, GatewayRoute> ent : pathMap.entrySet()) {
            if (matcher.match(ent.getKey(), path)) {
                try {
                    return doBuildRoute(path, verb, ent.getValue());
                } catch (URISyntaxException e) {
                    throw new MalformedRouteUrlException("Failed to parse URI " + path, e);
                }
            }
        }

        return null;
    }

    protected Route doBuildRoute(String matchedPath, String verb, GatewayRoute gatewayRoute) throws URISyntaxException {
        if (TRUE.equals(gatewayRoute.getStripPrefix())) {
            matchedPath = matcher.extractPathWithinPattern(gatewayRoute.getPath(), matchedPath);

            if (!matchedPath.startsWith("/")) {
                matchedPath = "/" + matchedPath;
            }
        }

        String upstreamUrl =
                CharMatcher.is('/').trimTrailingFrom(
                        gatewayRoute.getUrl() + matchedPath
                );

        return new Route(new URI(upstreamUrl), gatewayRoute.getPath(), verb, gatewayRoute);
    }
}
