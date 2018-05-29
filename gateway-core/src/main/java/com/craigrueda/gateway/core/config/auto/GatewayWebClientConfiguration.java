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
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import org.springframework.web.reactive.socket.server.WebSocketService;
import org.springframework.web.reactive.socket.server.support.HandshakeWebSocketService;
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
public class GatewayWebClientConfiguration {
    @Bean
    public Consumer<? super HttpClientOptions.Builder> httpClientOptions(GatewayConfiguration gatewayConfiguration) {
        return doBuildClientOptions(gatewayConfiguration.getUpstreamHttp(), "http");
    }

    @Bean
    public Consumer<? super HttpClientOptions.Builder> wsClientOptions(GatewayConfiguration gatewayConfiguration) {
        return doBuildClientOptions(gatewayConfiguration.getUpstreamWs(), "websocket");
    }

    private Consumer<? super HttpClientOptions.Builder> doBuildClientOptions(
            final GatewayUpstream upstream, final String clientType) {
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
                    opts.preferNative(true);
                }
                sslContextBuilder.sslProvider(selectedProvider);
                log.info("Building {} client with SSL provider [{}] (OpenSSL available:{})",
                        clientType, selectedProvider, openSSLAvailable);
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
    public WebClient webClient(@Qualifier("httpClientOptions") Consumer<? super HttpClientOptions.Builder> options) {
        return builder()
                .clientConnector(new ReactorClientHttpConnector(options))
                .build();
    }

    @Bean
    @ConditionalOnMissingBean
    public WebSocketClient webSocketClient(@Qualifier("wsClientOptions") Consumer<? super HttpClientOptions.Builder> options) {
        return new ReactorNettyWebSocketClient(options);
    }

    @Bean
    @ConditionalOnMissingBean
    public WebSocketService webSocketService() {
        return new HandshakeWebSocketService();
    }
}
