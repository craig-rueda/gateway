package com.craigrueda.gateway.core.config;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

import static java.util.Collections.unmodifiableSet;

/**
 * Created by Craig Rueda
 */
@Data
@NoArgsConstructor
public class GatewayRoute {
    /**
     * The base url of the upstream that will handle requests for this route
     */
    private String url;
    /**
     * The path prefix that matches this route (see https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/util/AntPathMatcher.html)
     */
    private String path;
    /**
     * Should we strip the matched path prefix before forwarding the request to the upstream?
     */
    private Boolean stripPrefix;
    /**
     * Which headers should be stripped from the upstream response before returning to the client for this route only?
     */
    private Set<String> sensitiveClientResponseHeaders = new HashSet<>();
    /**
     * Which headers should be stripped from the request before forwarding to the upstream for this route only?
     */
    private Set<String> sensitiveUpstreamRequestHeaders = new HashSet<>();

    public GatewayRoute(String url, String path, Boolean stripPrefix) {
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
