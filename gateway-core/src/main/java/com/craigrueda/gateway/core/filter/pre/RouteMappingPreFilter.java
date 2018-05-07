package com.craigrueda.gateway.core.filter.pre;

import com.craigrueda.gateway.core.filter.AbstractGatewayFilter;
import com.craigrueda.gateway.core.filter.ctx.FilteringContext;
import org.springframework.http.HttpHeaders;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.URISyntaxException;

import static com.craigrueda.gateway.core.filter.GatewayFilterType.PRE;
import static reactor.core.publisher.Mono.empty;

/**
 * Created by Craig Rueda
*/
public class RouteMappingPreFilter extends AbstractGatewayFilter {
    public RouteMappingPreFilter() {
        super(PRE, 5);
    }

    @Override
    public boolean shouldFilter(ServerWebExchange exchange) {
        return true;
    }

    @Override
    public Mono<Void> doFilter(FilteringContext ctx) {
        try {
            ctx.setUpstreamRequestUrl(new URI("http://httpbin.org/ip"));
            HttpHeaders upstreamHeaders = new HttpHeaders();
            upstreamHeaders.addAll(ctx.getExchange().getRequest().getHeaders());
            upstreamHeaders.set("Host", "httpbin.org");

            ctx.setUpstreamRequestHeaders(upstreamHeaders);

        }
        catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return empty();
    }
}
