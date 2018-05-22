package com.craigrueda.gateway.core.handler;

import com.craigrueda.gateway.core.filter.AbstractGatewayFilter;
import com.craigrueda.gateway.core.filter.GatewayFilter;
import com.craigrueda.gateway.core.filter.GatewayFilterType;
import com.craigrueda.gateway.core.filter.ctx.DefaultFilteringContext;
import com.craigrueda.gateway.core.filter.ctx.FilteringContext;
import com.craigrueda.gateway.core.filter.ctx.FilteringContextFactory;
import com.craigrueda.gateway.core.handler.web.FilterAssemblingWebHandler;
import org.junit.Test;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.craigrueda.gateway.core.filter.GatewayFilterType.PRE;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.sort;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Craig Rueda
   */
public class FilterChainingWebHandlerTest {
    @Test
    public void testChaining() {
        class ExecOrderFilter extends AbstractGatewayFilter {
            public ExecOrderFilter(GatewayFilterType filterType, int order) {
                super(filterType, order);
            }

            @Override
            public Mono<Void> doFilter(FilteringContext ctx) {
                ((List<GatewayFilter>) ctx.getAttribute("execOrder")).add(this);
                return Mono.empty();
            }
        }

        GatewayFilter
                filter0 = new ExecOrderFilter(PRE, 0),
                filter1 = new ExecOrderFilter(PRE, 1),
                filter2 = new ExecOrderFilter(PRE, 1) {
                    @Override
                    public boolean shouldFilter(FilteringContext ctx) {
                        return false;
                    }
                };

        List<GatewayFilter> filters = newArrayList(filter0, filter1, filter2);

        sort(filters);

        Map<String, Object> attribs = new HashMap<>();
        List<GatewayFilter> execOrderList = newArrayList();
        attribs.put("execOrder", execOrderList);
        ServerWebExchange exchange = mock(ServerWebExchange.class);
        when(exchange.getAttributes()).thenReturn(attribs);
        FilterAssemblingWebHandler handler = new FilterAssemblingWebHandler(filters, new FilteringContextFactory() {
            @Override
            public FilteringContext buildContext(ServerWebExchange exchange) {
                return new DefaultFilteringContext(exchange);
            }
        });

        handler.handle(exchange).block();
        assertEquals(2, execOrderList.size());
        assertSame(filter0, execOrderList.get(0));
        assertSame(filter1, execOrderList.get(1));
    }
}
