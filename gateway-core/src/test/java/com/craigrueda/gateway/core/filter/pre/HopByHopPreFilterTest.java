package com.craigrueda.gateway.core.filter.pre;

import com.craigrueda.gateway.core.filter.BaseFilterTest;
import com.craigrueda.gateway.core.filter.GatewayFilter;
import org.junit.Test;
import org.springframework.http.HttpHeaders;

import java.util.function.Supplier;

import static com.craigrueda.gateway.core.filter.GatewayFilterType.PRE;
import static com.google.common.collect.Lists.newArrayList;
import static org.junit.Assert.assertEquals;

/**
 * Created by Craig Rueda
 */
public class HopByHopPreFilterTest extends BaseFilterTest {
    public HopByHopPreFilterTest() {
        super(70, PRE);
    }

    @Test
    public void testDoFilter() {
        HttpHeaders upstreamReqHeaders = new HttpHeaders(){{
            add("Connection", "val");
            add("Keep-Alive", "val");
            add("Proxy-Authenticate", "val");
            add("Proxy-Authorization", "val");
            add("TE", "val");
            add("Trailer", "val");
            add("Transfer-Encoding", "val");
            add("Upgrade", "val");

            add("Host", "cool.com");
        }};
        context.setUpstreamRequestHeaders(upstreamReqHeaders);

        filter.doFilter(context);

        assertEquals(1, upstreamReqHeaders.size());
        assertEquals(newArrayList("cool.com"), upstreamReqHeaders.get("host"));
    }

    @Override
    protected Supplier<GatewayFilter> doBuildFilter() {
        return HopByHopPreFilter::new;
    }
}
