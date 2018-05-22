package com.craigrueda.gateway.core.filter.pre;

import com.craigrueda.gateway.core.config.GatewayConfiguration;
import com.craigrueda.gateway.core.filter.BaseFilterTest;
import com.craigrueda.gateway.core.filter.GatewayFilter;
import com.craigrueda.gateway.core.filter.route.RouteNotFoundException;
import com.craigrueda.gateway.core.routing.HeaderFilter;
import com.craigrueda.gateway.core.routing.Route;
import com.craigrueda.gateway.core.routing.RouteResolver;
import org.junit.Test;
import org.springframework.http.HttpHeaders;

import java.util.function.Supplier;

import static com.craigrueda.gateway.core.filter.GatewayFilterType.PRE;
import static com.google.common.collect.Lists.newArrayList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Craig Rueda
 */
public class RouteMappingPreFilterTest extends BaseFilterTest {
    private RouteResolver routeResolver;
    private HeaderFilter headerFilter;

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

    @Override
    protected Supplier<GatewayFilter> doBuildFilter() {
        return () -> {
            routeResolver = mock(RouteResolver.class);
            headerFilter = mock(HeaderFilter.class);
            return new RouteMappingPreFilter(routeResolver, new GatewayConfiguration(), headerFilter);
        };
    }
}
