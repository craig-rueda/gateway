package com.craigrueda.gateway.core.filter.route;

import com.craigrueda.gateway.core.filter.ctx.FilteringContext;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import org.springframework.web.reactive.socket.server.WebSocketService;
import reactor.core.publisher.Mono;

import java.net.URI;

import static com.craigrueda.gateway.core.filter.DefaultGatewayFilterOrder.WebsocketRoutingFilter;

/**
 * Created by Craig Rueda
 */
public class WebsocketRoutingFilter extends BaseRoutingFilter {
    private final WebSocketClient webSocketClient;
    private final WebSocketService webSocketService;

    public WebsocketRoutingFilter( WebSocketClient webSocketClient, WebSocketService webSocketService) {
        super(WebsocketRoutingFilter.getFilterType(), WebsocketRoutingFilter.getOrder(), "ws");
        this.webSocketClient = webSocketClient;
        this.webSocketService = webSocketService;
    }

    @Override
    public Mono<Void> doHandleRequest(URI upstreamUri, FilteringContext ctx) {
        return Mono.empty();
    }
}
