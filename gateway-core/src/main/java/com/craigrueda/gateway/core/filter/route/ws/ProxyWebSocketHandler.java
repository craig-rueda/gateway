package com.craigrueda.gateway.core.filter.route.ws;

import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;

import static java.util.Collections.emptyList;

/**
 * Created by Craig Rueda
 *
 * Note - Original source: https://github.com/spring-cloud/spring-cloud-gateway
 */
public class ProxyWebSocketHandler implements WebSocketHandler {
    private final WebSocketClient client;
    private final URI url;
    private final HttpHeaders headers;
    private final List<String> subProtocols;

    public ProxyWebSocketHandler(URI url, WebSocketClient client, HttpHeaders headers, List<String> protocols) {
        this.client = client;
        this.url = url;
        this.headers = headers;
        this.subProtocols = protocols == null ? emptyList() : protocols;
    }

    @Override
    public List<String> getSubProtocols() {
        return this.subProtocols;
    }

    @Override
    public Mono<Void> handle(WebSocketSession downstreamSession /* This session is connected to the client */) {
        // pass headers along so custom headers can be sent through
        return client.execute(url, this.headers, new WebSocketHandler() {
            @Override
            public Mono<Void> handle(WebSocketSession upstreamSession /* This session is connected to the backend service */) {
                /**
                 * Basically just hook each pipe to its compliment, passing one flux to the next's send()
                 */
                Mono<Void> proxySessionSend = upstreamSession
                        .send(downstreamSession.receive().doOnNext(WebSocketMessage::retain));
                Mono<Void> serverSessionSend = downstreamSession
                        .send(upstreamSession.receive().doOnNext(WebSocketMessage::retain));

                return Mono.when(proxySessionSend, serverSessionSend);
            }

            /**
             * Copy subProtocols so they are available upstream.
             * @return
             */
            @Override
            public List<String> getSubProtocols() {
                return ProxyWebSocketHandler.this.subProtocols;
            }
        });
    }
}
