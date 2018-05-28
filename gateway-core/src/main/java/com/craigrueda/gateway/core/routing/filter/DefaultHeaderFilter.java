package com.craigrueda.gateway.core.routing.filter;

import com.craigrueda.gateway.core.config.GatewayConfiguration;
import com.craigrueda.gateway.core.routing.Route;
import org.springframework.http.HttpHeaders;

import java.util.Set;

import static com.google.common.collect.Sets.union;
import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.toSet;

/**
 * Created by Craig Rueda
 */
public class DefaultHeaderFilter implements HeaderFilter {
    private final Set<String> sensitiveClientResponseHeaders;
    private final Set<String> sensitiveUpstreamRequestHeaders;

    public DefaultHeaderFilter(GatewayConfiguration gatewayConfiguration) {
        this.sensitiveClientResponseHeaders = gatewayConfiguration
                .getSensitiveClientResponseHeaders()
                .stream()
                .map(String::toLowerCase)
                .collect(toSet());
        this.sensitiveUpstreamRequestHeaders = gatewayConfiguration
                .getSensitiveUpstreamRequestHeaders()
                .stream()
                .map(String::toLowerCase)
                .collect(toSet());
    }

    @Override
    public HttpHeaders filterUpstreamRequestHeaders(HttpHeaders headers, Route route) {
        return doFilterHeaders(
                headers,
                sensitiveUpstreamRequestHeaders,
                route.getMatchedRouteRule().getSensitiveUpstreamRequestHeaders()
        );
    }

    @Override
    public HttpHeaders filterClientResponseHeaders(HttpHeaders headers, Route route) {
        return doFilterHeaders(
                headers,
                sensitiveClientResponseHeaders,
                route.getMatchedRouteRule().getSensitiveClientResponseHeaders()
        );
    }

    protected HttpHeaders doFilterHeaders(HttpHeaders headers, Set<String> globalFiltered, Set<String> routeFiltered) {
        HttpHeaders ret = new HttpHeaders();
        Set<String> toFilter = union(
                globalFiltered == null ? emptySet() : globalFiltered,
                routeFiltered == null ? emptySet() : routeFiltered
        );

        if (headers != null) {
            headers
                .entrySet()
                .stream()
                .filter(entry -> !toFilter.contains(entry.getKey().toLowerCase()))
                .forEach(entry -> ret.put(entry.getKey(), entry.getValue()));
        }

        return ret;
    }
}
