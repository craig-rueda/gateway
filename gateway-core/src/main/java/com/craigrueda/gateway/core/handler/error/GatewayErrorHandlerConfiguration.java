package com.craigrueda.gateway.core.handler.error;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.SearchStrategy;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.result.view.ViewResolver;

import java.util.Collections;
import java.util.List;

/**
 * Created by Craig Rueda
 */
@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
@ConditionalOnClass(WebFluxConfigurer.class)
public class GatewayErrorHandlerConfiguration {
    @Bean
    @ConditionalOnMissingBean(value = ErrorWebExceptionHandler.class, search = SearchStrategy.CURRENT)
    @Order(0)
    public GatewayWebExceptionHandler gatewayWebExceptionHandler(ErrorAttributes errorAttributes,
                                                                 ServerProperties serverProperties,
                                                                 ResourceProperties resourceProperties,
                                                                 ApplicationContext applicationContext,
                                                                 ObjectProvider<List<ViewResolver>> viewResolversProvider,
                                                                 ServerCodecConfigurer serverCodecConfigurer) {
        GatewayWebExceptionHandler exceptionHandler = new GatewayWebExceptionHandler(errorAttributes,
                resourceProperties, serverProperties.getError(), applicationContext);

        exceptionHandler.setViewResolvers(viewResolversProvider.getIfAvailable(Collections::emptyList));
        exceptionHandler.setMessageWriters(serverCodecConfigurer.getWriters());
        exceptionHandler.setMessageReaders(serverCodecConfigurer.getReaders());

        return exceptionHandler;
    }

    @Bean
    @ConditionalOnMissingBean(value = ErrorAttributes.class, search = SearchStrategy.CURRENT)
    @Order(0)
    public ErrorAttributes errorAttributes(ServerProperties serverProperties) {
        return new GatewayErrorAttributes(serverProperties.getError().isIncludeException());
    }
}
