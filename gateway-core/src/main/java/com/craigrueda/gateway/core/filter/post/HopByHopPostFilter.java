package com.craigrueda.gateway.core.filter.post;

import com.craigrueda.gateway.core.filter.ctx.FilteringContext;
import com.craigrueda.gateway.core.filter.hop.AbstractHopByHopFilter;
import org.springframework.http.HttpHeaders;

import static com.craigrueda.gateway.core.filter.DefaultGatewayFilterOrder.HopByHopPostFilter;

/**
 * Created by Craig Rueda
 */
public class HopByHopPostFilter extends AbstractHopByHopFilter {
    public HopByHopPostFilter() {
        super(HopByHopPostFilter);
    }

    @Override
    protected HttpHeaders getHeadersToFilter(FilteringContext ctx) {
        return ctx.getClientResponseHeaders();
    }
}
