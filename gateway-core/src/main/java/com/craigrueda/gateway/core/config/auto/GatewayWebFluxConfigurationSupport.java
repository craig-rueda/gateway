package com.craigrueda.gateway.core.config.auto;

import com.craigrueda.gateway.core.filter.AppCtxGatewayFilterSource;
import com.craigrueda.gateway.core.filter.GatewayFilterSource;
import com.craigrueda.gateway.core.filter.ctx.FilteringContextFactory;
import com.craigrueda.gateway.core.handler.web.GatewayHandlerMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.config.WebFluxConfigurationSupport;

/**
 * Created by Craig Rueda
 */
@Configuration
public class GatewayWebFluxConfigurationSupport extends WebFluxConfigurationSupport {
    private final GatewayFilterSource gatewayFilterSource;
    private final FilteringContextFactory filteringContextFactory;

    public GatewayWebFluxConfigurationSupport(
            @Autowired GatewayFilterSource gatewayFilterSource,
            @Autowired FilteringContextFactory filteringContextFactory) {
        this.gatewayFilterSource = gatewayFilterSource;
        this.filteringContextFactory = filteringContextFactory;
    }

    @Bean
    public GatewayFilterSource gatewayFilterSource() {
        return new AppCtxGatewayFilterSource();
    }

    @Override
    public HandlerMapping resourceHandlerMapping() {
        return new GatewayHandlerMapping(gatewayFilterSource, filteringContextFactory);
    }
}
