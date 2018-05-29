package com.craigrueda.gateway.core.filter.route;

import com.craigrueda.gateway.core.filter.BaseFilterTest;
import com.craigrueda.gateway.core.filter.GatewayFilterType;
import com.craigrueda.gateway.core.routing.Route;
import org.junit.Test;
import org.springframework.http.HttpHeaders;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Craig Rueda
 */
public abstract class BaseRoutingFilterTest extends BaseFilterTest<BaseRoutingFilter> {
    private final String handledScheme;

    protected BaseRoutingFilterTest(int expectedOrder, GatewayFilterType expectedType, String handledScheme) {
        super(expectedOrder, expectedType);
        this.handledScheme = handledScheme;
    }

    @Override
    public void testShouldFilter() throws URISyntaxException {
        context.setRequestUri(new URI(handledScheme + "://test.com"));
        assertFalse(filter.shouldFilter(context));
        context.setAlreadyRouted(true);
        assertFalse(filter.shouldFilter(context));
        context.setShouldSendResponse(true);
        assertFalse(filter.shouldFilter(context));
        context.setAlreadyRouted(false);
        assertTrue(filter.shouldFilter(context));
        context.setRequestUri(new URI( "test://test.com"));
        assertFalse(filter.shouldFilter(context));
    }

    @Test
    public void testUriBuilding() throws URISyntaxException {
        Route route = new Route(new URI("http://test.com"), "/test", null);
        HttpHeaders queryParams = new HttpHeaders();
        context.setUpstreamQueryParams(queryParams);
        context.setUpstreamRequestRoute(route);

        URI uri = filter.buildRequestUri(context);
        assertEquals(new URI("http://test.com"), uri);

        route.setUpstreamUri(new URI("http://test.com/test"));
        queryParams.add("test", "testVal");
        uri = filter.buildRequestUri(context);
        assertEquals(new URI("http://test.com/test?test=testVal"), uri);

        queryParams.clear();
        queryParams.add("test", "testVal ");
        uri = filter.buildRequestUri(context);
        assertEquals(new URI("http://test.com/test?test=testVal+"), uri);

        queryParams.clear();
        queryParams.add("test", "testVal+ ");
        uri = filter.buildRequestUri(context);
        assertEquals(new URI("http://test.com/test?test=testVal%2B+"), uri);

        queryParams.add("test2", "testVal2");
        uri = filter.buildRequestUri(context);
        assertEquals(new URI("http://test.com/test?test=testVal%2B+&test2=testVal2"), uri);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullQueryParams() {
        filter.buildRequestUri(context);
    }
}
