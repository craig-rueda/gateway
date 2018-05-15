package com.craigrueda.gateway.core.filter.response;

import com.craigrueda.gateway.core.filter.AbstractGatewayFilter;
import com.craigrueda.gateway.core.filter.ctx.FilteringContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;

import static com.craigrueda.gateway.core.filter.GatewayFilterType.RESPONSE;
import static org.springframework.web.reactive.function.BodyExtractors.toDataBuffers;
import static reactor.core.publisher.Mono.defer;
import static reactor.core.publisher.Mono.empty;

/**
 * Created by Craig Rueda
   */
@Slf4j
public class WriteResponseFilter extends AbstractGatewayFilter {
    public WriteResponseFilter() {
        super(RESPONSE, 100_000);
    }

    @Override
    public Mono<Void> doFilter(FilteringContext ctx) {
        final ClientResponse upstreamResponse = ctx.getUpstreamResponse();
        if (upstreamResponse != null && ctx.getShouldSendResponse()) {
            ServerHttpResponse response = ctx.getExchange().getResponse();
            return response.writeWith(upstreamResponse.body(toDataBuffers()));
        }

        /*HttpClientResponse clientResponse = ctx.getUpstreamResponse();
        if (clientResponse != null && ctx.shouldSendResponse()) {
            ServerHttpResponse response = ctx.getExchange().getResponse();

            NettyDataBufferFactory factory = (NettyDataBufferFactory) response.bufferFactory();
            //TODO: what if it's not netty

            final Flux<NettyDataBuffer> body = clientResponse.receive()
                    .retain() //TODO: needed?
                    .map(factory::wrap);

            log.debug("Writing downstream response for request {}", ctx.getRequestNum());

            //MediaType contentType = response.getHeaders().getContentType();
            return response.writeWith(body);
        }*/

        return empty();
    }
}
