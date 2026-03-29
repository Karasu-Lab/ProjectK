package com.karasu256.projectk.block.custom;

import net.minecraft.world.level.block.state.BlockBehaviour;

public abstract class AbstractEnergyBlock extends AbstractContainerBlock {
    public static class Properties {
        private final long energyCapacity;

        public Properties(long energyCapacity) {
            this.energyCapacity = energyCapacity;
        }

        public long getEnergyCapacity() {
            return energyCapacity;
        }
    }

    private final Properties energyProperties;

    public AbstractEnergyBlock(BlockBehaviour.Properties blockProperties, Properties energyProperties) {
        super(blockProperties);
        this.energyProperties = energyProperties;
    }

    public Properties getEnergyProperties() {
        return energyProperties;
    }
}
