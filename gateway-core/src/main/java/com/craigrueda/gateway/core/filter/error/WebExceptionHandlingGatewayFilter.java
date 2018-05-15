package com.craigrueda.gateway.core.filter.error;

import com.craigrueda.gateway.core.filter.AbstractGatewayFilter;
import com.craigrueda.gateway.core.filter.ctx.FilteringContext;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

import static com.craigrueda.gateway.core.filter.GatewayFilterType.ERROR;

/**
 * Created by Craig Rueda
 */
public class WebExceptionHandlingGatewayFilter extends AbstractGatewayFilter {
    private final WebExceptionHandler webExceptionHandler;

    public WebExceptionHandlingGatewayFilter(WebExceptionHandler webExceptionHandler) {
        super(ERROR, Integer.MAX_VALUE);
        this.webExceptionHandler = webExceptionHandler;
    }

    @Override
    public Mono<Void> doFilter(FilteringContext ctx) {
        return webExceptionHandler.handle(ctx.getExchange(), ctx.getError());
    }
}
