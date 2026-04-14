package com.karasu256.projectk.item.custom;

import com.karasu256.projectk.energy.AbyssEnergyUtils;
import com.karasu256.projectk.energy.ProjectKEnergies;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public class AbyssCoreItem extends BlockItem {
    public AbyssCoreItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public @NotNull Component getName(ItemStack stack) {
        return ProjectKEnergies.getDefinition(AbyssEnergyUtils.getEffectiveEnergyId(stack))
                .<Component>map(def -> Component.translatable("block.projectk." + def.idPath() + "_core"))
                .orElse(super.getName(stack));
    }
}
