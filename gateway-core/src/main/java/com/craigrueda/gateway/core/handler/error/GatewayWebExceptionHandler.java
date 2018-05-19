package com.craigrueda.gateway.core.handler.error;

import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.DefaultErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;

/**
 * Created by Craig Rueda
 *
 * Gives a concrete class to reference in configuration rather than depending on the various instances
 * created in other framework configs...
 *
 */
public class GatewayWebExceptionHandler extends DefaultErrorWebExceptionHandler {
    /**
     * Create a new {@code GatewayWebExceptionHandler} instance.
     *
     * @param errorAttributes    the error attributes
     * @param resourceProperties the resources configuration properties
     * @param errorProperties    the error configuration properties
     * @param applicationContext the current application context
     */
    public GatewayWebExceptionHandler(ErrorAttributes errorAttributes, ResourceProperties resourceProperties,
                                      ErrorProperties errorProperties, ApplicationContext applicationContext) {
        super(errorAttributes, resourceProperties, errorProperties, applicationContext);
    }
}
