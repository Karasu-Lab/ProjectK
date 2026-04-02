package com.karasu256.projectk.compat.wthit;

import com.karasu256.projectk.block.entity.AbyssEnergyCableBlockEntity;
import com.karasu256.projectk.block.entity.AbyssMagicTableBlockEntity;
import com.karasu256.projectk.block.entity.impl.AbstractPKEnergyBlockEntity;
import com.karasu256.projectk.energy.IProjectKEnergy;
import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITooltip;
import net.karasuniki.karasunikilib.api.IHeldItem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ProjectKEnergyBlockWthitProvider implements IBlockComponentProvider {
    public static final ProjectKEnergyBlockWthitProvider INSTANCE = new ProjectKEnergyBlockWthitProvider();

    @Override
    public void appendBody(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
        BlockEntity be = accessor.getBlockEntity();
        if (be instanceof AbyssMagicTableBlockEntity) {
            return;
        }
        if (be instanceof AbstractPKEnergyBlockEntity<?> energyBe) {
            addEnergyInfo(tooltip, energyBe.getEnergyType(), energyBe.getAmount(), energyBe.getCapacity());
            addHeldItem(tooltip, energyBe);
            return;
        }
        if (be instanceof AbyssEnergyCableBlockEntity cable) {
            addEnergyInfo(tooltip, cable.getEnergyType(), cable.getEnergyAmount(), cable.getEnergyCapacity());
            return;
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
