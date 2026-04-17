package com.karasu256.projectk.item.custom;

import com.karasu256.projectk.data.AbyssEnergyData;
import com.karasu256.projectk.energy.ProjectKEnergies;
import com.karasu256.projectk.item.IVariantItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class AbyssMachineBlockItem extends ProjectKBlockItem {
    private final long initialAmount;

    public AbyssMachineBlockItem(Block block, Properties properties, long initialAmount) {
        super(block, properties);
        this.initialAmount = initialAmount;
    }

    @Override
    public boolean shouldSkipDefault() {
        return true;
    }

    @Override
    public void displayVariants(CreativeModeTab.Output output) {
        for (ProjectKEnergies.EnergyDefinition def : ProjectKEnergies.getDefinitions()) {
            ItemStack stack = new ItemStack(this);
            AbyssEnergyData.applyToStack(stack, def.id(), initialAmount);
            output.accept(stack);
        }
    }
}
