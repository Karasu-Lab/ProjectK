package com.karasu256.projectk.registry;

import dev.architectury.registry.registries.DeferredRegister;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;

import static com.karasu256.projectk.ProjectK.MOD_ID;

public class CreativeTabsRegistry {
    private static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(MOD_ID, Registries.CREATIVE_MODE_TAB);

    public static void register() {
        TABS.register();
    }
}
