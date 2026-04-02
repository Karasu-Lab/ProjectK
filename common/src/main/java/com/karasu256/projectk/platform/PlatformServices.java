package com.karasu256.projectk.platform;

public final class PlatformServices {
    private static ProjectKPlatform platform;

    private PlatformServices() {
    }

    public static ProjectKPlatform platform() {
        if (platform == null) {
            throw new IllegalStateException("ProjectKPlatform is not registered");
        }
        return platform;
    }

    public static void register(ProjectKPlatform implementation) {
        if (platform != null) {
            throw new IllegalStateException("ProjectKPlatform is already registered");
        }
        platform = implementation;
    }
}
