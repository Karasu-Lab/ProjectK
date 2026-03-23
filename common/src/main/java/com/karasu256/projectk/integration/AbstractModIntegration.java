package com.karasu256.projectk.integration;

public abstract class AbstractModIntegration implements IModIntegration {

    @Override
    public final void bootstrap() {
        ModIntegrationBootstrapper.bootstrap(getModId(), isModLoaded(), this::onBootstrap);
    }
}
