package com.karasu256.projectk;

import com.karasu256.projectk.block.ProjectKBlocks;
import com.karasu256.projectk.block.entity.ProjectKBlockEntities;
import com.karasu256.projectk.data.ProjectKDataComponets;
import com.karasu256.projectk.event.ModEvents;
import com.karasu256.projectk.item.ProjectKItems;
import com.karasu256.projectk.registry.BlockEntitiesRegistry;
import com.karasu256.projectk.registry.BlocksRegistry;
import com.karasu256.projectk.registry.CreativeTabsRegistry;
import com.karasu256.projectk.registry.DataComponentTypesRegistry;
import com.karasu256.projectk.registry.EntitiesRegistry;
import com.karasu256.projectk.registry.ParticlesRegistry;
import com.karasu256.projectk.registry.EnergiesRegistry;
import com.karasu256.projectk.registry.ItemsRegistry;

public final class ProjectK {
    public static final String MOD_ID = "projectk";

    public static void init() {
        ProjectKBlocks.init();
        ProjectKBlockEntities.init();
        ProjectKItems.init();
        ProjectKDataComponets.init();
        ModEvents.init();

        DataComponentTypesRegistry.register();
        EntitiesRegistry.register();
        ParticlesRegistry.register();
        BlocksRegistry.register();
        BlockEntitiesRegistry.register();
        ItemsRegistry.register();
        CreativeTabsRegistry.register();
        EnergiesRegistry.register();
    }
}
