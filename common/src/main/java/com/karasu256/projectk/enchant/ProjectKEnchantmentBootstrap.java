package com.karasu256.projectk.enchant;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;

public final class ProjectKEnchantmentBootstrap {
    private ProjectKEnchantmentBootstrap() {
    }

    public static void bootstrap(BootstrapContext<Enchantment> context) {
        HolderGetter<Item> items = context.lookup(Registries.ITEM);
        register(
                context,
                ProjectKEnchantments.ABYSS_BOOSTER_KEY,
                Enchantment.enchantment(
                        Enchantment.definition(
                                items.getOrThrow(ItemTags.SWORDS),
                                10,
                                30,
                                Enchantment.dynamicCost(1, 10),
                                Enchantment.dynamicCost(1, 15),
                                5,
                                EquipmentSlotGroup.MAINHAND
                        )
                )
        );
    }

    private static void register(BootstrapContext<Enchantment> context, ResourceKey<Enchantment> key, Enchantment.Builder builder) {
        context.register(key, builder.build(key.location()));
    }
}
