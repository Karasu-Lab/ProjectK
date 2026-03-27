package com.karasu256.projectk.neoforge.integrations;

import net.karasuniki.karasunikilib.api.IModIntegration;
import net.karasuniki.karasunikilib.api.ModIntegrationSupplier;
import org.jetbrains.annotations.NotNull;

public class NeoForgeModIntegrationSupplier<T extends IModIntegration> extends ModIntegrationSupplier<T> {
    public NeoForgeModIntegrationSupplier(@NotNull String className) {
        super(className, INeoForgeModIntegration::isModLoaded);
    }
}
