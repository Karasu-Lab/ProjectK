package com.karasu256.projectk.block.custom;

import net.minecraft.world.level.block.state.BlockBehaviour;

public abstract class AbstractEnergyBlock extends AbstractContainerBlock {
    private final Properties energyProperties;

    public AbstractEnergyBlock(BlockBehaviour.Properties blockProperties, Properties energyProperties) {
        super(blockProperties);
        this.energyProperties = energyProperties;
    }

    public Properties getEnergyProperties() {
        return energyProperties;
    }

    public record Properties(long energyCapacity) {
    }
}
