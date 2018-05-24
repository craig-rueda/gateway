package com.craigrueda.gateway.core.filter.pre;

import com.craigrueda.gateway.core.filter.ctx.FilteringContext;
import com.craigrueda.gateway.core.filter.hop.AbstractHopByHopFilter;
import org.springframework.http.HttpHeaders;

import static com.craigrueda.gateway.core.filter.DefaultGatewayFilterOrder.HopByHopPreFilter;

/**
 * Created by Craig Rueda
 */
public class HopByHopPreFilter extends AbstractHopByHopFilter {
    public HopByHopPreFilter() {
        super(HopByHopPreFilter);
    }

    @Override
    protected HttpHeaders getHeadersToFilter(FilteringContext ctx) {
        return ctx.getUpstreamRequestHeaders();
    }
}
