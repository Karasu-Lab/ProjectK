package com.karasu256.projectk.menu;

import com.karasu256.projectk.energy.IEnergyItemInput;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class EnergyInputSlot extends Slot {
    private final IEnergyItemInput handler;

    public EnergyInputSlot(Container container, int slot, int x, int y, IEnergyItemInput handler) {
        super(container, slot, x, y);
        this.handler = handler;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return handler != null && handler.canAcceptEnergyItem(stack);
    }
}
