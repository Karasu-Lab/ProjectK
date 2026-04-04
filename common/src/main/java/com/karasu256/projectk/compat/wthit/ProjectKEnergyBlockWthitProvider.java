package com.karasu256.projectk.compat.wthit;

import com.karasu256.projectk.block.entity.AbyssMagicTableBlockEntity;
import com.karasu256.projectk.block.entity.impl.AbstractPKEnergyBlockEntity;
import com.karasu256.projectk.energy.IEnergyListHolder;
import com.karasu256.projectk.energy.IProjectKEnergy;
import com.karasu256.projectk.energy.ProjectKEnergies;
import com.karasu256.projectk.item.custom.AbyssEnergyItem;
import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITooltip;
import net.karasuniki.karasunikilib.api.IHeldItem;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.List;

public class ProjectKEnergyBlockWthitProvider implements IBlockComponentProvider {
    public static final ProjectKEnergyBlockWthitProvider INSTANCE = new ProjectKEnergyBlockWthitProvider();

    public static void addEnergyEntries(ITooltip tooltip, List<IEnergyListHolder.EnergyEntry> entries) {
        for (IEnergyListHolder.EnergyEntry entry : entries) {
            ResourceLocation id = entry.id();
            if (id == null || entry.amount() <= 0) {
                continue;
            }
            Component name = ProjectKEnergies.getDefinition(id).map(definition -> (Component) Component.translatable("energy.projectk." + definition.idPath())).orElseGet(() -> AbyssEnergyItem.resolveEnergyName(id));
            Component formatted = Component.translatable("energy.projectk.abyss_energy_format", name);
            tooltip.addLine(formatted);
            tooltip.addLine(Component.translatable("tooltip.projectk.wthit.energy", entry.amount(), entry.capacity()));
            tooltip.addLine(new AbyssEnergyBarComponent(id, entry.amount(), entry.capacity(), entry.active()));
        }
    }

    @Override
    public void appendBody(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
        BlockEntity be = accessor.getBlockEntity();
        if (be instanceof AbyssMagicTableBlockEntity) {
            return;
        }
        if (be instanceof IEnergyListHolder listHolder) {
            addEnergyEntries(tooltip, listHolder.getEnergyEntries());
            if (be instanceof IHeldItem held) {
                addHeldItem(tooltip, held);
            }
            return;
        }
        if (be instanceof AbstractPKEnergyBlockEntity<?> energyBe) {
            addEnergyInfo(tooltip, energyBe.getEnergyType(), energyBe.getAmount(), energyBe.getCapacity());
            addHeldItem(tooltip, energyBe);
        }
    }

    private void addEnergyInfo(ITooltip tooltip, IProjectKEnergy energy, long amount, long capacity) {
        if (energy != null) {
            tooltip.addLine(Component.translatable("tooltip.projectk.wthit.energy_type", energy.getName()));
        }
        tooltip.addLine(Component.translatable("tooltip.projectk.wthit.energy", amount, capacity));
    }

    private void addHeldItem(ITooltip tooltip, IHeldItem heldItem) {
        ItemStack stack = heldItem.getHeldItem();
        if (!stack.isEmpty()) {
            tooltip.addLine(Component.translatable("tooltip.projectk.wthit.held_item", stack.getHoverName()));
        }
    }
}
