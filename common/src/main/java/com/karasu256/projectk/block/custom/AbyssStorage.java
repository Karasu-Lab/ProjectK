package com.karasu256.projectk.block.custom;

import com.karasu256.projectk.block.entity.AbyssStorageBlockEntity;
import com.karasu256.projectk.block.entity.ProjectKBlockEntities;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AbyssStorage extends BaseEntityBlock {
    public static final MapCodec<AbyssStorage> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Properties.CODEC.fieldOf("properties").forGetter(AbyssStorage::getProperties)
    ).apply(instance, properties -> new AbyssStorage(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK), properties)));

    private static final VoxelShape SHAPE = box(0.0, 0.0, 0.0, 16.0, 16.0, 16.0);
    private final Properties properties;

    public AbyssStorage(BlockBehaviour.Properties blockProperties, Properties properties) {
        super(blockProperties);
        this.properties = properties;
    }

    @Override
    @NotNull
    protected MapCodec<? extends BaseEntityBlock> codec() {
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
        if (level.isClientSide) return InteractionResult.SUCCESS;

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
            }
            super.onRemove(state, level, pos, newState, movedByPiston);
        }
    }

    @Override
    public @Nullable <T extends BlockEntity> net.minecraft.world.level.block.entity.BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null : createTickerHelper(type, ProjectKBlockEntities.ABYSS_STORAGE.get(), AbyssStorageBlockEntity::tick);
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

    public static class Properties {
        public static final com.mojang.serialization.Codec<Properties> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                com.mojang.serialization.Codec.LONG.fieldOf("capacity").forGetter(Properties::capacity),
                com.mojang.serialization.Codec.INT.fieldOf("max_types").forGetter(Properties::maxTypes)
        ).apply(instance, Properties::new));

        private final long capacity;
        private final int maxTypes;

        private Properties(long capacity, int maxTypes) {
            this.capacity = capacity;
            this.maxTypes = maxTypes;
        }

        public static Properties of() {
            return new Properties(0L, 0);
        }

        public Properties capacity(long capacity) {
            return new Properties(capacity, maxTypes);
        }

        public Properties maxTypes(int maxTypes) {
            return new Properties(capacity, maxTypes);
        }

        public long capacity() {
            return capacity;
        }

        public int maxTypes() {
            return maxTypes;
        }
    }
}
