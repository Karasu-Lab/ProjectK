package com.karasu256.projectk.api.integration;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class ModIntegrationBootstrapper {

    @SafeVarargs
    public static <T extends IModIntegration> void bootstrap(ModIntegrationSupplier<T> @NotNull ... suppliers) {
        Arrays.stream(suppliers).forEach(supplier -> {
            if (supplier.isModLoaded()) {
                T integration = supplier.get();
                integration.bootstrap();
                ModIntegrationRegistry.register(integration);
            }
        });
    }
}
