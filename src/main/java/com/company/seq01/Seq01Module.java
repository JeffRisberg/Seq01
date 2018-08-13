package com.company.seq01;

import com.company.common.services.MicroserviceModule;
import com.google.inject.Scopes;
import com.company.common.base.binding.annotation.MySQL;
import com.company.common.base.binding.annotation.RedshiftP;
import com.company.common.base.config.AppConfig;
import com.company.common.base.config.DatabaseConfig;
import com.company.common.base.persist.db.ConnectionFactory;
import com.company.common.base.persist.db.MySQLConnectionFactory;
import com.company.common.config.EnvironmentBasedAppConfig;
import com.company.common.config.EnvironmentBasedMySQLConfiguration;
import com.company.common.config.EnvironmentBasedRedshiftConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class Seq01Module extends MicroserviceModule {
    private static final Logger logger = LoggerFactory.getLogger(Seq01Module.class);

    @Override
    public void configureDependencies() {
        // configs
        bind(AppConfig.class).to(EnvironmentBasedAppConfig.class).in(Scopes.SINGLETON);
        bind(DatabaseConfig.class).annotatedWith(MySQL.class).to(EnvironmentBasedMySQLConfiguration.class).in(Scopes.SINGLETON);
        bind(DatabaseConfig.class).annotatedWith(RedshiftP.class).to(EnvironmentBasedRedshiftConfiguration.class).in(Scopes.SINGLETON);

        // connection factories
        bind(ConnectionFactory.class).annotatedWith(MySQL.class).to(MySQLConnectionFactory.class).in(Scopes.SINGLETON);
    }

    @Override
    protected String programName() {
        return "seq01-service";
    }

    @Override
    public List<Class<?>> resources() {
        return Arrays.asList(
                // SystemEndpoint.class  -- this should exist
                com.company.seq01.endpoints.JobEndpoint.class,
                com.company.seq01.endpoints.JobExecutionEndpoint.class
        );
    }

    //@Provides
    //@StartUpDelay
    //int getStartUpDelayInSec() {
    //    return 5;
    //}

    private String getEnvironmentValue(String key) {
        return Optional.ofNullable(System.getenv(key)).orElseThrow(() ->
            new RuntimeException(String.format("Environment variable: '%s' does not exist", key)));
    }
}
