package com.karasu256.projectk.neoforge.integrations;

import com.karasu256.karasulab.karasucore.api.IModIntegration;
import com.karasu256.karasulab.karasucore.api.ModIntegrationSupplier;
import org.jetbrains.annotations.NotNull;

public class NeoForgeModIntegrationSupplier<T extends IModIntegration> extends ModIntegrationSupplier<T> {
    public NeoForgeModIntegrationSupplier(@NotNull String className) {
        super(className, INeoForgeModIntegration::isModLoaded);
    }
}
