package com.craigrueda.gateway.core;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import static reactor.core.publisher.Mono.just;

/**
 * Created by Craig Rueda
   */
@RestController
public class TestController {
    @GetMapping("/hello")
    public Mono<String> hello() {
        return just("hello");
    }
}
