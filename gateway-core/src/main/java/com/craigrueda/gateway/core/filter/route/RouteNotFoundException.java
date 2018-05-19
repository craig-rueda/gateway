package com.craigrueda.gateway.core.filter.route;

import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.NOT_FOUND;

/**
 * Created by Craig Rueda
 */
public class RouteNotFoundException extends ResponseStatusException {
    public RouteNotFoundException(String path) {
        super(NOT_FOUND, "Route matching path " + path + " not found");
    }
}
