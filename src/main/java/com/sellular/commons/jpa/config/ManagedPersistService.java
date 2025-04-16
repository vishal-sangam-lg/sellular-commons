package com.sellular.commons.jpa.config;

import com.google.inject.Inject;
import com.google.inject.persist.PersistService;
import io.dropwizard.lifecycle.Managed;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ManagedPersistService implements Managed {

    private PersistService persistService;

    @Inject
    public ManagedPersistService(PersistService persistService) {
        this.persistService = persistService;
    }

    public void start() {
        log.info("Starting guice persist service");
        persistService.start();
    }

    public void stop() {
        log.info("Stopping suice persist service");
        persistService.stop();
    }

}
