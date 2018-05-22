package com.craigrueda.gateway.core.config.auto;

import com.craigrueda.gateway.core.config.GatewayConfiguration;
import com.craigrueda.gateway.core.filter.AppCtxGatewayFilterSource;
import com.craigrueda.gateway.core.filter.GatewayFilterSource;
import com.craigrueda.gateway.core.filter.ctx.FilteringContextFactory;
import com.craigrueda.gateway.core.filter.error.WebExceptionHandlingErrorFilter;
import com.craigrueda.gateway.core.filter.post.UpstreamResponseHandlingPostFilter;
import com.craigrueda.gateway.core.filter.pre.ForwardedForPreFilter;
import com.craigrueda.gateway.core.filter.pre.RouteMappingPreFilter;
import com.craigrueda.gateway.core.filter.response.WriteResponseFilter;
import com.craigrueda.gateway.core.filter.route.WebClientRoutingFilter;
import com.craigrueda.gateway.core.handler.error.GatewayErrorHandlerConfiguration;
import com.craigrueda.gateway.core.handler.error.GatewayWebExceptionHandler;
import com.craigrueda.gateway.core.routing.DefaultHeaderFilter;
import com.craigrueda.gateway.core.routing.DefaultRouteResolver;
import com.craigrueda.gateway.core.routing.HeaderFilter;
import com.craigrueda.gateway.core.routing.RouteResolver;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.reactive.HttpHandlerAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.reactive.DispatcherHandler;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Created by Craig Rueda
   */
@Configuration
@ConditionalOnProperty(name = "gateway.enabled", matchIfMissing = true)
@EnableConfigurationProperties
@AutoConfigureBefore(HttpHandlerAutoConfiguration.class)
@ConditionalOnClass(DispatcherHandler.class)
@Import({
        GatewayConfiguration.class,
        GatewayErrorHandlerConfiguration.class,
        GatewayWebClientConfigration.class,
        GatewayWebFluxConfigurationSupport.class
})
public class GatewayAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public RouteResolver routeResolver(GatewayConfiguration gatewayConfiguration) {
        return new DefaultRouteResolver(gatewayConfiguration.getRoutes());
    }

    @Bean
    @ConditionalOnMissingBean
    public HeaderFilter headerFilter(GatewayConfiguration gatewayConfiguration) {
        return new DefaultHeaderFilter(gatewayConfiguration);
    }

    @Bean
    @ConditionalOnMissingBean
    public FilteringContextFactory filteringContextFactory() {
        return new FilteringContextFactory() {};
    }

    @Bean
    @ConditionalOnMissingBean
    public GatewayFilterSource gatewayFilterSource() {
        return new AppCtxGatewayFilterSource();
    }

    @Bean
    public ForwardedForPreFilter forwardedForPreFilter(GatewayConfiguration gatewayConfiguration) {
        return new ForwardedForPreFilter(gatewayConfiguration);
    }

    @Bean
    public RouteMappingPreFilter routeMappingPreFilter(RouteResolver routeResolver,
                                                       GatewayConfiguration gatewayConfiguration,
                                                       HeaderFilter headerFilter) {
        return new RouteMappingPreFilter(routeResolver, gatewayConfiguration, headerFilter);
    }

    @Bean
    public WriteResponseFilter writeResponseFilter() {
        return new WriteResponseFilter();
    }

    @Bean
    public WebClientRoutingFilter webClientRoutingFilter(WebClient webClient) {
        return new WebClientRoutingFilter(webClient);
    }

    @Bean
    public WebExceptionHandlingErrorFilter webExceptionHandlingGatewayFilter(GatewayWebExceptionHandler exceptionHandler) {
        return new WebExceptionHandlingErrorFilter(exceptionHandler);
    }

    @Bean
    public UpstreamResponseHandlingPostFilter headerFilteringPostFilter(HeaderFilter headerFilter) {
        return new UpstreamResponseHandlingPostFilter(headerFilter);
    }
}
