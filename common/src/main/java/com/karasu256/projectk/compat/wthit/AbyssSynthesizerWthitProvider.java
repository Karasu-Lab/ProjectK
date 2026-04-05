package com.karasu256.projectk.compat.wthit;

import com.karasu256.projectk.block.entity.AbyssSynthesizerBlockEntity;
import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITooltip;
import net.minecraft.world.level.block.entity.BlockEntity;

public class AbyssSynthesizerWthitProvider extends AbstractProjectKWthitProvider implements IBlockComponentProvider {
    public static final AbyssSynthesizerWthitProvider INSTANCE = new AbyssSynthesizerWthitProvider();

    @Override
    public void appendBody(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
        BlockEntity be = accessor.getBlockEntity();
        if (!(be instanceof AbyssSynthesizerBlockEntity synthesizer)) {
            return;
        }

        addEnergyEntries(tooltip, synthesizer.getEnergyEntries());
        addTierInfo(tooltip, be);
    }
}
