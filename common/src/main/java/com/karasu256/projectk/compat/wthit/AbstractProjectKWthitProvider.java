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
        if (entries.isEmpty()) {
            return;
        }
        for (IEnergyListHolder.EnergyEntry entry : entries) {
            ResourceLocation id = entry.id();
            if (id == null) {
                continue;
            }
            Component name = ProjectKEnergies.getDefinition(id).map(ProjectKEnergies.EnergyDefinition::getDisplayName)
                    .orElseGet(() -> AbyssEnergyItem.resolveEnergyName(id));
            tooltip.addLine(name);

            boolean isInfinite = ProjectKEnergies.isInfinite(entry.amount(), entry.capacity());

            if (isInfinite) {
                tooltip.addLine(Component.translatable("tooltip.projectk.infinite"));
            } else if (entry.capacity() == null || entry.capacity() <= 0) {
                tooltip.addLine(Component.literal(String.format("%,d ", entry.amount())).append(Component.translatable("tooltip.projectk.energy_unit")));
            } else {
                tooltip.addLine(Component.literal(String.format("%,d / %,d ", entry.amount(), entry.capacity())).append(Component.translatable("tooltip.projectk.energy_unit")));
            }
            tooltip.addLine(new AbyssEnergyBarComponent(id, entry.amount(), entry.capacity(), entry.active()));
        }
    }

    protected void addEnergyInfo(ITooltip tooltip, IProjectKEnergy energy, long amount, Long capacity) {
        if (energy != null) {
            ResourceLocation id = energy.getId();
            addEnergyEntries(tooltip, List.of(new IEnergyListHolder.EnergyEntry(id, amount, capacity, false)));
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
