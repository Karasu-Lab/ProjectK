package com.karasu256.projectk.block.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class ProjectKBlock extends AbstractProjectKBlock {
    public ProjectKBlock() {
        super(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE));
    }

    public ProjectKBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    public static class CustomProperties {
        public static final Codec<CustomProperties> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.LONG.fieldOf("capacity").forGetter(CustomProperties::capacity)
        ).apply(instance, CustomProperties::new));

        private final long capacity;

        private CustomProperties(long capacity) {
            this.capacity = capacity;
        }

        public static CustomProperties of() {
            return new CustomProperties(0L);
        }

        public CustomProperties capacity(long capacity) {
            return new CustomProperties(capacity);
        }

        public long capacity() {
            return capacity;
        }
    }
}
