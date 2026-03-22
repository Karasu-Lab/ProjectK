package com.karasu256.projectk.integration;

public interface IModIntegration {
    void bootstrap();

    void onBootstrap();

    boolean isModLoaded();

    String getModId();
}
