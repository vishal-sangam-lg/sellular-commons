package com.sellular.commons.jpa.config;

import com.google.inject.AbstractModule;
import javax.sql.DataSource;
import java.util.Map;
import java.util.Properties;

import com.google.inject.persist.jpa.JpaPersistOptions;
import org.hibernate.cfg.Environment;
import com.google.inject.Module;
import com.google.inject.persist.jpa.JpaPersistModule;

public class GuiceJpaModule extends AbstractModule {

    private Map<String, Object> additionalProperties;

    private DataSource dataSource;

    private String persistenceUnitName;

    public GuiceJpaModule(final DataSource dataSource, final String persistenceUnitName, final Map<String, Object> additionalProperties) {
        this.additionalProperties = additionalProperties;
        this.persistenceUnitName = persistenceUnitName;
        this.dataSource = dataSource;
    }

    @Override
    protected void configure() {
        install(jpaModule());
    }

    private Module jpaModule() {
        final Properties properties = new Properties();
        properties.put(Environment.DATASOURCE, dataSource);
        properties.putAll(additionalProperties);
        final JpaPersistModule jpaModule = new JpaPersistModule(persistenceUnitName, getJpaPersistOptions());
        jpaModule.properties(properties);
        return jpaModule;
    }

    private JpaPersistOptions getJpaPersistOptions() {
        return JpaPersistOptions.builder()
                .setAutoBeginWorkOnEntityManagerCreation(true)
                .build();
    }

}
