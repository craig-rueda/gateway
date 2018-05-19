package com.craigrueda.gateway.core.config;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static java.util.Collections.unmodifiableSet;

/**
 * Created by Craig Rueda
 */
@Data
@NoArgsConstructor
public class GatewayRoute {
    private String verb;
    private String url;
    private String path;
    private Boolean stripPrefix;
    private Boolean retryable;
    private Set<String> sensitiveClientResponseHeaders = new HashSet<>();
    private Set<String> sensitiveUpstreamRequestHeaders = new HashSet<>();

    public GatewayRoute(String verb, String url, String path, Boolean stripPrefix) {
        this.verb = verb;
        this.url = url;
        this.path = path;
        this.stripPrefix = stripPrefix;
    }

    public void setSensitiveClientResponseHeaders(Set<String> sensitiveClientResponseHeaders) {
        this.sensitiveClientResponseHeaders = unmodifiableSet(sensitiveClientResponseHeaders);
    }

    public void setSensitiveUpstreamRequestHeaders(Set<String> sensitiveUpstreamRequestHeaders) {
        this.sensitiveUpstreamRequestHeaders = unmodifiableSet(sensitiveUpstreamRequestHeaders);
    }
}
