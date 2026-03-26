package com.karasu256.karasulab.karasucore.bridge.geckolib.neoforge;

import com.karasu256.karasulab.karasucore.bridge.geckolib.GeckoLibHelper;
import com.karasu256.projectk.neoforge.client.NeoForgeGeckoLibHelper;
import org.jetbrains.annotations.NotNull;

public class GeckoLibBridgeImpl {
    @NotNull
    public static GeckoLibHelper get() {
        return new NeoForgeGeckoLibHelper();
    }
}
