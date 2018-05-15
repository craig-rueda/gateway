package com.craigrueda.gateway.core.filter.pre;

import com.craigrueda.gateway.core.filter.AbstractGatewayFilter;
import com.craigrueda.gateway.core.filter.ctx.FilteringContext;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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
            ctx.setUpstreamRequestUrl(new URI("http://craig.craigrueda.com:8000/sample2.json"));
            HttpHeaders upstreamHeaders = new HttpHeaders();
            upstreamHeaders.addAll(ctx.getExchange().getRequest().getHeaders());
            //upstreamHeaders.set("Host", "httpbin.org");

            ctx.setUpstreamRequestHeaders(upstreamHeaders);
            ctx.setShouldSendResponse(true);

            log.debug("Mapping upstream request {}", ctx.getRequestNum());
        }
        catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return empty();
    }
}
