package com.craigrueda.gateway.core.filter.route.ws;

import com.craigrueda.gateway.core.filter.ctx.FilteringContext;
import com.craigrueda.gateway.core.filter.route.BaseRoutingFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import org.springframework.web.reactive.socket.server.WebSocketService;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.craigrueda.gateway.core.filter.DefaultGatewayFilterOrder.WebsocketRoutingFilter;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static org.springframework.util.StringUtils.commaDelimitedListToStringArray;

/**
 * Created by Craig Rueda
 */
public class WebsocketRoutingFilter extends BaseRoutingFilter {
    private static final String SEC_WEBSOCKET_PROTOCOL = "sec-websocket-protocol";

    private final WebSocketClient webSocketClient;
    private final WebSocketService webSocketService;

    public WebsocketRoutingFilter( WebSocketClient webSocketClient, WebSocketService webSocketService) {
        super(WebsocketRoutingFilter.getFilterType(), WebsocketRoutingFilter.getOrder(), "ws");
        this.webSocketClient = webSocketClient;
        this.webSocketService = webSocketService;
    }

    @Override
    public Mono<Void> doHandleRequest(URI upstreamUri, FilteringContext ctx) {
        HttpHeaders headers = ctx.getUpstreamRequestHeaders();
        List<String> protocols = headers.get(SEC_WEBSOCKET_PROTOCOL);
        if (protocols != null) {
            protocols = protocols.stream()
                    .flatMap(header -> stream(commaDelimitedListToStringArray(header)))
                    .map(String::trim)
                    .collect(toList());
        }
        // This filter will handle all two-way comms, so there's no need for and RESPONSE filters...
        ctx.setShouldSendResponse(false);

        return this.webSocketService.handleRequest(
            ctx.getExchange(),
            new ProxyWebSocketHandler(upstreamUri, webSocketClient, headers, protocols)
        );
    }
}
