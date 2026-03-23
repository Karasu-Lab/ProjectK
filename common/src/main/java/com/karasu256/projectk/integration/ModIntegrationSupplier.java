package com.karasu256.projectk.integration;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;

import java.util.function.Predicate;
import java.util.function.Supplier;

public class ModIntegrationSupplier<T extends IModIntegration> implements Supplier<T> {
    private static final String MOD_ID_FIELD = "MOD_ID";

    private final String className;
    private final Predicate<String> modLoadedChecker;
    private final String modId;

    public ModIntegrationSupplier(@NotNull String className, @NotNull Predicate<String> modLoadedChecker) {
        this.className = className;
        this.modLoadedChecker = modLoadedChecker;
        this.modId = resolveModId(className);
    }

    @Nullable
    private static String resolveModId(@NotNull String className) {
        try {
            Class<?> clazz = Class.forName(className, false, Thread.currentThread().getContextClassLoader());
            Field field = clazz.getDeclaredField(MOD_ID_FIELD);
            field.setAccessible(true);
            return (String) field.get(null);
        } catch (Throwable t) {
            return null;
        }
    }

    public boolean isModLoaded() {
        return modId != null && modLoadedChecker.test(modId);
    }


    public String getModId() {
        return modId;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T get() {
        try {
            Class<?> clazz = Class.forName(className, true, Thread.currentThread().getContextClassLoader());
            return (T) clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Failed to instantiate integration class: " + className, e);
        }
    }
}
