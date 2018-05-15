package com.craigrueda.gateway.core.handler;

import com.craigrueda.gateway.core.filter.GatewayFilter;
import com.craigrueda.gateway.core.filter.ctx.FilteringContext;
import com.craigrueda.gateway.core.filter.ctx.FilteringContextImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebHandler;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.craigrueda.gateway.core.filter.GatewayFilterType.ERROR;
import static java.util.stream.Collectors.toList;
import static reactor.core.publisher.Mono.defer;
import static reactor.core.publisher.Mono.empty;

/**
 * Created by Craig Rueda
   */
@Slf4j
public class FilterAssemblingWebHandler implements WebHandler {
    private final List<GatewayFilter> happyPathFilters, errorFilters;
    private final AtomicInteger concurrencyLevel = new AtomicInteger();

    public FilterAssemblingWebHandler(List<GatewayFilter> filters) {
        this.happyPathFilters = filters.stream().filter(f -> f.getFilterType() != ERROR).collect(toList());
        this.errorFilters = filters.stream().filter(f -> f.getFilterType() == ERROR).collect(toList());

        doLogConfig();
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange) {
        FilteringContext ctx = createFilteringContext(exchange);
        Mono<Void> ret = defer(() -> {
            if (log.isTraceEnabled()) {
                log.trace("Starting request {}, concurrency level {}",
                    ctx.getRequestNum(), concurrencyLevel.incrementAndGet());
            }

            return empty();
        });

        ret = constructFilterAssembly(ret, happyPathFilters, ctx);

        return ret.doFinally(signalType -> {
            if (log.isTraceEnabled()) {
                log.trace("Finished request {}, concurrency level {} with signal {}",
                    ctx.getRequestNum(), concurrencyLevel.decrementAndGet(), signalType);
            }
        }).onErrorResume((throwable -> {
            ctx.setError(throwable);
            return constructFilterAssembly(empty(), errorFilters, ctx);
        }))
          .publishOn(Schedulers.parallel())
          .subscribeOn(Schedulers.parallel())
          .map(obj -> null);
    }

    protected FilteringContext createFilteringContext(ServerWebExchange exchange) {
        return new FilteringContextImpl(exchange);
    }

    protected Mono<Void> constructFilterAssembly(Mono<Void> baseAssembly, List<GatewayFilter> filters, FilteringContext ctx) {
        for (GatewayFilter f : filters) {
            baseAssembly = baseAssembly.then(defer(() ->
                f.shouldFilter(ctx.getExchange()) ? f.doFilter(ctx) : empty()
            ));
        }

        return baseAssembly;
    }

    protected void doLogConfig() {
        StringBuilder happyBuilder = new StringBuilder(),
                errorBuilder = new StringBuilder();

        happyPathFilters.forEach(f -> happyBuilder.append("\n  - ").append(f.getClass().getSimpleName()).append(" (").append(f.getOrder()).append(")"));
        errorFilters.forEach(f -> errorBuilder.append("\n  - ").append(f.getClass().getSimpleName()).append(" (").append(f.getOrder()).append(")"));

        log.info("\nInitialized {} Filter Configuration:\n" +
                "Filters:{}\n\n" +
                "Error Filters:{}\n", getClass().getSimpleName(), happyBuilder, errorBuilder);
    }
}
