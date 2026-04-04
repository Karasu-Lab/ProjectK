package com.karasu256.projectk.energy;

import net.minecraft.world.item.ItemStack;

public interface IEnergyItemOutput {
    boolean canChargeItem(ItemStack stack);

    ItemStack chargeItem(ItemStack stack);
}
