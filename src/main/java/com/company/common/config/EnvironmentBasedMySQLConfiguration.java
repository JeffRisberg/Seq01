package com.company.common.config;

import com.company.common.base.config.DatabaseConfig;

import static com.company.common.constants.EnvironmentKeys.MYSQL_CONNECTION_MAXIMUM_CONNECTIONS;
import static com.company.common.constants.EnvironmentKeys.MYSQL_CONNECTION_MINIMUM_IDLE;
import static com.company.common.constants.EnvironmentKeys.MYSQL_DEFAULT_DATABASE;
import static com.company.common.constants.EnvironmentKeys.MYSQL_PASSWORD;
import static com.company.common.constants.EnvironmentKeys.MYSQL_URL;
import static com.company.common.constants.EnvironmentKeys.MYSQL_USER;

public class EnvironmentBasedMySQLConfiguration implements DatabaseConfig {

    private String getFromEnv(String field) {
        String result = System.getenv(field);
        if (result == null) {
            throw new RuntimeException("Environment variable: '" + field + "' does not exist");
        }
        return result;
    }

    @Override
    public String getDriverClass() {
        return "com.mysql.jdbc.Driver";
    }

    @Override
    public String getUsername() {
        return this.getFromEnv(MYSQL_USER);
    }

    @Override
    public String getPassword() {
        return this.getFromEnv(MYSQL_PASSWORD);
    }

    @Override
    public String getServer() {
        return this.getFromEnv(MYSQL_URL);
    }

    @Override
    public String getDb() {
        return this.getFromEnv(MYSQL_DEFAULT_DATABASE);
    }

    @Override
    public String getUrl() {
        if (this.getDb().equals("")) {
            return this.getFromEnv(MYSQL_URL);
        } else {
            return this.getFromEnv(MYSQL_URL) + "/" + this.getDb();
        }
    }

    @Override
    public int getConnectionMin() {
        String connectionMin = this.getFromEnv(MYSQL_CONNECTION_MINIMUM_IDLE);
        return Integer.parseInt(connectionMin);
    }

    @Override
    public int getConnectionMax() {
        String connectionMax = this.getFromEnv(MYSQL_CONNECTION_MAXIMUM_CONNECTIONS);
        return Integer.parseInt(connectionMax);
    }

    /*
    @Override
    public long getConnectionTimeoutMs() {
        String connectionTimeout = this.getFromEnv(MYSQL_CONNECTION_TIMEOUT);
        return Integer.parseInt(connectionTimeout);
    }

    @Override
    public long getConnectionIdleTimeoutMs() {
        String connectionTimeout = this.getFromEnv(MYSQL_IDLE_TIMEOUT);
        return Integer.parseInt(connectionTimeout);
    }

    @Override
    public long getConnectionMaxLifetimeMs() {
        return Long.parseLong(this.getFromEnv(MYSQL_CONNECTION_MAX_LIFETIME));
    }

    @Override
    public String getConnectionTestQuery() {
        return "SELECT 1";
    }
    */
}
