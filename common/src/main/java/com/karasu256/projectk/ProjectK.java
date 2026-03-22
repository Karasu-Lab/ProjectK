package com.karasu256.projectk;

import com.karasu256.projectk.block.ProjectKBlocks;
import com.karasu256.projectk.data.ProjectKDataComponets;
import com.karasu256.projectk.item.ProjectKItems;
import com.karasu256.projectk.registry.BlocksRegistry;
import com.karasu256.projectk.registry.CreativeTabsRegistry;
import com.karasu256.projectk.registry.DataComponentTypesRegistry;
import com.karasu256.projectk.registry.ItemsRegistry;

public final class ProjectK {
    public static final String MOD_ID = "projectk";

    public static void init() {
        ProjectKBlocks.init();
        ProjectKItems.init();
        ProjectKDataComponets.init();

        DataComponentTypesRegistry.register();
        BlocksRegistry.register();
        ItemsRegistry.register();
        CreativeTabsRegistry.register();
    }
}
