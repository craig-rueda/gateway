package com.craigrueda.gateway.core.routing;

import com.craigrueda.gateway.core.config.GatewayRoute;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.URI;

/**
 * Created by Craig Rueda
 */
@Data
@NoArgsConstructor
public class Route {
    private URI upstreamUri;
    private String matchedPath;
    private String verb;
    private String hostHeader;
    private GatewayRoute matchedRouteRule;

    public Route(URI upstreamUri, String matchedPath, String verb, GatewayRoute matchedRouteRule) {
        this.upstreamUri = upstreamUri;
        this.matchedPath = matchedPath;
        this.verb = verb;
        this.matchedRouteRule = matchedRouteRule;
    }
}
