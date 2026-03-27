package net.karasuniki.karasunikilib.bridge.geckolib.fabric;

import com.karasu256.projectk.fabric.client.FabricGeckoLibHelper;
import net.karasuniki.karasunikilib.bridge.geckolib.GeckoLibHelper;
import org.jetbrains.annotations.NotNull;

public class GeckoLibBridgeImpl {
    @NotNull
    public static GeckoLibHelper get() {
        return new FabricGeckoLibHelper();
    }
}
