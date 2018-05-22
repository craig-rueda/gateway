package com.craigrueda.gateway.core.filter.post;

import com.craigrueda.gateway.core.filter.AbstractGatewayFilter;
import com.craigrueda.gateway.core.filter.ctx.FilteringContext;
import com.craigrueda.gateway.core.routing.HeaderFilter;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;

import static com.craigrueda.gateway.core.filter.DefaultGatewayFilterOrder.*;
import static org.springframework.web.reactive.function.BodyExtractors.toDataBuffers;
import static reactor.core.publisher.Mono.empty;

/**
 * Created by Craig Rueda
 */
public class UpstreamResponseHandlingPostFilter extends AbstractGatewayFilter {
    private final HeaderFilter headerFilter;

    public UpstreamResponseHandlingPostFilter(HeaderFilter headerFilter) {
        super(UpstreamResponseHandlingPostFilter.getFilterType(), UpstreamResponseHandlingPostFilter.getOrder());
        this.headerFilter = headerFilter;
    }

    @Override
    public Mono<Void> doFilter(FilteringContext ctx) {
        final ClientResponse upstreamResponse = ctx.getUpstreamResponse();
        if (upstreamResponse != null) {
            ctx.setClientResponseHeaders(
                    headerFilter.filterClientResponseHeaders(
                            upstreamResponse.headers().asHttpHeaders(),
                            ctx.getUpstreamRequestRoute()
                    )
            );
            ctx.setResponseStatus(upstreamResponse.statusCode());
            ctx.setUpstreamResponseBody(upstreamResponse.body(toDataBuffers()));
        }

        return empty();
    }
}
