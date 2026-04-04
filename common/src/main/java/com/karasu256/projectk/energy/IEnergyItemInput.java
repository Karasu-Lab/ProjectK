package com.karasu256.projectk.energy;

import net.minecraft.world.item.ItemStack;

public interface IEnergyItemInput {
    boolean canAcceptEnergyItem(ItemStack stack);

    ItemStack chargeFromItem(ItemStack stack);
}
