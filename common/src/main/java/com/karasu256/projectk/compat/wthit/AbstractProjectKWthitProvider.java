package com.karasu256.projectk.compat.wthit;

import com.karasu256.projectk.energy.IEnergyListHolder;
import com.karasu256.projectk.energy.IProjectKEnergy;
import com.karasu256.projectk.energy.ITierInfo;
import com.karasu256.projectk.energy.ProjectKEnergies;
import com.karasu256.projectk.item.custom.AbyssEnergyItem;
import mcp.mobius.waila.api.ITooltip;
import net.karasuniki.karasunikilib.api.IHeldItem;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.List;

public abstract class AbstractProjectKWthitProvider {
    protected void addEnergyEntries(ITooltip tooltip, List<IEnergyListHolder.EnergyEntry> entries) {
        for (IEnergyListHolder.EnergyEntry entry : entries) {
            ResourceLocation id = entry.id();
            if (id == null || entry.amount() <= 0) {
                continue;
            }
            Component name = ProjectKEnergies.getDefinition(id).map(ProjectKEnergies.EnergyDefinition::getDisplayName)
                    .orElseGet(() -> AbyssEnergyItem.resolveEnergyName(id));
            Component formatted = Component.translatable("energy.projectk.abyss_energy_format", name);
            tooltip.addLine(formatted);
            if (entry.capacity() == null) {
                tooltip.addLine(Component.translatable("tooltip.projectk.wthit.energy_no_limit", entry.amount()));
            } else {
                tooltip.addLine(
                        Component.translatable("tooltip.projectk.wthit.energy", entry.amount(), entry.capacity()));
            }
            tooltip.addLine(new AbyssEnergyBarComponent(id, entry.amount(), entry.capacity(), entry.active()));
        }
    }

    protected void addEnergyInfo(ITooltip tooltip, IProjectKEnergy energy, long amount, Long capacity) {
        if (energy != null) {
            tooltip.addLine(Component.translatable("tooltip.projectk.wthit.energy_type", energy.getName()));
        }
        if (capacity == null) {
            tooltip.addLine(Component.translatable("tooltip.projectk.wthit.energy_no_limit", amount));
        } else {
            tooltip.addLine(Component.translatable("tooltip.projectk.wthit.energy", amount, capacity));
        }
    }

    protected void addHeldItem(ITooltip tooltip, IHeldItem heldItem) {
        ItemStack stack = heldItem.getHeldItem();
        if (!stack.isEmpty()) {
            tooltip.addLine(Component.translatable("tooltip.projectk.wthit.held_item", stack.getHoverName()));
        }
    }

    protected void addTierInfo(ITooltip tooltip, BlockEntity be) {
        if (be instanceof ITierInfo tierInfo) {
            tooltip.addLine(
                    Component.translatable("tooltip.projectk.wthit.tier", tierInfo.getTier(), tierInfo.getMaxTier()));
        }
    }
}
