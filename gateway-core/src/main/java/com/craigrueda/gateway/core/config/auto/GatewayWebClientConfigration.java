package com.craigrueda.gateway.core.config.auto;

import com.craigrueda.gateway.core.config.GatewayConfiguration;
import com.craigrueda.gateway.core.config.GatewayUpstream;
import io.netty.handler.ssl.SslProvider;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.timeout.ReadTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
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

import static io.netty.channel.ChannelOption.CONNECT_TIMEOUT_MILLIS;
import static io.netty.handler.ssl.OpenSsl.ensureAvailability;
import static io.netty.handler.ssl.OpenSsl.isAvailable;
import static io.netty.handler.ssl.SslProvider.JDK;
import static io.netty.handler.ssl.SslProvider.OPENSSL;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.springframework.web.reactive.function.client.WebClient.builder;
import static reactor.ipc.netty.resources.PoolResources.elastic;
import static reactor.ipc.netty.resources.PoolResources.fixed;

/**
 * Created by Craig Rueda
 */
@Configuration
@ConditionalOnClass(HttpClient.class)
@Slf4j
public class GatewayWebClientConfigration {
    @Bean
    @ConditionalOnMissingBean
    public Consumer<? super HttpClientOptions.Builder> nettyClientOptions(GatewayConfiguration gatewayConfiguration) {
        final GatewayUpstream upstream = gatewayConfiguration.getUpstream();
        final GatewayUpstream.SSL ssl = upstream.getSsl();
        final boolean openSSLAvailable = isAvailable();

        return opts -> {
            opts.sslSupport(sslContextBuilder -> {
                if (!ssl.isSslHostnameValidationEnabled()) {
                    sslContextBuilder.trustManager(InsecureTrustManagerFactory.INSTANCE);
                }

                SslProvider selectedProvider = JDK;
                if (ssl.isUseOpenSSL()) {
                    ensureAvailability();
                    selectedProvider = OPENSSL;
                }
                sslContextBuilder.sslProvider(selectedProvider);
                log.info("Building client with SSL provider [{}] (OpenSSL available:{})",
                        selectedProvider, openSSLAvailable);
                sslContextBuilder.ciphers(ssl.getAcceptedCiphers());
                sslContextBuilder.enableOcsp(ssl.isEnableOcsp());
                sslContextBuilder.protocols(ssl.getProtocols());
                sslContextBuilder.sessionCacheSize(ssl.getSessionCacheSize());
                sslContextBuilder.sessionTimeout(ssl.getSessionTimeoutSec());
            });

            PoolResources poolResources;
            if (upstream.getMaxPerRouteConnections() > 0) {
                poolResources = fixed(
                    upstream.getUpstreamConnectionPoolName(),
                    upstream.getMaxPerRouteConnections(),
                    upstream.getConnectionAquisitionTimeoutMs()
                );
            }
            else {
                // Just assume there's no limit...
                poolResources = elastic(
                    upstream.getUpstreamConnectionPoolName()
                );
            }
            opts.poolResources(poolResources);
            opts.option(CONNECT_TIMEOUT_MILLIS, upstream.getConnectTimeoutMs());
            opts.afterChannelInit(channel ->
                    channel.pipeline()
                            .addFirst(
                                    "ReadTimeout Handler",
                                    new ReadTimeoutHandler(upstream.getSocketTimeoutMs(), MILLISECONDS)
                            )
            );
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
