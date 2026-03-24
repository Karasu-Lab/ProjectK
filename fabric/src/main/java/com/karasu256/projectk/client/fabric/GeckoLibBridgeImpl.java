package com.karasu256.projectk.client.fabric;

import com.karasu256.projectk.client.GeckoLibHelper;
import com.karasu256.projectk.fabric.client.FabricGeckoLibHelper;

public class GeckoLibBridgeImpl {
    public static GeckoLibHelper get() {
        return new FabricGeckoLibHelper();
    }
}
