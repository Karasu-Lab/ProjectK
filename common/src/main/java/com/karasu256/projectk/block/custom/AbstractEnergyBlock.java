package com.karasu256.projectk.block.custom;

import com.karasu256.projectk.registry.AbstractBlockProperties;
import com.karasu256.projectk.registry.ProjectKProperties;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.List;

public abstract class AbstractEnergyBlock extends AbstractContainerBlock {
    private final Properties energyProperties;

    public AbstractEnergyBlock(BlockBehaviour.Properties blockProperties, Properties energyProperties) {
        super(blockProperties, energyProperties);
        this.energyProperties = energyProperties;
    }

    public Properties getEnergyProperties() {
        return energyProperties;
    }

    public static class Properties extends AbstractBlockProperties<Properties> {
        private final long energyCapacity;

        public Properties(long energyCapacity) {
            super();
            this.energyCapacity = energyCapacity;
        }

        private Properties(long energyCapacity, List<TagKey<Block>> tags) {
            super(tags);
            this.energyCapacity = energyCapacity;
        }

        @Override
        public Properties withTags(List<TagKey<Block>> tags) {
            return new Properties(energyCapacity, tags);
        }

        public long energyCapacity() {
            return energyCapacity;
        }
    }
}
