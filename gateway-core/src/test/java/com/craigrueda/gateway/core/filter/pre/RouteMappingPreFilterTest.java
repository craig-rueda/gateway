package com.craigrueda.gateway.core.filter.pre;

import com.craigrueda.gateway.core.config.GatewayConfiguration;
import com.craigrueda.gateway.core.filter.BaseFilterTest;
import com.craigrueda.gateway.core.filter.GatewayFilter;
import com.craigrueda.gateway.core.filter.route.RouteNotFoundException;
import com.craigrueda.gateway.core.routing.filter.HeaderFilter;
import com.craigrueda.gateway.core.routing.Route;
import com.craigrueda.gateway.core.routing.resolve.RouteResolver;
import org.junit.Test;
import org.springframework.http.HttpHeaders;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.function.Supplier;

import static com.craigrueda.gateway.core.filter.GatewayFilterType.PRE;
import static com.google.common.collect.Lists.newArrayList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.TRACE;

/**
 * Created by Craig Rueda
 */
public class RouteMappingPreFilterTest extends BaseFilterTest {
    private RouteResolver routeResolver;
    private HeaderFilter headerFilter;
    private RouteMappingPreFilter filter;

    public RouteMappingPreFilterTest() {
        super(50, PRE);
    }

    @Test(expected = RouteNotFoundException.class)
    public void testNoRouteFound() {
        filter.doFilter(context);
    }

    @Test
    public void testFoundRoute() {
        Route route = new Route();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Host", "test.com");

        when(routeResolver.resolveRoute(any())).thenReturn(route);
        when(headerFilter.filterUpstreamRequestHeaders(any(), any())).thenReturn(headers);

        filter.doFilter(context);
        assertSame(route, context.getUpstreamRequestRoute());
        assertNull(context.getUpstreamRequestHeaders().get("host"));
        assertTrue(context.getUpstreamQueryParams().isEmpty());
        assertTrue(context.getShouldSendResponse());
    }

    @Test
    public void testPreserveHostHeader() {
        Route route = new Route();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Host", "test.com");

        when(routeResolver.resolveRoute(any())).thenReturn(route);
        when(headerFilter.filterUpstreamRequestHeaders(any(), any())).thenReturn(headers);
        filter = new RouteMappingPreFilter(routeResolver, new GatewayConfiguration(){{setPreserveHostHeader(true);}}, headerFilter);

        filter.doFilter(context);
        assertEquals(newArrayList("test.com"), context.getUpstreamRequestHeaders().get("host"));
    }

    @Test
    public void testHasBody() {
        assertTrue(filter.hasBody(GET, new HttpHeaders(){{add("content-length", "0");}}));
        assertTrue(filter.hasBody(GET, new HttpHeaders(){{add("transfer-encoding", "0");}}));
        assertFalse(filter.hasBody(GET, new HttpHeaders()));
        assertFalse(filter.hasBody(TRACE, new HttpHeaders(){{add("content-length", "0");}}));
    }

    @Test
    public void testUpdateWebsocketScheme() throws URISyntaxException {
        context.setUpstreamRequestHeaders(new HttpHeaders(){{add("Upgrade", "Websocket");}});

        context.setRequestUri(new URI("http://test.com/test?test=testval"));
        filter.updateWebsocketScheme(context);
        assertEquals(new URI("ws://test.com/test?test=testval"), context.getRequestUri());

        context.setRequestUri(new URI("https://test.com/test?test=testval"));
        filter.updateWebsocketScheme(context);
        assertEquals(new URI("wss://test.com/test?test=testval"), context.getRequestUri());

        context.setRequestUri(new URI("wss://test.com/test?test=testval2"));
        filter.updateWebsocketScheme(context);
        assertEquals(new URI("wss://test.com/test?test=testval2"), context.getRequestUri());
    }

    @Override
    protected Supplier<GatewayFilter> doBuildFilter() {
        return () -> {
            routeResolver = mock(RouteResolver.class);
            headerFilter = mock(HeaderFilter.class);
            filter = new RouteMappingPreFilter(routeResolver, new GatewayConfiguration(), headerFilter);
            return filter;
        };
    }
}
