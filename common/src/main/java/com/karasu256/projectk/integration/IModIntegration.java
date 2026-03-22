package com.karasu256.projectk.integration;

public interface IModIntegration {
    void bootStrap();

    void onBootStrap();

    boolean isModLoaded();

    String getModId();
}
