package com.craigrueda.gateway.core.routing;

import com.craigrueda.gateway.core.config.GatewayRoute;
import org.junit.Test;

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
            new GatewayRoute("GET", "http://test.com", "/**", false)
        ));

        Route r = resolver.resolveRoute(null, null);
        assertEquals("http://test.com", r.getUpstreamUri());

        r = resolver.resolveRoute("/", null);
        assertEquals("http://test.com", r.getUpstreamUri());

        r = resolver.resolveRoute("//////", null);
        assertEquals("http://test.com", r.getUpstreamUri());

        // We only clean up the end of uris
        r = resolver.resolveRoute("///test///", null);
        assertEquals("http://test.com///test", r.getUpstreamUri());
    }

    @Test
    public void testMidString() throws URISyntaxException {
        DefaultRouteResolver resolver = new DefaultRouteResolver(newArrayList(
            new GatewayRoute("GET", "http://test.com", "/*/first", true),
            new GatewayRoute("GET", "http://test.com", "/*/second", false),
            new GatewayRoute("GET", "http://test.com", "/third/*/fourth", true)
        ));

        // Due to the way AntPathMatcher works, strip prefix doesn't strip when a mid-path wildcard is encountered
        Route r = resolver.resolveRoute("/part/first", "GET");
        assertEquals("http://test.com/part/first", r.getUpstreamUri());
        assertEquals("/*/first", r.getMatchedPath());
        assertEquals("GET", r.getVerb());

        r = resolver.resolveRoute("/part/second", "GET");
        assertEquals("http://test.com/part/second", r.getUpstreamUri());
        assertEquals("/*/second", r.getMatchedPath());
        assertEquals("GET", r.getVerb());

        r = resolver.resolveRoute("/third/part/fourth", "GET");
        assertEquals("http://test.com/part/fourth", r.getUpstreamUri());
        assertEquals("/third/*/fourth", r.getMatchedPath());
        assertEquals("GET", r.getVerb());
    }

    @Test
    public void testStripPrefix() throws URISyntaxException {
        DefaultRouteResolver resolver = new DefaultRouteResolver(newArrayList(
            new GatewayRoute("GET", "http://test.com", "/first/**", true),
            new GatewayRoute("GET", "http://test.com", "/second/**", false)
        ));

        Route r = resolver.resolveRoute("/first/part", "GET");
        assertEquals("http://test.com/part", r.getUpstreamUri());
        assertEquals("/first/**", r.getMatchedPath());
        assertEquals("GET", r.getVerb());

        r = resolver.resolveRoute("/second/part", "GET");
        assertEquals("http://test.com/second/part", r.getUpstreamUri());
        assertEquals("/second/**", r.getMatchedPath());
        assertEquals("GET", r.getVerb());

        r = resolver.resolveRoute("/", "GET");
        assertNull(r);
    }

    @Test
    public void testPreferenceOrder() throws URISyntaxException {
        DefaultRouteResolver resolver = new DefaultRouteResolver(newArrayList(
            new GatewayRoute("GET", "http://test.com", "/first/second/**", false),
            new GatewayRoute("GET", "http://test.com", "/first/**", false)
        ));

        Route r = resolver.resolveRoute("/first/part", "GET");
        assertEquals("/first/**", r.getMatchedPath());
        assertEquals("http://test.com/first/part", r.getUpstreamUri());

        r = resolver.resolveRoute("/first/second/", "GET");
        assertEquals("/first/second/**", r.getMatchedPath());
        assertEquals("http://test.com/first/second", r.getUpstreamUri());

        r = resolver.resolveRoute("/first/second/more", "GET");
        assertEquals("/first/second/**", r.getMatchedPath());
        assertEquals("http://test.com/first/second/more", r.getUpstreamUri());
    }
}
