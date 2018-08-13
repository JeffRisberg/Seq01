package com.company.common.base.config;

/**
 */
public interface DatabaseConfig {
    String getDriverClass();

    String getUsername();

    String getPassword();

    String getServer();

    String getDb();

    String getUrl();

    int getConnectionMin();

    int getConnectionMax();
}
