package com.sellular.commons.core.filter;

import com.sellular.commons.core.exception.SellularException;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.Priorities;
import jakarta.annotation.Priority;
import org.eclipse.jetty.http.HttpStatus;

@AccessTokenRequired
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AccessTokenAuthFilter implements ContainerRequestFilter {

    private static final String HEADER_NAME = "ACCESS-TOKEN";

    private final String expectedToken;

    public AccessTokenAuthFilter(final String expectedAccessToken) {
        this.expectedToken = expectedAccessToken;
    }

    @Override
    public void filter(ContainerRequestContext requestContext) {

        final String incomingToken = requestContext.getHeaderString(HEADER_NAME);

        if (incomingToken == null) {
            throw SellularException.builder()
                    .code("1000")
                    .header("Bad Request")
                    .statusCode(HttpStatus.UNAUTHORIZED_401)
                    .message("Access Token is required")
                    .build();
        } else if (!incomingToken.equals(expectedToken)) {
            throw SellularException.builder()
                    .code("1000")
                    .header("Bad Request")
                    .statusCode(HttpStatus.UNAUTHORIZED_401)
                    .message("Access Token is invalid")
                    .build();
        }

    }
}