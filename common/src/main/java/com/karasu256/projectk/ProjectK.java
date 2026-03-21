package com.karasu256.projectk;

import com.karasu256.projectk.registry.BlocksRegistry;
import com.karasu256.projectk.registry.CreativeTabsRegistry;
import com.karasu256.projectk.registry.DataComponentTypesRegistry;
import com.karasu256.projectk.registry.ItemsRegistry;

public final class ProjectK {
    public static final String MOD_ID = "projectk";

    public static void init() {
        BlocksRegistry.register();
        CreativeTabsRegistry.register();
        DataComponentTypesRegistry.register();
        ItemsRegistry.register();
    }
}
