package com.karasu256.projectk.client;

import dev.architectury.injectables.annotations.ExpectPlatform;

public class GeckoLibBridge {
    @ExpectPlatform
    public static GeckoLibHelper get() {
        throw new AssertionError();
    }
}
