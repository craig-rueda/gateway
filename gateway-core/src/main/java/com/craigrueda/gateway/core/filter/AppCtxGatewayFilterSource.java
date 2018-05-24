package com.craigrueda.gateway.core.filter;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static java.util.Collections.EMPTY_LIST;
import static java.util.Collections.sort;
import static java.util.Collections.unmodifiableList;

/**
 * Created by Craig Rueda
   */
public class AppCtxGatewayFilterSource implements GatewayFilterSource {
    private final List<Consumer<GatewayFilterSource>> callbacks = new ArrayList<>();
    private List<GatewayFilter> filters = EMPTY_LIST;

    @EventListener
    public void handleContextRefresh(ContextRefreshedEvent event) {
        ApplicationContext ctx = event.getApplicationContext();

        if (!ctx.getId().contains("management")) {
            Map<String, GatewayFilter> beans = ctx.getBeansOfType(GatewayFilter.class);

            if (beans != null && !beans.isEmpty()) {
                List<GatewayFilter> newFilters = new ArrayList<>(beans.values());
                sort(newFilters);
                filters = unmodifiableList(newFilters);

                callbacks.forEach(cb -> cb.accept(this));
            }
        }
    }

    @Override
    public List<GatewayFilter> getMergedFilters() {
        return filters;
    }

    @Override
    public void registerSourceUpdatedCallback(Consumer<GatewayFilterSource> cb) {
        callbacks.add(cb);
    }
}
