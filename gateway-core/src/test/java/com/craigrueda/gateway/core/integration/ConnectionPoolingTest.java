package com.craigrueda.gateway.core.integration;

import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.ipc.netty.resources.PoolResources;

import java.net.URI;
import java.net.URISyntaxException;

import static org.springframework.web.reactive.function.client.WebClient.builder;

/**
 * Created by Craig Rueda
 */
@Slf4j
public class ConnectionPoolingTest {
    @Test
    public void testConnectionPooling() throws URISyntaxException, InterruptedException {
        WebClient webClient = builder()
                .clientConnector(new ReactorClientHttpConnector(opts -> {

                    opts.sslSupport(sslContextBuilder -> {
                        sslContextBuilder.trustManager(InsecureTrustManagerFactory.INSTANCE);
                        //sslContextBuilder.sslProvider(OPENSSL);
                    });

                    opts.poolResources(PoolResources.fixed("Netty-Upstream-Pool", 1, 100));
                }))
                .build();

        webClient.get().uri(new URI("http://httpbin.org/drip"))
                .exchange()
                .doOnSuccess(res -> log.info("Success on 1"))
                .doOnError(Throwable::printStackTrace)
                .subscribe();

        webClient.get().uri(new URI("http://192.168.99.100/drip"))
                .exchange()
                .doOnSuccess(res -> log.info("Success on 2"))
                .doOnError(Throwable::printStackTrace)
                .subscribe();

        Thread.sleep(10000);
    }
}
