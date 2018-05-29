package com.craigrueda.gateway.core.filter.route;

import com.craigrueda.gateway.core.filter.BaseFilterTest;
import com.craigrueda.gateway.core.filter.GatewayFilter;
import com.craigrueda.gateway.core.routing.Route;
import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.craigrueda.gateway.core.filter.GatewayFilterType.ROUTE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.TRACE;

/**
 * Created by Craig Rueda
 */
public class WebClientRoutingFilterTest extends BaseRoutingFilterTest {
    private WebClient webClient;

    public WebClientRoutingFilterTest() {
        super(50, ROUTE, "http");
    }

    /**
     * Note: tests like this suck - too fragile/tightly coupled to the actual implementation...
     * Perhaps a refactor is in order? WebClient behavior could be
     * extracted to an adapter, perhaps?
     *
     * @throws URISyntaxException
     */
    @Test
    public void testDoFilter() throws URISyntaxException {
        Route route = new Route(new URI("http://test.com"), "/test", null);
        HttpHeaders queryParams = new HttpHeaders(){{add("test", "testVal");}};
        HttpHeaders requestHeaders = new HttpHeaders(){{add("Header1", "Val1");}};
        context.setUpstreamQueryParams(queryParams);
        context.setUpstreamRequestRoute(route);
        context.setUpstreamRequestHeaders(requestHeaders);

        WebClient.RequestBodyUriSpec bodyUriSpec = mock(WebClient.RequestBodyUriSpec.class);
        WebClient.RequestBodySpec requestBodySpec = mock(WebClient.RequestBodySpec.class);
        WebClient.RequestHeadersSpec requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
        ClientResponse response = mock(ClientResponse.class);
        Mono<ClientResponse> clientResponseMono = Mono.just(response);
        when(webClient.method(any())).thenAnswer(invocation -> {
            assertEquals(GET, invocation.getArgument(0));
            return bodyUriSpec;
        });
        when(bodyUriSpec.uri((URI) any())).thenAnswer(invocation -> {
            assertEquals(new URI("http://test.com?test=testVal"), invocation.getArgument(0));
            return requestBodySpec;
        });
        when(requestBodySpec.headers(any())).thenAnswer(invocation -> {
            Consumer<HttpHeaders> headersConsumer = invocation.getArgument(0);
            HttpHeaders newHeaders = new HttpHeaders();
            headersConsumer.accept(newHeaders);
            assertEquals(requestHeaders, newHeaders);
            return requestBodySpec;
        });
        when(requestBodySpec.body(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.exchange()).thenReturn(clientResponseMono);

        filter.doFilter(context).block();

        assertSame(response, context.getUpstreamResponse());
    }

    @Override
    protected Supplier<BaseRoutingFilter> doBuildFilter() {
        return () ->
            new WebClientRoutingFilter(webClient = mock(WebClient.class));
    }
}
