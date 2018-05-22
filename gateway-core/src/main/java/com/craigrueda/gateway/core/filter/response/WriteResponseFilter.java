package com.craigrueda.gateway.core.filter.response;

import com.craigrueda.gateway.core.filter.AbstractGatewayFilter;
import com.craigrueda.gateway.core.filter.ctx.FilteringContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpResponse;
import reactor.core.publisher.Mono;

import static com.craigrueda.gateway.core.filter.DefaultGatewayFilterOrder.*;
import static reactor.core.publisher.Mono.empty;

/**
 * Created by Craig Rueda
   */
@Slf4j
public class WriteResponseFilter extends AbstractGatewayFilter {
    public WriteResponseFilter() {
        super(WriteResponseFilter.getFilterType(), WriteResponseFilter.getOrder());
    }

    @Override
    public boolean shouldFilter(FilteringContext ctx) {
        return ctx.getShouldSendResponse();
    }

    @Override
    public Mono<Void> doFilter(FilteringContext ctx) {
        ServerHttpResponse response = ctx.getExchange().getResponse();

        response.setStatusCode(ctx.getResponseStatus());
        response.getHeaders().addAll(ctx.getClientResponseHeaders());

        return response.writeWith(ctx.getUpstreamResponseBody());
    }
}
