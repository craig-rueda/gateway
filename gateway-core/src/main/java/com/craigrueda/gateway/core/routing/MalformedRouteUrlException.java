package com.craigrueda.gateway.core.routing;

import org.springframework.core.NestedRuntimeException;

/**
 * Created by Craig Rueda
 */
public class MalformedRouteUrlException extends NestedRuntimeException {
    public MalformedRouteUrlException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
