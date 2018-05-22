package com.craigrueda.gateway.core.filter.response;

import com.craigrueda.gateway.core.filter.BaseFilterTest;
import com.craigrueda.gateway.core.filter.GatewayFilter;
import org.junit.Test;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpResponse;
import reactor.core.publisher.Flux;

import java.util.function.Supplier;

import static com.craigrueda.gateway.core.filter.GatewayFilterType.RESPONSE;
import static com.google.common.collect.Lists.newArrayList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Craig Rueda
 */
public class WriteResponseFilterTest extends BaseFilterTest {
    public WriteResponseFilterTest() {
        super(50, RESPONSE);
    }

    @Override
    public void testShouldFilter() {
        assertFalse(filter.shouldFilter(context));
        context.setShouldSendResponse(true);
        assertTrue(filter.shouldFilter(context));
    }

    @Test
    public void testDoFilter() {
        DefaultDataBufferFactory dbFactory = new DefaultDataBufferFactory();
        context.setResponseStatus(HttpStatus.I_AM_A_TEAPOT);
        context.setClientResponseHeaders(new HttpHeaders(){{add("test", "val1");}});
        context.setUpstreamResponseBody(Flux.just(dbFactory.wrap("test".getBytes())));

        filter.doFilter(context).block();

        MockServerHttpResponse response = (MockServerHttpResponse) context.getExchange().getResponse();
        assertEquals(HttpStatus.I_AM_A_TEAPOT, response.getStatusCode());
        assertEquals("test", response.getBodyAsString().block());
        assertEquals(newArrayList("val1"), response.getHeaders().get("test"));
    }

    @Override
    protected Supplier<GatewayFilter> doBuildFilter() {
        return WriteResponseFilter::new;
    }
}
