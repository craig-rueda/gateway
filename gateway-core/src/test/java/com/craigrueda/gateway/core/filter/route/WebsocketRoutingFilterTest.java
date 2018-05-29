package com.craigrueda.gateway.core.filter.route;

import com.craigrueda.gateway.core.filter.GatewayFilter;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import org.springframework.web.reactive.socket.server.WebSocketService;

import java.util.function.Supplier;

import static com.craigrueda.gateway.core.filter.GatewayFilterType.ROUTE;
import static org.mockito.Mockito.mock;

/**
 * Created by Craig Rueda
 */
public class WebsocketRoutingFilterTest extends BaseRoutingFilterTest {
    private WebSocketClient webSocketClient;
    private WebSocketService webSocketService;

    public WebsocketRoutingFilterTest() {
        super(51, ROUTE, "ws");
    }

    @Override
    protected Supplier<BaseRoutingFilter> doBuildFilter() {
        return () ->
            new WebsocketRoutingFilter(
                    webSocketClient = mock(WebSocketClient.class),
                    webSocketService = mock(WebSocketService.class)
            );
    }
}
