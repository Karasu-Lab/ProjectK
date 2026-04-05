package com.karasu256.projectk.compat.wthit;

import com.karasu256.projectk.block.entity.AbyssMagicTableBlockEntity;
import com.karasu256.projectk.block.entity.impl.AbstractPKEnergyBlockEntity;
import com.karasu256.projectk.energy.IEnergyListHolder;
import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITooltip;
import net.karasuniki.karasunikilib.api.IHeldItem;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ProjectKEnergyBlockWthitProvider extends AbstractProjectKWthitProvider implements IBlockComponentProvider {
    public static final ProjectKEnergyBlockWthitProvider INSTANCE = new ProjectKEnergyBlockWthitProvider();

    @Override
    public void appendBody(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
        BlockEntity be = accessor.getBlockEntity();
        if (be instanceof AbyssMagicTableBlockEntity) {
            return;
        }
        if (be instanceof IEnergyListHolder listHolder) {
            addEnergyEntries(tooltip, listHolder.getEnergyEntries());
            addTierInfo(tooltip, be);
            if (be instanceof IHeldItem held) {
                addHeldItem(tooltip, held);
            }
            return;
        }
        if (be instanceof AbstractPKEnergyBlockEntity<?> energyBe) {
            addEnergyInfo(tooltip, energyBe.getEnergyType(), energyBe.getAmount(), energyBe.getCapacity());
            addTierInfo(tooltip, be);
            addHeldItem(tooltip, energyBe);
        }
    }
}
