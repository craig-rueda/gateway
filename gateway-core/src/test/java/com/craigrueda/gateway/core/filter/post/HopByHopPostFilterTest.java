package com.craigrueda.gateway.core.filter.post;

import com.craigrueda.gateway.core.filter.BaseFilterTest;
import com.craigrueda.gateway.core.filter.GatewayFilter;
import org.junit.Test;
import org.springframework.http.HttpHeaders;

import java.util.function.Supplier;

import static com.craigrueda.gateway.core.filter.GatewayFilterType.POST;
import static com.google.common.collect.Lists.newArrayList;
import static org.junit.Assert.assertEquals;

/**
 * Created by Craig Rueda
 */
public class HopByHopPostFilterTest extends BaseFilterTest {
    public HopByHopPostFilterTest() {
        super(60, POST);
    }

    @Test
    public void testDoFilter() {
        HttpHeaders clientRespHeaders = new HttpHeaders(){{
            add("Connection", "val");
            add("Keep-Alive", "val");
            add("Proxy-Authenticate", "val");
            add("Proxy-Authorization", "val");
            add("TE", "val");
            add("Trailer", "val");
            add("Transfer-Encoding", "val");
            add("Upgrade", "val");

            add("Cool-Header", "cool.com");
        }};
        context.setClientResponseHeaders(clientRespHeaders);

        filter.doFilter(context);

        assertEquals(1, clientRespHeaders.size());
        assertEquals(newArrayList("cool.com"), clientRespHeaders.get("cool-header"));
    }

    @Override
    protected Supplier<GatewayFilter> doBuildFilter() {
        return HopByHopPostFilter::new;
    }
}
