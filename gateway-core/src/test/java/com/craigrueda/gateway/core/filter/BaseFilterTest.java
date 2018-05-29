package com.craigrueda.gateway.core.filter;

import com.craigrueda.gateway.core.filter.ctx.DefaultFilteringContext;
import com.craigrueda.gateway.core.filter.ctx.FilteringContext;
import com.craigrueda.gateway.core.filter.route.BaseRoutingFilter;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;

import java.net.InetSocketAddress;
import java.net.URISyntaxException;
import java.util.function.Supplier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.mock.http.server.reactive.MockServerHttpRequest.get;
import static org.springframework.mock.web.server.MockServerWebExchange.from;

/**
 * Created by Craig Rueda
 */
public abstract class BaseFilterTest<T extends AbstractGatewayFilter> {
    protected FilteringContext context;
    protected T filter;
    private final int expectedOrder;
    private final GatewayFilterType expectedType;

    protected BaseFilterTest(int expectedOrder, GatewayFilterType expectedType) {
        this.expectedOrder = expectedOrder;
        this.expectedType = expectedType;
    }

    @Before
    public void beforeBase() {
        MockServerHttpRequest request =
                get("http://test.com")
                .headers(new HttpHeaders(){{add("Host", "test.com");}})
                .remoteAddress(new InetSocketAddress(80))
                .build();
        ServerWebExchange exchange = from(request);
        context = new DefaultFilteringContext(exchange);
        filter = doBuildFilter().get();
    }

    @Test
    public void testOrder() {
        assertEquals(expectedOrder, filter.getOrder());
    }

    @Test
    public void testType() {
        assertEquals(expectedType, filter.getFilterType());
    }

    @Test
    public void testShouldFilter() throws Exception {
        assertTrue(filter.shouldFilter(context));
    }

    protected abstract Supplier<T> doBuildFilter();
}
