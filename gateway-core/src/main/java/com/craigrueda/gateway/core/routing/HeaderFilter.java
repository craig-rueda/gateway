package com.craigrueda.gateway.core.routing;

import org.springframework.http.HttpHeaders;

/**
 * Created by Craig Rueda
 */
public interface HeaderFilter {
    HttpHeaders filterUpstreamRequestHeaders(HttpHeaders headers, Route route);

    HttpHeaders filterClientResponseHeaders(HttpHeaders headers, Route route);
}
