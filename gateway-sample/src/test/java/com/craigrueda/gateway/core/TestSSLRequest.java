package com.craigrueda.gateway.core;

import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.ssl.SslProvider;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.NettyDataBuffer;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import reactor.core.publisher.Flux;
import reactor.ipc.netty.NettyPipeline;
import reactor.ipc.netty.http.client.HttpClient;
import reactor.ipc.netty.http.client.HttpClientRequest;
import reactor.ipc.netty.resources.PoolResources;

import static java.lang.Thread.sleep;
import static reactor.core.publisher.Flux.empty;

/**
 * Created by Craig Rueda
   */
public class TestSSLRequest {
    private static final Logger log = LoggerFactory.getLogger(TestSSLRequest.class);

    private HttpClient httpClient;

    @Before
    public void before() {
        httpClient = HttpClient.create(opts -> {
            opts.sslSupport(sslContextBuilder -> {
                sslContextBuilder.trustManager(InsecureTrustManagerFactory.INSTANCE);
                sslContextBuilder.sslProvider(SslProvider.OPENSSL);
            });

            opts.poolResources(PoolResources.elastic("http"));
        });
    }

    @Test
    public void doTest() throws InterruptedException {
        final HttpMethod method = HttpMethod.valueOf("GET");
        final String url = "https://localhost.anaplan.com:4434/dump.json";
        final DefaultHttpHeaders httpHeaders = new DefaultHttpHeaders();

        httpHeaders.set("Accept", "*/*");
        httpHeaders.set("Host", "https.org");

        httpClient.request(method, url, req -> {
            final HttpClientRequest proxyRequest = req.options(NettyPipeline.SendOptions::flushOnEach)
                    .headers(httpHeaders)
                    .chunkedTransfer(false)
                    .failOnServerError(false)
                    .failOnClientError(false);

            return proxyRequest.sendHeaders() //I shouldn't need this
                    .send(empty());
        }).doOnNext(res -> {
            //res.receiveContent().doOnNext(httpContent -> httpContent.content())

            log.info("Got response");
            /*NettyDataBufferFactory factory = (NettyDataBufferFactory) res.bufferFactory();
            final Flux<NettyDataBuffer> body = res.receive()
                    .retain() //TODO: needed?
                    .map(factory::wrap);*/


        }).block();

        httpClient.request(method, url, req -> {
            final HttpClientRequest proxyRequest = req.options(NettyPipeline.SendOptions::flushOnEach)
                    .headers(httpHeaders)
                    .chunkedTransfer(false)
                    .failOnServerError(false)
                    .failOnClientError(false);

            return proxyRequest.sendHeaders() //I shouldn't need this
                    .send(empty());
        }).doOnNext(res -> {
            log.info("Got response");
        }).block();
    }
}
