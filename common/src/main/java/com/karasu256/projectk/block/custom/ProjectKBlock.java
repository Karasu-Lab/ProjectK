package com.karasu256.projectk.block.custom;

import com.karasu256.projectk.registry.AbstractBlockProperties;
import com.karasu256.projectk.registry.IProjectKPropertiesProvider;
import com.karasu256.projectk.registry.ProjectKProperties;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
 
import java.util.ArrayList;
import java.util.List;

public class ProjectKBlock extends AbstractProjectKBlock {
    public interface ITieredMachineProperties {
        long capacity();

        int maxTier();

        int defaultTier();
    }

    private final CustomProperties customProperties;

    public ProjectKBlock() {
        this(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE), CustomProperties.of());
    }

    public ProjectKBlock(BlockBehaviour.Properties properties) {
        this(properties, CustomProperties.of());
    }

    public ProjectKBlock(BlockBehaviour.Properties properties, CustomProperties customProperties) {
        super(properties, customProperties);
        this.customProperties = customProperties;
    }

    public CustomProperties getCustomProperties() {
        return customProperties;
    }


    public static class CustomProperties extends AbstractBlockProperties<CustomProperties> implements ITieredMachineProperties {
        public static final Codec<CustomProperties> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(Codec.LONG.fieldOf("capacity").forGetter(CustomProperties::capacity),
                                Codec.LONG.fieldOf("transferRate").forGetter(CustomProperties::transferRate),
                                Codec.INT.optionalFieldOf("maxTier", 3).forGetter(CustomProperties::maxTier),
                                Codec.INT.optionalFieldOf("defaultTier", 1).forGetter(CustomProperties::defaultTier))
                        .apply(instance, (capacity, transferRate, maxTier, defaultTier) -> new CustomProperties(capacity, transferRate, maxTier, defaultTier, new ArrayList<>())));
 
        private final long capacity;
        private final long transferRate;
        private final int maxTier;
        private final int defaultTier;

        private CustomProperties(long capacity, long transferRate) {
            this(capacity, transferRate, 3, 1, new ArrayList<>());
        }

        private CustomProperties(long capacity, long transferRate, int maxTier, int defaultTier, List<TagKey<Block>> tags) {
            super(tags);
            this.capacity = capacity;
            this.transferRate = transferRate;
            this.maxTier = maxTier;
            this.defaultTier = defaultTier;
        }

        public static CustomProperties of() {
            return new CustomProperties(0L, 0L);
        }

        public CustomProperties capacity(long capacity) {
            return new CustomProperties(capacity, transferRate, maxTier, defaultTier, tags);
        }

        public CustomProperties transferRate(long transferRate) {
            return new CustomProperties(capacity, transferRate, maxTier, defaultTier, tags);
        }

        public CustomProperties maxTier(int maxTier) {
            return new CustomProperties(capacity, transferRate, maxTier, defaultTier, tags);
        }

        public CustomProperties defaultTier(int defaultTier) {
            return new CustomProperties(capacity, transferRate, maxTier, defaultTier, tags);
        }

        @Override
        public CustomProperties withTags(List<TagKey<Block>> tags) {
            return new CustomProperties(capacity, transferRate, maxTier, defaultTier, tags);
        }

        public long capacity() {
            return capacity;
        }

        public long transferRate() {
            return transferRate;
        }

        public int maxTier() {
            return maxTier;
        }

        public int defaultTier() {
            return defaultTier;
        }
    }
}
