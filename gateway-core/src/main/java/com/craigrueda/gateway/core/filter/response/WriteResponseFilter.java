package com.craigrueda.gateway.core.filter.response;

import com.craigrueda.gateway.core.filter.AbstractGatewayFilter;
import com.craigrueda.gateway.core.filter.ctx.FilteringContext;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;

import static com.craigrueda.gateway.core.filter.GatewayFilterType.RESPONSE;
import static reactor.core.publisher.Mono.defer;
import static reactor.core.publisher.Mono.empty;

/**
 * Created by Craig Rueda
   */
public class WriteResponseFilter extends AbstractGatewayFilter {
    public WriteResponseFilter() {
        super(RESPONSE, 100);
    }

    @Override
    public Mono<Void> doFilter(FilteringContext ctx) {
        ClientResponse upstreamResponse = ctx.getUpstreamResponse();
        if (upstreamResponse != null && ctx.shouldSendResponse()) {
            return defer(() -> {
                ServerHttpResponse response = ctx.getExchange().getResponse();
                return response.writeWith(upstreamResponse.body(BodyExtractors.toDataBuffers()));
            });
        }

        return empty();
    }
}
