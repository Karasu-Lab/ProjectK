package com.karasu256.projectk.compat.wthit;

import com.karasu256.projectk.block.entity.AbyssMagicTableBlockEntity;
import com.karasu256.projectk.energy.IEnergyListHolder;
import com.karasu256.projectk.utils.AbyssMagicTableInfo;
import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITooltip;
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

        if (table instanceof IEnergyListHolder listHolder) {
            ProjectKEnergyBlockWthitProvider.addEnergyEntries(tooltip, listHolder.getEnergyEntries());
        }

        float ratio = AbyssMagicTableInfo.progressRatio(table.getProgress(), table.getMaxProgress());
        ItemStack input = table.getInputItem();
        ItemStack output = table.getOutputItem();
        tooltip.addLine(new AbyssMagicTableRecipeComponent(input, ratio, output));
    }
}
