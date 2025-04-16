package com.sellular.commons.jpa.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.dropwizard.core.setup.Environment;
import io.dropwizard.lifecycle.Managed;
import lombok.Getter;

public class InstrumentedHikariDataSource implements Managed {

    private final HikariDatabaseConfig databaseConfig;

    @Getter
    private final HikariDataSource hikariDataSource;

    public InstrumentedHikariDataSource(Environment environment, HikariDatabaseConfig databaseConfig) {
        hikariDataSource = new HikariDataSource(createHikariConfig(databaseConfig));
        this.databaseConfig = databaseConfig;
    }

    private static HikariConfig createHikariConfig(HikariDatabaseConfig databaseConfig) {
        final HikariConfig config = new HikariConfig();
        config.setDriverClassName(databaseConfig.getDriverClass());
        config.setJdbcUrl(databaseConfig.getUrl());
        config.setUsername(databaseConfig.getUser());
        config.setPassword(databaseConfig.getPassword());
        return config;
    }

}
