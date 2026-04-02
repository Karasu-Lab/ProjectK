package com.karasu256.projectk.compat.wthit;

import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.TooltipPosition;
import net.minecraft.world.level.block.Block;

@SuppressWarnings({"unused", "deprecation"})
public class ProjectKWTHITPlugin implements IWailaPlugin {
    @Override
    public void register(IRegistrar registrar) {
        registrar.addComponent(ProjectKEnergyBlockWthitProvider.INSTANCE, TooltipPosition.BODY, Block.class);
        registrar.addComponent(AbyssMagicTableWthitProvider.INSTANCE, TooltipPosition.BODY, Block.class);
    }
}
