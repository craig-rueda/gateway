package com.craigrueda.gateway.core.handler.web;

import com.craigrueda.gateway.core.filter.GatewayFilter;
import com.craigrueda.gateway.core.filter.ctx.FilteringContext;
import com.craigrueda.gateway.core.filter.ctx.FilteringContextFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebHandler;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.craigrueda.gateway.core.filter.GatewayFilterType.ERROR;
import static java.lang.String.format;
import static java.lang.System.nanoTime;
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
    private final FilteringContextFactory filteringContextFactory;

    public FilterAssemblingWebHandler(List<GatewayFilter> filters, FilteringContextFactory filteringContextFactory) {
        this.happyPathFilters = buildHappyPathFilters(filters);
        this.errorFilters = buildErrorFilters(filters);
        this.filteringContextFactory = filteringContextFactory;

        doLogConfig();
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange) {
        FilteringContext ctx = filteringContextFactory.buildContext(exchange);
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
                log.trace("Finished request {}, concurrency level {} with signal {} in {}ms",
                    ctx.getRequestNum(), concurrencyLevel.decrementAndGet(), signalType, (nanoTime() - ctx.getStartTimeNs()) / 1_000_000D);
            }
        }).onErrorResume((throwable -> {
            ctx.setError(throwable);
            return constructFilterAssembly(empty(), errorFilters, ctx);
        }))
          .publishOn(Schedulers.parallel())
          .subscribeOn(Schedulers.parallel())
          .map(obj -> null);
    }

    protected Mono<Void> constructFilterAssembly(Mono<Void> baseAssembly, List<GatewayFilter> filters, FilteringContext ctx) {
        for (GatewayFilter f : filters) {
            baseAssembly = baseAssembly.then(defer(() ->
                f.shouldFilter(ctx) ? f.doFilter(ctx) : empty()
            ));
        }

        return baseAssembly;
    }

    protected List<GatewayFilter> buildHappyPathFilters(List<GatewayFilter> allFilters) {
        return allFilters.stream().filter(f -> f.getFilterType() != ERROR).collect(toList());
    }

    protected List<GatewayFilter> buildErrorFilters(List<GatewayFilter> allFilters) {
        return allFilters.stream().filter(f -> f.getFilterType() == ERROR).collect(toList());
    }

    protected void doLogConfig() {
        StringBuilder happyBuilder = new StringBuilder(),
                errorBuilder = new StringBuilder();

        happyPathFilters.forEach(f -> happyBuilder.append(
                format("\n  - %s [%s:%d]", f.getClass().getSimpleName(), f.getFilterType(), f.getOrder())
            )
        );
        errorFilters.forEach(f -> errorBuilder.append(
                format("\n  - %s [%s:%d]", f.getClass().getSimpleName(), f.getFilterType(), f.getOrder())
                )
        );

        log.info("\nInitialized {} Filter Configuration:\n" +
                "Filters:{}\n\n" +
                "Error Filters:{}\n", getClass().getSimpleName(), happyBuilder, errorBuilder);
    }
}
