package com.craigrueda.gateway.core.config;

import com.craigrueda.gateway.core.filter.AppCtxGatewayFilterSource;
import com.craigrueda.gateway.core.filter.GatewayFilterSource;
import com.craigrueda.gateway.core.filter.error.ErrorLoggingGatewayFilter;
import com.craigrueda.gateway.core.filter.error.WebExceptionHandlingGatewayFilter;
import com.craigrueda.gateway.core.filter.pre.RouteMappingPreFilter;
import com.craigrueda.gateway.core.filter.response.WriteResponseFilter;
import com.craigrueda.gateway.core.filter.route.WebClientRoutingFilter;
import com.craigrueda.gateway.core.handler.GatewayHandlerMapping;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.reactive.HttpHandlerAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.DispatcherHandler;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.config.WebFluxConfigurationSupport;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.WebExceptionHandler;
import reactor.ipc.netty.http.client.HttpClient;
import reactor.ipc.netty.http.client.HttpClientOptions;
import reactor.ipc.netty.resources.PoolResources;

import java.util.function.Consumer;

import static org.springframework.web.reactive.function.client.WebClient.builder;

/**
 * Created by Craig Rueda
   */
@Configuration
@ConditionalOnProperty(name = "com.craigrueda.gateway.enabled", matchIfMissing = true)
@EnableConfigurationProperties
@AutoConfigureBefore(HttpHandlerAutoConfiguration.class)
@ConditionalOnClass(DispatcherHandler.class)
public class GatewayAutoConfiguration implements InitializingBean {
    @Autowired
    private NettyReactiveWebServerFactory nettyReactiveWebServerFactory;

    @Bean
    public GatewayFilterSource gatewayFilterSource() {
        return new AppCtxGatewayFilterSource();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        nettyReactiveWebServerFactory = null;
    }

    @Bean
    public RouteMappingPreFilter routeMappingPreFilter() {
        return new RouteMappingPreFilter();
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
    public ErrorLoggingGatewayFilter errorLoggingGatewayFilter() {
        return new ErrorLoggingGatewayFilter();
    }

    @Bean
    public WebExceptionHandlingGatewayFilter webExceptionHandlingGatewayFilter(
            @Qualifier("errorWebExceptionHandler") WebExceptionHandler exceptionHandler) {
        return new WebExceptionHandlingGatewayFilter(exceptionHandler);
    }

    @Configuration
    public static class GatewayWebFluxConfigurationSupport extends WebFluxConfigurationSupport {
        @Autowired
        private GatewayFilterSource gatewayFilterSource;

        @Override
        public HandlerMapping resourceHandlerMapping() {
            return new GatewayHandlerMapping(gatewayFilterSource);
        }
    }

    @Configuration
    @ConditionalOnClass(HttpClient.class)
    protected static class NettyConfiguration {
       /* @Bean
        public HttpClient httpClient(@Qualifier("nettyClientOptions") Consumer<? super HttpClientOptions.Builder> options) {
            return HttpClient.create(options);
        }*/

        @Bean
        public Consumer<? super HttpClientOptions.Builder> nettyClientOptions() {
            return opts -> {

                opts.sslSupport(sslContextBuilder -> {
                    sslContextBuilder.trustManager(InsecureTrustManagerFactory.INSTANCE);
                    //sslContextBuilder.sslProvider(OPENSSL);
                });

                opts.poolResources(PoolResources.elastic("Netty-Upstream-Pool"));
            };
        }

        @Bean
        @ConditionalOnMissingBean
        public WebClient webClient(@Qualifier("nettyClientOptions") Consumer<? super HttpClientOptions.Builder> options) {
            return builder()
                    .clientConnector(new ReactorClientHttpConnector(options))
                    .build();
        }
    }
}
