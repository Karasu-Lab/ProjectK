package com.karasu256.projectk.integration;

public abstract class AbstractModIntegration implements IModIntegration {
    @Override
    public void bootstrap() {
        LOGGER.info("Initializing {} integration", getModId());
    }
}
