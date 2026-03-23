package com.karasu256.projectk.integration;

import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public interface IModIntegration {
    static <T extends IModIntegration> void bootstrap(@NotNull Supplier<T> modIntegration) {
        modIntegration.get().bootstrap();
    }

    void bootstrap();

    void onBootstrap();

    boolean isModLoaded();

    String getModId();
}
