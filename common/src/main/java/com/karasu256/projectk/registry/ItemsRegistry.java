package com.karasu256.projectk.registry;

import dev.architectury.registry.registries.DeferredRegister;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;

import static com.karasu256.projectk.ProjectK.MOD_ID;

public class ItemsRegistry {
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(MOD_ID, Registries.ITEM);

    public static void register(){
        ITEMS.register();
    }
}
