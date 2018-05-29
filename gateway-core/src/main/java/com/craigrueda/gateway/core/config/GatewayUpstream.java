package com.craigrueda.gateway.core.config;

import io.netty.handler.ssl.SslProvider;
import lombok.Data;

import java.util.List;

/**
 * Created by Craig Rueda
 */
@Data
public class GatewayUpstream {
    private SSL ssl = new SSL();
    /**
     * When requesting a new connection from the underlying connection pool, how long should we wait in MS?
     * Note: only applies to bounded pools
     */
    private long connectionAquisitionTimeoutMs = 5000;
    /**
     * What do we name our connection pool?
     */
    private String upstreamConnectionPoolName = "Upstream-Pool";
    /**
     * The maximum number of connections that can be used by a single route. (Unlimited by default)
     */
    private int maxPerRouteConnections = -1;
    /**
     * The socket timeout in ms.
     */
    private int socketTimeoutMs = 10000;
    /**
     * The connection timeout in ms.
     */
    private int connectTimeoutMs = 2000;

    @Data
    public static class SSL {
        /**
         * False if we should disable any hostname verification when connecting to TLS-enabled upstreams
         */
        private boolean sslHostnameValidationEnabled = true;
        /**
         * Should we try and use OpenSSL? If so, lib-tcnative must be on the classpath, as well as OpenSSL/APR
         */
        private boolean useOpenSSL = false;
        /**
         * Enables OCSP stapling. Please note that not all {@link SslProvider} implementations support OCSP
         * stapling and an exception will be thrown.
         */
        private boolean enableOcsp = false;
        /**
         * Ciphers that will be accepted when establishing TLS sessions with upstreams {use null for defaults}
         */
        private List<String> acceptedCiphers = null;
        /**
         * The supported TLS protocols that we should accept
         */
        private String[] protocols = { "TLSv1", "TLSv1.1", "TLSv1.2" };
        /**
         * How much memory should be allocated for the storage of SSL sessions? {use 0 for default}
         */
        private long sessionCacheSize = 0;
        /**
         * How long should we cache SSL sessions for? {use 0 for default}
         */
        private long sessionTimeoutSec = 0;
    }
}
