package com.karasu256.karasulab.karasucore.bridge.geckolib.fabric;

import com.karasu256.karasulab.karasucore.bridge.geckolib.GeckoLibHelper;
import com.karasu256.projectk.fabric.client.FabricGeckoLibHelper;
import org.jetbrains.annotations.NotNull;

public class GeckoLibBridgeImpl {
    @NotNull
    public static GeckoLibHelper get() {
        return new FabricGeckoLibHelper();
    }
}
