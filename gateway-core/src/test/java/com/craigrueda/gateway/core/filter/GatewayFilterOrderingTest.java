package com.craigrueda.gateway.core.filter;

import com.craigrueda.gateway.core.filter.ctx.FilteringContext;
import org.junit.Test;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

import static com.craigrueda.gateway.core.filter.GatewayFilterType.*;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.sort;
import static org.junit.Assert.assertEquals;

/**
 * Created by Craig Rueda
   */
public class GatewayFilterOrderingTest {
    @Test
    public void testOrder() {
        List<GatewayFilter> filters = newArrayList(
            new NoOpFilter(PRE, 1),
            new NoOpFilter(PRE, 2),
            new NoOpFilter(PRE, 3),

            new NoOpFilter(ROUTE, 1),
            new NoOpFilter(ROUTE, 2),
            new NoOpFilter(ROUTE, 3),

            new NoOpFilter(ERROR, 1),
            new NoOpFilter(ERROR, 2),
            new NoOpFilter(ERROR, 3),

            new NoOpFilter(RESPONSE, 1),
            new NoOpFilter(RESPONSE, 2),
            new NoOpFilter(RESPONSE, 3),

            new NoOpFilter(POST, 1),
            new NoOpFilter(POST, 2),
            new NoOpFilter(POST, 3)
        );

        sort(filters);

        int groupOrdinal = 0, groupOrder = 0;
        GatewayFilterType currentType = GatewayFilterType.values()[groupOrdinal];
        for (GatewayFilter filter : filters) {
            if (groupOrder == 3) {
                groupOrder = 1;
                currentType = GatewayFilterType.values()[++groupOrdinal];
            }
            else {
                groupOrder++;
            }

            assertEquals(currentType, filter.getFilterType());
            assertEquals(groupOrder, filter.getOrder());
        }
    }

    class NoOpFilter extends AbstractGatewayFilter {
        public NoOpFilter(GatewayFilterType filterType, int order) {
            super(filterType, order);
        }

        @Override
        public Mono<Void> doFilter(FilteringContext ctx) {
            return null;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("NoOpFilter{");
            sb.append("FilterOrder=").append(getOrder());
            sb.append(" FilterType=").append(getFilterType());
            sb.append('}');
            return sb.toString();
        }
    }
}
