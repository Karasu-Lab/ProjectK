package com.karasu256.projectk.client.neoforge;

import com.karasu256.projectk.client.GeckoLibHelper;
import com.karasu256.projectk.neoforge.client.NeoForgeGeckoLibHelper;

public class GeckoLibBridgeImpl {
    public static GeckoLibHelper get() {
        return new NeoForgeGeckoLibHelper();
    }
}
