package com.sellular.commons.core.annotations;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Parameters({
        @Parameter(name = "X-APP-TOKEN", description = "App Token of the caller", required = true, in = ParameterIn.HEADER),
        @Parameter(name = "CONTENT-TYPE", description = "Content Type", in = ParameterIn.HEADER, example = "application/json")
})
public @interface GlobalSwaggerHeaders {
}
