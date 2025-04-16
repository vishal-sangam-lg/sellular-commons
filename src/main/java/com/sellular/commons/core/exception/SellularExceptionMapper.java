package com.sellular.commons.core.exception;

import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class SellularExceptionMapper implements ExceptionMapper<SellularException> {

    @Override
    @Produces (value = MediaType.APPLICATION_JSON)
    public Response toResponse(final SellularException exception) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", exception.getCode());
        response.put("message", exception.getMessage());
        response.put("header", exception.getHeader());
        response.put("status_code", exception.getStatusCode());
        if (exception.getStatusCode() >= 500) {
            log.error("Sellular Dropwizard Exception - Code: {}, Message: {}, Header: {}, Status Code: {}",
                    exception.getCode(), exception.getMessage(), exception.getHeader(), exception.getStatusCode());
        } else {
            log.warn("Sellular Dropwizard Exception - Code: {}, Message: {}, Header: {}, Status Code: {}",
                    exception.getCode(), exception.getMessage(), exception.getHeader(), exception.getStatusCode());
        }
        return Response.status(exception.getStatusCode()).entity(response).build();
    }

}
