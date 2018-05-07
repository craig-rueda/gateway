package com.craigrueda.gateway.core.handler;

import com.craigrueda.gateway.core.filter.GatewayFilter;
import com.craigrueda.gateway.core.filter.ctx.FilteringContext;
import com.craigrueda.gateway.core.filter.ctx.FilteringContextImpl;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebHandler;
import reactor.core.publisher.Mono;

import java.util.List;

import static reactor.core.publisher.Mono.defer;
import static reactor.core.publisher.Mono.empty;

/**
 * Created by Craig Rueda
   */
public class FilterChainingWebHandler implements WebHandler {
    private List<GatewayFilter> filters;

    public FilterChainingWebHandler(List<GatewayFilter> filters) {
        this.filters = filters;
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange) {
        Mono<Void> ret = null;
        FilteringContext ctx = new FilteringContextImpl(exchange);

        for (GatewayFilter f : filters) {
            if (ret == null) {
                ret = defer(
                    () -> f.shouldFilter(exchange) ? f.doFilter(ctx) : empty()
                );
            }
            else {
                ret = ret.then(defer(
                    () -> f.shouldFilter(exchange) ? f.doFilter(ctx) : empty()
                ));
            }
        }

        return ret;
    }
}
