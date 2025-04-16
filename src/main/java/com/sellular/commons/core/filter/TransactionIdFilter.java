package com.sellular.commons.core.filter;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.UUID;

@Provider
@Priority(Priorities.AUTHENTICATION)
@Slf4j
public class TransactionIdFilter implements ContainerRequestFilter {

    private static final String TRANSACTION_ID_HEADER = "x-transaction-id";

    public static final String SERVICE_HEADER = "service";

    private String serviceName;

    public TransactionIdFilter(final String serviceName) {
        this.serviceName = serviceName;
    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String transactionId = requestContext.getHeaderString(TRANSACTION_ID_HEADER);

        if (transactionId == null || transactionId.isEmpty()) {
            transactionId = UUID.randomUUID().toString();
        }

        // Set it in MDC for logging
        MDC.put(TRANSACTION_ID_HEADER, transactionId);
        MDC.put(SERVICE_HEADER, serviceName);

        // Also make sure it's available in downstream calls
        requestContext.setProperty(TRANSACTION_ID_HEADER, transactionId);
    }
}

