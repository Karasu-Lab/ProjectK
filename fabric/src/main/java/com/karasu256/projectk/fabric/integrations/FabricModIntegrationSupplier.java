package com.karasu256.projectk.fabric.integrations;

import com.karasu256.projectk.api.integration.IModIntegration;
import com.karasu256.projectk.api.integration.ModIntegrationSupplier;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import org.jetbrains.annotations.NotNull;

public class FabricModIntegrationSupplier<T extends IModIntegration> extends ModIntegrationSupplier<T> {
    private final boolean environmentValid;

    public FabricModIntegrationSupplier(@NotNull String className) {
        super(className, IFabricModIntegration::isModLoaded);
        this.environmentValid = checkEnvironment(className);
    }

    @Override
    public boolean isModLoaded() {
        return environmentValid && super.isModLoaded();
    }

    private static boolean checkEnvironment(String className) {
        try {
            Class<?> clazz = Class.forName(className, false, Thread.currentThread().getContextClassLoader());
            Environment env = clazz.getAnnotation(Environment.class);
            return env != null && env.value() == FabricLoader.getInstance().getEnvironmentType();
        } catch (Throwable t) {
            return false;
        }
    }
}
