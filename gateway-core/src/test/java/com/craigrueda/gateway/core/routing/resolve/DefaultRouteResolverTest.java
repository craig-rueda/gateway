package com.craigrueda.gateway.core.routing.resolve;

import com.craigrueda.gateway.core.config.GatewayRoute;
import com.craigrueda.gateway.core.routing.Route;
import com.craigrueda.gateway.core.routing.resolve.DefaultRouteResolver;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by Craig Rueda
 */
public class DefaultRouteResolverTest {
    @Test
    public void testErroneous() throws URISyntaxException {
        DefaultRouteResolver resolver = new DefaultRouteResolver(newArrayList(
            new GatewayRoute("http://test.com", "/**", false)
        ));

        Route r = resolver.resolveRoute(null);
        assertEquals(new URI("http://test.com"), r.getUpstreamUri());

        r = resolver.resolveRoute("/");
        assertEquals(new URI("http://test.com"), r.getUpstreamUri());

        r = resolver.resolveRoute("//////");
        assertEquals(new URI("http://test.com"), r.getUpstreamUri());

        // We only clean up the end of uris
        r = resolver.resolveRoute("///test///");
        assertEquals(new URI("http://test.com///test"), r.getUpstreamUri());
    }

    @Test
    public void testMidString() throws URISyntaxException {
        DefaultRouteResolver resolver = new DefaultRouteResolver(newArrayList(
            new GatewayRoute("http://test.com", "/*/first", true),
            new GatewayRoute("http://test.com", "/*/second", false),
            new GatewayRoute("http://test.com", "/third/*/fourth", true)
        ));

        // Due to the way AntPathMatcher works, strip prefix doesn't strip when a mid-path wildcard is encountered
        Route r = resolver.resolveRoute("/part/first");
        assertEquals(new URI("http://test.com/part/first"), r.getUpstreamUri());
        assertEquals("/*/first", r.getMatchedPath());

        r = resolver.resolveRoute("/part/second");
        assertEquals(new URI("http://test.com/part/second"), r.getUpstreamUri());
        assertEquals("/*/second", r.getMatchedPath());

        r = resolver.resolveRoute("/third/part/fourth");
        assertEquals(new URI("http://test.com/part/fourth"), r.getUpstreamUri());
        assertEquals("/third/*/fourth", r.getMatchedPath());
    }

    @Test
    public void testStripPrefix() throws URISyntaxException {
        DefaultRouteResolver resolver = new DefaultRouteResolver(newArrayList(
            new GatewayRoute("http://test.com", "/first/**", true),
            new GatewayRoute("http://test.com", "/second/**", false)
        ));

        Route r = resolver.resolveRoute("/first/part");
        assertEquals(new URI("http://test.com/part"), r.getUpstreamUri());
        assertEquals("/first/**", r.getMatchedPath());

        r = resolver.resolveRoute("/second/part");
        assertEquals(new URI("http://test.com/second/part"), r.getUpstreamUri());
        assertEquals("/second/**", r.getMatchedPath());

        r = resolver.resolveRoute("/");
        assertNull(r);
    }

    @Test
    public void testPreferenceOrder() throws URISyntaxException {
        DefaultRouteResolver resolver = new DefaultRouteResolver(newArrayList(
            new GatewayRoute("http://test.com", "/first/second/**", false),
            new GatewayRoute("http://test.com", "/first/**", false)
        ));

        Route r = resolver.resolveRoute("/first/part");
        assertEquals("/first/**", r.getMatchedPath());
        assertEquals(new URI("http://test.com/first/part"), r.getUpstreamUri());

        r = resolver.resolveRoute("/first/second/");
        assertEquals("/first/second/**", r.getMatchedPath());
        assertEquals(new URI("http://test.com/first/second"), r.getUpstreamUri());

        r = resolver.resolveRoute("/first/second/more");
        assertEquals("/first/second/**", r.getMatchedPath());
        assertEquals(new URI("http://test.com/first/second/more"), r.getUpstreamUri());
    }
}
