package com.karasu256.projectk.integration;

import java.util.Arrays;
import java.util.function.Supplier;

public class ModIntegrationBootstrapper {
    @SafeVarargs
    public static <T extends IModIntegration> void bootstrap(Supplier<T>... modIntegrations) {
        var list = Arrays.stream(modIntegrations).toList();
        list.forEach(modIntegrationSupplier -> {
            var modIntegration = modIntegrationSupplier.get();
            if (modIntegration.isModLoaded()) {
                modIntegration.bootStrap();
            }
        });
    }
}
