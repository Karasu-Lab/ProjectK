package com.karasu256.projectk.neoforge.integrations;

import com.karasu256.projectk.integration.IModIntegration;
import com.karasu256.projectk.integration.ModIntegrationSupplier;
import org.jetbrains.annotations.NotNull;


public class NeoForgeModIntegrationSupplier<T extends IModIntegration> extends ModIntegrationSupplier<T> {
    public NeoForgeModIntegrationSupplier(@NotNull String className) {
        super(className, INeoForgeModIntegration::isModLoaded);
    }
}
