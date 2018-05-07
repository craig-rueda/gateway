package com.craigrueda.gateway.core.filter;

import java.util.List;
import java.util.function.Consumer;

/**
 * Created by Craig Rueda
   */
public interface GatewayFilterSource {
    List<GatewayFilter> getMergedFilters();
    void registerSourceUpdatedCallback(Consumer<GatewayFilterSource> cb);
}
