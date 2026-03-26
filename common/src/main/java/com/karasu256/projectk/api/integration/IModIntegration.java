package com.karasu256.projectk.api.integration;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
public interface IModIntegration {
    Logger LOGGER = LoggerFactory.getLogger("ProjectK Mod Loader");

    static void bootstrap(boolean isLoaded, Runnable onBootstrap) {
        if (isLoaded) {
            onBootstrap.run();
        }
    }

    void bootstrap();

    @SuppressWarnings("unused")
    boolean isModLoaded();

    String getModId();

    static <T extends IModIntegration> void bootstrap(@NotNull ModIntegrationSupplier<T> supplier) {
        IModIntegration.bootstrap(supplier.isModLoaded(), () -> supplier.get().bootstrap());
    }
}
