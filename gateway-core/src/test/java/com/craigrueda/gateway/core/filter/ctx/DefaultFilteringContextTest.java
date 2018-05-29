package com.craigrueda.gateway.core.filter.ctx;

import com.craigrueda.gateway.core.routing.Route;
import org.junit.Test;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
 * Created by Craig Rueda
 */
public class DefaultFilteringContextTest {
    @Test
    public void testContextGetSet() throws URISyntaxException {
        MockServerHttpRequest request = MockServerHttpRequest.get("http://test.com").build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);
        FilteringContext ctx = new DefaultFilteringContext(exchange);

        ctx.setExchange(exchange);
        assertSame(exchange, ctx.getExchange());

        assertNull(ctx.getAttribute("test"));
        ctx.setAttribute("test");
        assertTrue(ctx.getAttribute("test"));

        assertNull(ctx.getAttribute("testKey"));
        ctx.setAttribute("testKey", "testVal");
        assertEquals("testVal", ctx.getAttribute("testKey"));

        assertEquals(new URI("http://test.com"), ctx.getRequestUri());
        ctx.setRequestUri(new URI("http://test2.com"));
        assertEquals(new URI("http://test2.com"), ctx.getRequestUri());

        assertFalse(ctx.getShouldSendResponse());
        ctx.setShouldSendResponse(true);
        assertTrue(ctx.getShouldSendResponse());

        assertNull(ctx.getUpstreamResponse());
        ClientResponse clientResponse = mock(ClientResponse.class);
        ctx.setUpstreamResponse(clientResponse);
        assertSame(clientResponse, ctx.getUpstreamResponse());

        assertNull(ctx.getUpstreamRequestHeaders());
        HttpHeaders httpHeaders = new HttpHeaders();
        ctx.setUpstreamRequestHeaders(httpHeaders);
        assertSame(httpHeaders, ctx.getUpstreamRequestHeaders());

        assertNull(ctx.getClientResponseHeaders());
        httpHeaders = new HttpHeaders();
        ctx.setClientResponseHeaders(httpHeaders);
        assertSame(httpHeaders, ctx.getClientResponseHeaders());

        assertNull(ctx.getUpstreamRequestRoute());
        Route route = new Route();
        ctx.setUpstreamRequestRoute(route);
        assertSame(route, ctx.getUpstreamRequestRoute());

        assertFalse(ctx.isAlreadyRouted());
        ctx.setAlreadyRouted(true);
        assertTrue(ctx.isAlreadyRouted());

        assertTrue(System.nanoTime() - ctx.getStartTimeNs() > 0);

        assertNull(ctx.getResponseStatus());
        HttpStatus status = HttpStatus.NOT_FOUND;
        ctx.setResponseStatus(status);
        assertSame(status, ctx.getResponseStatus());

        assertNull(ctx.getError());
        Throwable error = new RuntimeException();
        ctx.setError(error);
        assertSame(error, ctx.getError());

        assertNull(ctx.getUpstreamQueryParams());
        MultiValueMap<String, String> queryParams = new HttpHeaders();
        ctx.setUpstreamQueryParams(queryParams);
        assertSame(queryParams, ctx.getUpstreamQueryParams());

        assertNull(ctx.getUpstreamResponseBody());
        Flux<DataBuffer> body = Flux.empty();
        ctx.setUpstreamResponseBody(body);
        assertSame(body, ctx.getUpstreamResponseBody());
    }
}
