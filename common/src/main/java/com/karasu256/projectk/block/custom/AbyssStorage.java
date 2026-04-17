package com.karasu256.projectk.block.custom;

import com.karasu256.projectk.block.custom.ProjectKBlock.ITieredMachineProperties;
import com.karasu256.projectk.block.entity.AbyssStorageBlockEntity;
import com.karasu256.projectk.block.entity.ProjectKBlockEntities;
import com.karasu256.projectk.energy.ITierInfo;
import com.karasu256.projectk.item.ProjectKItems;
import com.karasu256.projectk.registry.AbstractBlockProperties;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class AbyssStorage extends AbstractGeneratorBlock {
    public static final MapCodec<AbyssStorage> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(Properties.CODEC.fieldOf("properties").forGetter(AbyssStorage::getProperties))
                    .apply(instance,
                            properties -> new AbyssStorage(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK),
                                    properties)));

    private static final VoxelShape SHAPE = box(0.0, 0.0, 0.0, 16.0, 16.0, 16.0);
    private final Properties properties;

    public AbyssStorage(BlockBehaviour.Properties blockProperties, Properties properties) {
        super(blockProperties, properties);
        this.properties = properties;
    }

    @Override
    @NotNull
    protected MapCodec<? extends AbstractGeneratorBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AbyssStorageBlockEntity(pos, state);
    }

    @Override
    protected boolean useShapeForLightOcclusion(BlockState state) {
        return true;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    @NotNull
    protected InteractionResult useWithoutItem(BlockState state, @NotNull Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.isClientSide)
            return InteractionResult.SUCCESS;

        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof AbyssStorageBlockEntity storage) {
            player.openMenu(storage);
            return InteractionResult.SUCCESS;
        }
        return super.useWithoutItem(state, level, pos, player, hitResult);
    }

    @Override
    public void onRemove(@NotNull BlockState state, Level level, BlockPos pos, @NotNull BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock())) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof AbyssStorageBlockEntity storage) {
                Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), storage.getInputItem());
                dropTierUpgrades(level, pos, storage);
            }
            super.onRemove(state, level, pos, newState, movedByPiston);
        }
    }

    private void dropTierUpgrades(Level level, BlockPos pos, ITierInfo tierInfo) {
        int count = Math.max(0, tierInfo.getTier() - tierInfo.getDefaultTier());
        for (int i = 0; i < count; i++) {
            Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(),
                    ProjectKItems.TIER_UPGRADE.get().getDefaultInstance());
        }
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null : createTickerHelper(type, ProjectKBlockEntities.ABYSS_STORAGE.get(),
                AbyssStorageBlockEntity::tick);
    }

    public long getCapacity() {
        return properties.capacity();
    }

    public int getMaxTypes() {
        return properties.maxTypes();
    }

    private Properties getProperties() {
        return properties;
    }

    public ITieredMachineProperties getTieredProperties() {
        return properties;
    }

    public static class Properties extends AbstractBlockProperties<Properties> implements ITieredMachineProperties {
        public static final Codec<Properties> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(Codec.LONG.fieldOf("capacity").forGetter(Properties::capacity),
                                Codec.INT.fieldOf("max_types").forGetter(Properties::maxTypes),
                                Codec.DOUBLE.optionalFieldOf("capacityScaling", 2.5).forGetter(Properties::capacityScaling),
                                Codec.INT.optionalFieldOf("maxTier", 3).forGetter(Properties::maxTier),
                                Codec.INT.optionalFieldOf("defaultTier", 1).forGetter(Properties::defaultTier))
                        .apply(instance, (capacity, maxTypes, capacityScaling, maxTier, defaultTier) -> new Properties(capacity, maxTypes, capacityScaling, maxTier, defaultTier, new ArrayList<>())));

        private final long capacity;
        private final int maxTypes;
        private final double capacityScaling;
        private final int maxTier;
        private final int defaultTier;
 
        private Properties(long capacity, int maxTypes) {
            this(capacity, maxTypes, 2.5, 3, 1, new ArrayList<>());
        }

        private Properties(long capacity, int maxTypes, double capacityScaling, int maxTier, int defaultTier, List<TagKey<Block>> tags) {
            super(tags);
            this.capacity = capacity;
            this.maxTypes = maxTypes;
            this.capacityScaling = capacityScaling;
            this.maxTier = maxTier;
            this.defaultTier = defaultTier;
        }

        public static Properties of() {
            return new Properties(0L, 0);
        }

        public Properties capacity(long capacity) {
            return new Properties(capacity, maxTypes, capacityScaling, maxTier, defaultTier, tags);
        }
 
        public Properties maxTypes(int maxTypes) {
            return new Properties(capacity, maxTypes, capacityScaling, maxTier, defaultTier, tags);
        }
 
        public Properties capacityScaling(double capacityScaling) {
            return new Properties(capacity, maxTypes, capacityScaling, maxTier, defaultTier, tags);
        }
 
        public Properties maxTier(int maxTier) {
            return new Properties(capacity, maxTypes, capacityScaling, maxTier, defaultTier, tags);
        }
 
        public Properties defaultTier(int defaultTier) {
            return new Properties(capacity, maxTypes, capacityScaling, maxTier, defaultTier, tags);
        }

        @Override
        public Properties withTags(List<TagKey<Block>> tags) {
            return new Properties(capacity, maxTypes, capacityScaling, maxTier, defaultTier, tags);
        }

        public long capacity() {
            return capacity;
        }

        public int maxTypes() {
            return maxTypes;
        }

        public double capacityScaling() {
            return capacityScaling;
        }

        public int maxTier() {
            return maxTier;
        }

        public int defaultTier() {
            return defaultTier;
        }
    }
}
