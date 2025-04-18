package com.sellular.commons.dropwizard.configuration;

import io.dropwizard.core.Configuration;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class BaseConfiguration extends Configuration {

    private String accessToken;

    private String serviceName;

    private SwaggerBundleConfiguration swaggerBundleConfiguration;

}
