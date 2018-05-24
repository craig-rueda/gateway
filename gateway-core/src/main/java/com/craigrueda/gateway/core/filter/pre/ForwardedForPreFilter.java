package com.craigrueda.gateway.core.filter.pre;

import com.craigrueda.gateway.core.config.GatewayConfiguration;
import com.craigrueda.gateway.core.filter.AbstractGatewayFilter;
import com.craigrueda.gateway.core.filter.ctx.FilteringContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import reactor.core.publisher.Mono;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.List;

import static com.craigrueda.gateway.core.filter.DefaultGatewayFilterOrder.*;
import static com.google.common.collect.Lists.newArrayList;
import static java.lang.String.valueOf;
import static reactor.core.publisher.Mono.empty;

/**
 * Created by Craig Rueda
 *
 * Injects a Forwarded-For header to be forwarded to upstreams
*/
@Slf4j
public class ForwardedForPreFilter extends AbstractGatewayFilter {
    public static final String X_FORWARDED_FOR_HEADER = "X-Forwarded-For";
    public static final String X_FORWARDED_HOST_HEADER = "X-Forwarded-Host";
    public static final String X_FORWARDED_PORT_HEADER = "X-Forwarded-Port";
    public static final String X_FORWARDED_PROTO_HEADER = "X-Forwarded-Proto";

    public static final int HTTP_PORT = 80;
    public static final int HTTPS_PORT = 443;
    public static final String HTTP_SCHEME = "http";

    private final boolean addForwardedHeaders;

    public ForwardedForPreFilter(GatewayConfiguration gatewayConfiguration) {
        super(ForwardedForPreFilter.getFilterType(), ForwardedForPreFilter.getOrder());
        this.addForwardedHeaders = gatewayConfiguration.isAddForwardedHeaders();
    }

    @Override
    public boolean shouldFilter(FilteringContext ctx) {
        return addForwardedHeaders;
    }

    @Override
    public Mono<Void> doFilter(FilteringContext ctx) {
        ServerHttpRequest request = ctx.getExchange().getRequest();

        HttpHeaders upstreamHeaders = ctx.getUpstreamRequestHeaders();

        /**
         * X-Forwarded-For
         */
        InetSocketAddress remoteAddress = request.getRemoteAddress();
        if (remoteAddress != null) {
            StringBuilder sb = new StringBuilder(remoteAddress.getAddress().getHostAddress());
            if (remoteAddress.getPort() > 0) {
                sb.append(":").append(remoteAddress.getPort());
            }

            upstreamHeaders.putIfAbsent(
                X_FORWARDED_FOR_HEADER,
                newArrayList(sb.toString())
            );
        }

        /**
         * X-Forwarded-Host
         */
        injectForwardedForHostHeader(upstreamHeaders, request);

        URI requestUri = request.getURI();
        /**
         * X-Forwarded-Port
         */
        upstreamHeaders.putIfAbsent(
            X_FORWARDED_PORT_HEADER,
            newArrayList(
                valueOf(
                    determineRemotePort(requestUri)
                )
            )
        );

        /**
         * X-Forwarded-Proto
         */
        upstreamHeaders.putIfAbsent(
            X_FORWARDED_PROTO_HEADER,
            newArrayList(requestUri.getScheme())
        );

        return empty();
    }

    protected int determineRemotePort(URI requestUri) {
        if (requestUri.getPort() > 0) {
            return requestUri.getPort();
        }

        return HTTP_SCHEME.equals(requestUri.getScheme()) ? HTTP_PORT : HTTPS_PORT;
    }

    protected void injectForwardedForHostHeader(HttpHeaders upstreamHeaders, ServerHttpRequest request) {
        List<String> hostHeaderVals = request.getHeaders().get("host");
        if (hostHeaderVals != null && !hostHeaderVals.isEmpty()) {
            upstreamHeaders.putIfAbsent(
                X_FORWARDED_HOST_HEADER,
                newArrayList(hostHeaderVals.get(0))
            );
        }
    }
}
