package com.craigrueda.gateway.core;

import com.craigrueda.gateway.core.config.GatewayConfiguration;
import com.craigrueda.gateway.core.config.auto.GatewayAutoConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by Craig Rueda
 */
@SpringBootTest
@ContextConfiguration(classes = {ContextLoadsTest.ContextLoadsConfig.class, GatewayAutoConfiguration.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class ContextLoadsTest {
    @Test
    public void contextLoads() {
        // Makes sure that there's nothing too crazy in the configs
    }

    @Configuration
    public static class ContextLoadsConfig {
        @Bean
        public ServerProperties serverProperties() {
            return new ServerProperties();
        }

        @Bean
        public ResourceProperties resourceProperties() {
            return new ResourceProperties();
        }
    }
}
