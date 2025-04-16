package com.sellular.commons.dropwizard;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.sellular.commons.core.exception.SellularExceptionMapper;
import com.sellular.commons.core.filter.AccessTokenAuthFilter;
import com.sellular.commons.core.filter.MdcCleanupFilter;
import com.sellular.commons.core.filter.TransactionIdFilter;
import com.sellular.commons.dropwizard.configuration.BaseConfiguration;
import io.dropwizard.core.Application;
import io.dropwizard.core.setup.Bootstrap;
import io.dropwizard.core.setup.Environment;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;

public abstract class BaseApplication<T extends BaseConfiguration> extends Application<T> {

    @Override
    public void initialize(final Bootstrap<T> bootstrap) {
        bootstrap.setConfigurationSourceProvider(
                new SubstitutingSourceProvider(
                        bootstrap.getConfigurationSourceProvider(),
                        new EnvironmentVariableSubstitutor(true)
                )
        );
    }

    public void run(T configuration, Environment environment) throws Exception {
        environment.getObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        environment.jersey().register(SellularExceptionMapper.class);
        environment.jersey().register(new AccessTokenAuthFilter(configuration.getAccessToken()));
        environment.jersey().register(new TransactionIdFilter(configuration.getServiceName()));
        environment.jersey().register(new MdcCleanupFilter());
        // TODO: Add request tracing filter
    }

}
