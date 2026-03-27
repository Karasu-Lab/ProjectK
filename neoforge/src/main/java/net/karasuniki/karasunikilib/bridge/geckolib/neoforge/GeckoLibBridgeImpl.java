package net.karasuniki.karasunikilib.bridge.geckolib.neoforge;

import com.karasu256.projectk.neoforge.client.NeoForgeGeckoLibHelper;
import net.karasuniki.karasunikilib.bridge.geckolib.GeckoLibHelper;
import org.jetbrains.annotations.NotNull;

public class GeckoLibBridgeImpl {
    @NotNull
    public static GeckoLibHelper get() {
        return new NeoForgeGeckoLibHelper();
    }
}
