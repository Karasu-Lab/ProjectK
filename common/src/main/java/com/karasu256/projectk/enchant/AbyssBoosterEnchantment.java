package com.karasu256.projectk.enchant;

import com.karasu256.projectk.utils.Id;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.enchantment.Enchantment;

public final class AbyssBoosterEnchantment {
    private AbyssBoosterEnchantment() {
    }

    public static Enchantment create() {
        return Enchantment.enchantment(Enchantment.definition(BuiltInRegistries.ITEM.getOrCreateTag(ItemTags.SWORDS), 10, 30, Enchantment.dynamicCost(1, 10), Enchantment.dynamicCost(1, 15), 5, EquipmentSlotGroup.MAINHAND)).build(Id.id("abyss_booster"));
    }
}
