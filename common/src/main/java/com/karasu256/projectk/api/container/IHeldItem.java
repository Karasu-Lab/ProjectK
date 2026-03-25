package com.karasu256.projectk.api.container;

import com.karasu256.projectk.api.nbt.INbtData;
import net.minecraft.world.item.ItemStack;

public interface IHeldItem extends INbtData {
    ItemStack getHeldItem();

    void setHeldItem(ItemStack stack);
}
