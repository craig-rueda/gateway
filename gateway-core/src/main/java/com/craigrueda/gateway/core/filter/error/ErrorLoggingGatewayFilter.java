package com.craigrueda.gateway.core.filter.error;

import com.craigrueda.gateway.core.filter.AbstractGatewayFilter;
import com.craigrueda.gateway.core.filter.ctx.FilteringContext;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import static com.craigrueda.gateway.core.filter.GatewayFilterType.ERROR;
import static reactor.core.publisher.Mono.empty;

/**
 * Created by Craig Rueda
 */
@Slf4j
public class ErrorLoggingGatewayFilter extends AbstractGatewayFilter {
    public ErrorLoggingGatewayFilter() {
        super(ERROR, 1);
    }

    @Override
    public Mono<Void> doFilter(FilteringContext ctx) {
        log.error("Caught error while executing filter chain");

        return empty();
    }
}
