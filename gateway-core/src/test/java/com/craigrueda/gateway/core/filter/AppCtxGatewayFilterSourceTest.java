package com.craigrueda.gateway.core.filter;

import com.craigrueda.gateway.core.filter.ctx.FilteringContext;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.craigrueda.gateway.core.filter.GatewayFilterType.POST;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Craig Rueda
 */
public class AppCtxGatewayFilterSourceTest {
    @Test
    public void testRefresh() {
        final AppCtxGatewayFilterSource filterSource = new AppCtxGatewayFilterSource();
        final AtomicBoolean callbackCalled = new AtomicBoolean(false);

        final ApplicationContext applicationContext = mock(ApplicationContext.class);
        when(applicationContext.getId()).thenReturn("ctx");
        when(applicationContext.getBeansOfType(GatewayFilter.class)).thenReturn(
                new HashMap<String, GatewayFilter>(){{put("filter1", new AbstractGatewayFilter(POST, 10) {
                    @Override
                    public Mono<Void> doFilter(FilteringContext ctx) {
                        return null;
                    }
                });}}
        );

        assertEquals(0, filterSource.getMergedFilters().size());

        filterSource.registerSourceUpdatedCallback(source -> callbackCalled.set(true));
        filterSource.handleContextRefresh(new ContextRefreshedEvent(applicationContext));

        assertTrue(callbackCalled.get());
        assertEquals(1, filterSource.getMergedFilters().size());

        // Test what happens when we call with the management ctx
        callbackCalled.set(false);
        when(applicationContext.getId()).thenReturn("management");
        filterSource.handleContextRefresh(new ContextRefreshedEvent(applicationContext));
        assertFalse(callbackCalled.get());
    }
}
