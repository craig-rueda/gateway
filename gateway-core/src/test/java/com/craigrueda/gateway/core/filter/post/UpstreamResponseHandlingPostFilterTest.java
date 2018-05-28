package com.craigrueda.gateway.core.filter.post;

import com.craigrueda.gateway.core.filter.BaseFilterTest;
import com.craigrueda.gateway.core.filter.GatewayFilter;
import com.craigrueda.gateway.core.routing.filter.HeaderFilter;
import org.junit.Test;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Flux;

import java.util.function.Supplier;

import static com.craigrueda.gateway.core.filter.GatewayFilterType.POST;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Craig Rueda
 */
public class UpstreamResponseHandlingPostFilterTest extends BaseFilterTest {
    private HeaderFilter headerFilter;

    public UpstreamResponseHandlingPostFilterTest() {
        super(50, POST);
    }

    @Test
    public void testDoFilter() {
        filter.doFilter(context);
        // Everything should be null, as there was no ClientResponse...
        assertNull(context.getClientResponseHeaders());
        assertNull(context.getResponseStatus());
        assertNull(context.getUpstreamResponseBody());

        HttpHeaders filteredHeaders = new HttpHeaders();
        when(headerFilter.filterClientResponseHeaders(any(), any())).thenReturn(filteredHeaders);

        ClientResponse response = mock(ClientResponse.class);
        HttpStatus status = HttpStatus.OK;
        Flux<DataBuffer> body = Flux.empty();
        when(response.headers()).thenReturn(mock(ClientResponse.Headers.class));
        when(response.headers().asHttpHeaders()).thenReturn(new HttpHeaders());
        when(response.statusCode()).thenReturn(status);
        when(response.body(any())).thenReturn(body);
        context.setUpstreamResponse(response);

        filter.doFilter(context);
        // This time, things should be set...
        assertSame(status, context.getResponseStatus());
        assertSame(body, context.getUpstreamResponseBody());
        assertSame(filteredHeaders, context.getClientResponseHeaders());
    }

    @Override
    protected Supplier<GatewayFilter> doBuildFilter() {
        return () -> {
            headerFilter = mock(HeaderFilter.class);
            return new UpstreamResponseHandlingPostFilter(headerFilter);
        };
    }
}
