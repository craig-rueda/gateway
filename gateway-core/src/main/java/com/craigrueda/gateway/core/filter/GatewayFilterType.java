package com.craigrueda.gateway.core.filter;

/**
 * Created by Craig Rueda
   */
public enum GatewayFilterType {
    PRE(1), ROUTE(2), POST(3), ERROR(4), RESPONSE(5);

    private int filterOrdinal;

    GatewayFilterType(int filterOrdinal) {
        this.filterOrdinal = filterOrdinal;
    }

    public int getFilterOrdinal() {
        return filterOrdinal;
    }
}
