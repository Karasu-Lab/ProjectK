package com.karasu256.projectk.menu;

import com.karasu256.projectk.energy.IEnergyItemOutput;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class EnergyChargeSlot extends Slot {
    private final IEnergyItemOutput handler;

    public EnergyChargeSlot(Container container, int slot, int x, int y, IEnergyItemOutput handler) {
        super(container, slot, x, y);
        this.handler = handler;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return handler != null && handler.canChargeItem(stack);
    }
}
