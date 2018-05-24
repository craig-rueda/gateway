package com.craigrueda.gateway.core.filter.hop;

import com.craigrueda.gateway.core.filter.AbstractGatewayFilter;
import com.craigrueda.gateway.core.filter.DefaultGatewayFilterOrder;
import com.craigrueda.gateway.core.filter.ctx.FilteringContext;
import org.springframework.http.HttpHeaders;
import reactor.core.publisher.Mono;

import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static reactor.core.publisher.Mono.empty;

/**
 * Created by Craig Rueda
 *
 * Removes "Hop-By-Hop" headers as defined here:
 * https://tools.ietf.org/html/draft-ietf-httpbis-p1-messaging-14#section-7.1.3
 */
public abstract class AbstractHopByHopFilter extends AbstractGatewayFilter {
    public static final Set<String> HOP_BY_HOP_HEADERS = newHashSet(
            "connection",
            "keep-alive",
            "proxy-authorization",
            "proxy-authenticate",
            "te",
            "trailer",
            "transfer-encoding",
            "upgrade",
            "x-application-context"
    );

    protected AbstractHopByHopFilter(DefaultGatewayFilterOrder order) {
        super(order.getFilterType(), order.getOrder());
    }

    @Override
    public Mono<Void> doFilter(FilteringContext ctx) {
        HttpHeaders headersToFilter = getHeadersToFilter(ctx);
        if (headersToFilter != null) {
            Set<String> headerNames = newHashSet(headersToFilter.keySet());

            headerNames.forEach(key -> {
                key = key.toLowerCase();
                if (HOP_BY_HOP_HEADERS.contains(key)) {
                    headersToFilter.remove(key);
                }
            });
        }

        return empty();
    }

    protected abstract HttpHeaders getHeadersToFilter(FilteringContext ctx);
}
