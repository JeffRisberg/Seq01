package com.company.common.config;

import com.company.common.base.config.DatabaseConfig;

import static com.company.common.constants.EnvironmentKeys.REDSHIFT_CLUSTER_MAXIMUM_CONNECTIONS;
import static com.company.common.constants.EnvironmentKeys.REDSHIFT_CLUSTER_MINIMUM_IDLE;
import static com.company.common.constants.EnvironmentKeys.REDSHIFT_HOST;
import static com.company.common.constants.EnvironmentKeys.REDSHIFT_PASSWORD;
import static com.company.common.constants.EnvironmentKeys.REDSHIFT_PORT;
import static com.company.common.constants.EnvironmentKeys.REDSHIFT_USER;

/**
 */
public class EnvironmentBasedRedshiftConfiguration implements DatabaseConfig {

    private String getFromEnv(String field) {
        String result = System.getenv(field);
        if (result == null) {
            throw new RuntimeException("Environment variable: '" + field + "' does not exist");
        }
        return result;
    }

    @Override
    public String getDriverClass() {
        return "org.postgresql.Driver";
    }

    @Override
    public String getUsername() {
        return this.getFromEnv(REDSHIFT_USER);
    }

    @Override
    public String getPassword() {
        return this.getFromEnv(REDSHIFT_PASSWORD);
    }

    @Override
    public String getServer() {
        return String.format("%s:%d", this.getFromEnv(REDSHIFT_HOST), this.getPort());
    }

    @Override
    public String getDb() {
        return "ivdb";
    }

    @Override
    public String getUrl() {
        throw new IllegalArgumentException("You need to specify a cluster number");
    }

    private String buildRedshiftUrl(String server, String db) {
        return String.format("jdbc:postgresql://%s/%s", server, db);
    }

    public int getPort() {
        String port = this.getFromEnv(REDSHIFT_PORT);
        return Integer.parseInt(port);
    }

    @Override
    public int getConnectionMin() {
        String connectionMin = this.getFromEnv(REDSHIFT_CLUSTER_MINIMUM_IDLE);
        return Integer.parseInt(connectionMin);
    }

    @Override
    public int getConnectionMax() {
        String connectionMax = this.getFromEnv(REDSHIFT_CLUSTER_MAXIMUM_CONNECTIONS);
        return Integer.parseInt(connectionMax);
    }

    /*
    @Override
    public long getConnectionTimeoutMs() {
        String connectionTimeout = this.getFromEnv(REDSHIFT_CLUSTER_CONNECTION_TIMEOUT);
        return Integer.parseInt(connectionTimeout);
    }

    @Override
    public long getConnectionIdleTimeoutMs() {
        String connectionTimeout = this.getFromEnv(REDSHIFT_CLUSTER_IDLE_TIMEOUT);
        return Integer.parseInt(connectionTimeout);
    }

    @Override
    public long getConnectionMaxLifetimeMs() {
        String connectionMaxLifetime = this.getFromEnv(REDSHIFT_CLUSTER_CONNECTION_MAX_LIFETIME);
        return Integer.parseInt(connectionMaxLifetime);
    }

    @Override
    public String getConnectionTestQuery() {
        return "SELECT 1";
    }
    */
}
