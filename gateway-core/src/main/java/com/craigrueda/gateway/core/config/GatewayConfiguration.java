package com.craigrueda.gateway.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Collections.unmodifiableSet;

/**
 * Created by Craig Rueda
 */
@Configuration
@ConfigurationProperties("gateway")
@Data
public class GatewayConfiguration {
    /**
     * Should we pass the host header read from the client upstream?
     */
    private boolean preserveHostHeader = false;
    /**
     * Should we inject `X-Forwarded-xxx` headers?
     */
    private boolean addForwardedHeaders = false;
    /**
     * Which headers should be stripped from the upstream response before returning to the client?
     */
    private Set<String> sensitiveClientResponseHeaders = new HashSet<>();
    /**
     * Which headers should be stripped from the request before forwarding to the upstream?
     */
    private Set<String> sensitiveUpstreamRequestHeaders = new HashSet<>();
    private List<GatewayRoute> routes = new ArrayList<>();
    private GatewayUpstream upstream = new GatewayUpstream();

    public void setSensitiveClientResponseHeaders(Set<String> sensitiveClientResponseHeaders) {
        this.sensitiveClientResponseHeaders = unmodifiableSet(sensitiveClientResponseHeaders);
    }

    public void setSensitiveUpstreamRequestHeaders(Set<String> sensitiveUpstreamRequestHeaders) {
        this.sensitiveUpstreamRequestHeaders = unmodifiableSet(sensitiveUpstreamRequestHeaders);
    }
}
