package com.craigrueda.gateway.core.filter.error;

import com.craigrueda.gateway.core.filter.BaseFilterTest;
import com.craigrueda.gateway.core.filter.GatewayFilter;
import org.junit.Test;
import org.springframework.web.server.WebExceptionHandler;

import java.util.function.Supplier;

import static com.craigrueda.gateway.core.filter.GatewayFilterType.ERROR;
import static java.lang.Integer.MAX_VALUE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

/**
 * Created by Craig Rueda
 */
public class WebExceptionHandlingErrorFilterTest extends BaseFilterTest {
    private WebExceptionHandler webExceptionHandler;

    public WebExceptionHandlingErrorFilterTest() {
        super(MAX_VALUE, ERROR);
    }

    @Override
    protected Supplier<GatewayFilter> doBuildFilter() {
        return () -> {
            webExceptionHandler = mock(WebExceptionHandler.class);
            return new WebExceptionHandlingErrorFilter(webExceptionHandler);
        };
    }

    @Test
    public void testDoFilter() {
        verifyZeroInteractions(webExceptionHandler);

        filter.doFilter(context);

        verify(webExceptionHandler).handle(any(), any());
    }
}
