package com.karasu256.projectk.integration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractModIntegration implements IModIntegration {
    private final Logger LOGGER;

    public AbstractModIntegration() {
        LOGGER = LoggerFactory.getLogger(IModIntegration.class);
    }

    @Override
    public void bootStrap() {
        if (isModLoaded()) {
            LOGGER.info("{} was detected. Bootstrapping...", getModId());
            onBootStrap();
        }
    }
}
