package com.craigrueda.gateway.core.filter.pre;

import com.craigrueda.gateway.core.config.GatewayConfiguration;
import com.craigrueda.gateway.core.filter.BaseFilterTest;
import com.craigrueda.gateway.core.filter.GatewayFilter;
import org.junit.Test;
import org.springframework.http.HttpHeaders;

import java.util.function.Supplier;

import static com.craigrueda.gateway.core.filter.GatewayFilterType.PRE;
import static com.google.common.collect.Lists.newArrayList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Craig Rueda
 */
public class ForwardedForPreFilterTest extends BaseFilterTest {
    public ForwardedForPreFilterTest() {
        super(60, PRE);
    }

    @Test
    public void testDoFilter() {
        HttpHeaders upstreamReqHeaders = new HttpHeaders();
        context.setUpstreamRequestHeaders(upstreamReqHeaders);

        filter.doFilter(context);

        assertEquals(newArrayList("0.0.0.0"), upstreamReqHeaders.get("X-Forwarded-For"));
    }

    @Override
    public void testShouldFilter() {
        assertFalse(filter.shouldFilter(context));

        GatewayConfiguration gatewayConfiguration = new GatewayConfiguration();
        gatewayConfiguration.setAddForwardForHeader(true);
        filter = new ForwardedForPreFilter(gatewayConfiguration);
        assertTrue(filter.shouldFilter(context));
    }

    @Override
    protected Supplier<GatewayFilter> doBuildFilter() {
        return () -> new ForwardedForPreFilter(new GatewayConfiguration());
    }
}
