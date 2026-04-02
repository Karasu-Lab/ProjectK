package com.karasu256.projectk.data.spawn;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import java.util.List;

public record BlockCondition(int radius, List<TagKey<Block>> tags, List<ResourceLocation> ids) {
    public static final BlockCondition ANY = new BlockCondition(0, List.of(), List.of());

    public static final Codec<BlockCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.optionalFieldOf("radius", 0).forGetter(BlockCondition::radius),
            Codec.list(TagKey.codec(Registries.BLOCK)).optionalFieldOf("tags", List.of()).forGetter(BlockCondition::tags),
            Codec.list(ResourceLocation.CODEC).optionalFieldOf("ids", List.of()).forGetter(BlockCondition::ids)
    ).apply(instance, BlockCondition::new));

    public boolean matches(Level level, BlockPos center) {
        if (tags.isEmpty() && ids.isEmpty()) {
            return true;
        }
        int r = Math.max(0, radius);
        for (BlockPos pos : BlockPos.betweenClosed(center.offset(-r, -r, -r), center.offset(r, r, r))) {
            var state = level.getBlockState(pos);
            for (TagKey<Block> tag : tags) {
                if (state.is(tag)) {
                    return true;
                }
            }
            ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(state.getBlock());
            if (ids.contains(blockId)) {
                return true;
            }
        }
        return false;
    }
}
