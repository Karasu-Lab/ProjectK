package com.karasu256.projectk.api.integration;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class ModIntegrationBootstrapper {

    @SafeVarargs
    public static <T extends IModIntegration> void bootstrap(ModIntegrationSupplier<T> @NotNull ... suppliers) {
        Arrays.stream(suppliers).toList().forEach(IModIntegration::bootstrap);
    }
}
