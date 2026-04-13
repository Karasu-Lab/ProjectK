package com.karasu256.projectk.block.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

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
        super(properties);
        this.customProperties = customProperties;
    }

    public CustomProperties getCustomProperties() {
        return customProperties;
    }

    public static class CustomProperties implements ITieredMachineProperties {
        public static final Codec<CustomProperties> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(Codec.LONG.fieldOf("capacity").forGetter(CustomProperties::capacity),
                                Codec.LONG.fieldOf("transferRate").forGetter(CustomProperties::transferRate),
                                Codec.INT.optionalFieldOf("maxTier", 3).forGetter(CustomProperties::maxTier),
                                Codec.INT.optionalFieldOf("defaultTier", 1).forGetter(CustomProperties::defaultTier))
                        .apply(instance, CustomProperties::new));

        private final long capacity;
        private final long transferRate;
        private final int maxTier;
        private final int defaultTier;

        private CustomProperties(long capacity, long transferRate) {
            this(capacity, transferRate, 3, 1);
        }

        private CustomProperties(long capacity, long transferRate, int maxTier, int defaultTier) {
            this.capacity = capacity;
            this.transferRate = transferRate;
            this.maxTier = maxTier;
            this.defaultTier = defaultTier;
        }

        public static CustomProperties of() {
            return new CustomProperties(0L, 0L);
        }

        public CustomProperties capacity(long capacity) {
            return new CustomProperties(capacity, transferRate, maxTier, defaultTier);
        }

        public CustomProperties transferRate(long transferRate) {
            return new CustomProperties(capacity, transferRate, maxTier, defaultTier);
        }

        public CustomProperties maxTier(int maxTier) {
            return new CustomProperties(capacity, transferRate, maxTier, defaultTier);
        }

        public CustomProperties defaultTier(int defaultTier) {
            return new CustomProperties(capacity, transferRate, maxTier, defaultTier);
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
