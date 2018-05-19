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
    private boolean preserveHostHeader = false;
    private Set<String> sensitiveClientResponseHeaders = new HashSet<>();
    private Set<String> sensitiveUpstreamRequestHeaders = new HashSet<>();
    private List<GatewayRoute> routes = new ArrayList<>();

    public void setSensitiveClientResponseHeaders(Set<String> sensitiveClientResponseHeaders) {
        this.sensitiveClientResponseHeaders = unmodifiableSet(sensitiveClientResponseHeaders);
    }

    public void setSensitiveUpstreamRequestHeaders(Set<String> sensitiveUpstreamRequestHeaders) {
        this.sensitiveUpstreamRequestHeaders = unmodifiableSet(sensitiveUpstreamRequestHeaders);
    }
}
