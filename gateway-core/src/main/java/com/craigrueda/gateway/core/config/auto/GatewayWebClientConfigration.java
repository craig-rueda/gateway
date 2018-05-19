package com.craigrueda.gateway.core.config.auto;

import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.ipc.netty.http.client.HttpClient;
import reactor.ipc.netty.http.client.HttpClientOptions;
import reactor.ipc.netty.resources.PoolResources;

import java.util.function.Consumer;

import static org.springframework.web.reactive.function.client.WebClient.builder;

/**
 * Created by Craig Rueda
 */
@Configuration
@ConditionalOnClass(HttpClient.class)
public class GatewayWebClientConfigration {
    @Bean
    @ConditionalOnMissingBean
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
