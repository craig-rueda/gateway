package com.craigrueda.gateway.core.filter.error;

import com.craigrueda.gateway.core.filter.AbstractGatewayFilter;
import com.craigrueda.gateway.core.filter.ctx.FilteringContext;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

import static com.craigrueda.gateway.core.filter.DefaultGatewayFilterOrder.*;

/**
 * Created by Craig Rueda
 */
public class WebExceptionHandlingErrorFilter extends AbstractGatewayFilter {
    private final WebExceptionHandler webExceptionHandler;

    public WebExceptionHandlingErrorFilter(WebExceptionHandler webExceptionHandler) {
        super(WebExceptionHandlingErrorFilter.getFilterType(), WebExceptionHandlingErrorFilter.getOrder());
        this.webExceptionHandler = webExceptionHandler;
    }

    @Override
    public Mono<Void> doFilter(FilteringContext ctx) {
        return webExceptionHandler.handle(ctx.getExchange(), ctx.getError());
    }
}
