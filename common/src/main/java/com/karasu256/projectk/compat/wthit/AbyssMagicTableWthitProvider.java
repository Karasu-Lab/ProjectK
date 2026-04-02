package com.karasu256.projectk.compat.wthit;

import com.karasu256.projectk.block.entity.AbyssMagicTableBlockEntity;
import com.karasu256.projectk.energy.IProjectKEnergy;
import com.karasu256.projectk.utils.AbyssMagicTableInfo;
import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public class AbyssMagicTableWthitProvider implements IBlockComponentProvider {
    public static final AbyssMagicTableWthitProvider INSTANCE = new AbyssMagicTableWthitProvider();

    @Override
    public void appendBody(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
        BlockEntity be = accessor.getBlockEntity();
        if (!(be instanceof AbyssMagicTableBlockEntity table)) {
            return;
        }

        IProjectKEnergy energy = table.getEnergyType();
        if (energy != null) {
            tooltip.addLine(Component.translatable("tooltip.projectk.wthit.energy_type", energy.getName()));
        }
        tooltip.addLine(Component.translatable("tooltip.projectk.wthit.energy", table.getAmount(), table.getCapacity()));

        float ratio = AbyssMagicTableInfo.progressRatio(table.getProgress(), table.getMaxProgress());
        ItemStack input = table.getInputItem();
        ItemStack output = table.getOutputItem();
        tooltip.addLine(new AbyssMagicTableRecipeComponent(input, ratio, output));
    }
}
