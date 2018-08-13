package com.company.common.constants;

/**
 */
public class EnvironmentKeys {
    public EnvironmentKeys() {
        // Suppress default constructor for noninstantiability
        throw new UnsupportedOperationException();
    }

    // Redshift configuration environment keys
    public static final String REDSHIFT_CLUSTER_MINIMUM_IDLE = "REDSHIFT_CLUSTER_MINIMUM_IDLE";
    public static final String REDSHIFT_CLUSTER_MAXIMUM_CONNECTIONS = "REDSHIFT_CLUSTER_MAXIMUM_CONNECTIONS";
    public static final String REDSHIFT_CLUSTER_CONNECTION_TIMEOUT = "REDSHIFT_CLUSTER_CONNECTION_TIMEOUT";
    public static final String REDSHIFT_CLUSTER_IDLE_TIMEOUT = "REDSHIFT_CLUSTER_IDLE_TIMEOUT";
    public static final String REDSHIFT_PASSWORD = "REDSHIFT_PASSWORD";
    public static final String REDSHIFT_HOST = "REDSHIFT_HOST";
    public static final String REDSHIFT_USER = "REDSHIFT_USER";
    public static final String REDSHIFT_CLUSTER_CONNECTION_MAX_LIFETIME = "REDSHIFT_CLUSTER_CONNECTION_MAX_LIFETIME";
    public static final String REDSHIFT_PORT = "REDSHIFT_PORT";

    // MySQL configuration environment keys
    public static final String MYSQL_USER = "MYSQL_USER";
    public static final String MYSQL_PASSWORD = "MYSQL_PASSWORD";
    public static final String MYSQL_CONNECTION_MINIMUM_IDLE = "MYSQL_CONNECTION_MINIMUM_IDLE";
    public static final String MYSQL_CONNECTION_MAXIMUM_CONNECTIONS = "MYSQL_CONNECTION_MAXIMUM_CONNECTIONS";
    public static final String MYSQL_CONNECTION_TIMEOUT = "MYSQL_CONNECTION_TIMEOUT";
    public static final String MYSQL_IDLE_TIMEOUT = "MYSQL_IDLE_TIMEOUT";
    public static final String MYSQL_URL = "MYSQL_URL";
    public static final String MYSQL_DEFAULT_DATABASE = "MYSQL_DEFAULT_DATABASE";
    public static final String MYSQL_CONNECTION_MAX_LIFETIME = "MYSQL_CONNECTION_MAX_LIFETIME";

    // Other configuration environment keys
    public static final String CLIENT_SETTINGS_URL = "CLIENT_SETTINGS_URL";
    public static final String STATS_D_KEY_PREFIX = "STATS_D_KEY_PREFIX";
}