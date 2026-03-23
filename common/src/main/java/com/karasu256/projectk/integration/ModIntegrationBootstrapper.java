package com.karasu256.projectk.integration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModIntegrationBootstrapper {
    private static final Logger LOGGER = LoggerFactory.getLogger(ModIntegrationBootstrapper.class);

    public static void bootstrap(String modId, boolean isLoaded, Runnable onBootstrap) {
        if (isLoaded) {
            LOGGER.info("{} was detected. Bootstrapping...", modId);
            onBootstrap.run();
        }
    }

    @SafeVarargs
    public static <T extends IModIntegration> void bootstrap(ModIntegrationSupplier<T>... suppliers) {
        for (ModIntegrationSupplier<T> supplier : suppliers) {
            bootstrap(supplier.getModId(), supplier.isModLoaded(), () -> supplier.get().onBootstrap());
        }
    }
}
