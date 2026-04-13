package com.karasu256.projectk.block.custom;

import com.karasu256.projectk.block.entity.AbyssEnchantRemoverBlockEntity;
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
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AbyssEnchantRemover extends BaseEntityBlock {
    public static final MapCodec<AbyssEnchantRemover> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Properties.CODEC.fieldOf("properties").forGetter(AbyssEnchantRemover::getProperties)).apply(instance,
            properties -> new AbyssEnchantRemover(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK),
                    properties)));

    private static final VoxelShape SHAPE = box(0.0, 0.0, 0.0, 16.0, 16.0, 16.0);
    private final Properties properties;

    public AbyssEnchantRemover(BlockBehaviour.Properties blockProperties, Properties properties) {
        super(blockProperties);
        this.properties = properties;
    }

    @Override
    @NotNull
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    @NotNull
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AbyssEnchantRemoverBlockEntity(pos, state);
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
        if (be instanceof AbyssEnchantRemoverBlockEntity remover) {
            player.openMenu(remover);
            return InteractionResult.SUCCESS;
        }
        return super.useWithoutItem(state, level, pos, player, hitResult);
    }

    @Override
    public void onRemove(@NotNull BlockState state, Level level, BlockPos pos, @NotNull BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock())) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof AbyssEnchantRemoverBlockEntity remover) {
                Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), remover.getInputItem());
                Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), remover.getBookItem());
            }
            super.onRemove(state, level, pos, newState, movedByPiston);
        }
    }

    @Override
    public @Nullable <T extends BlockEntity> net.minecraft.world.level.block.entity.BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return null;
    }

    public long getDefaultBookCapacity() {
        return properties.defaultBookCapacity();
    }

    private Properties getProperties() {
        return properties;
    }

    public static class Properties {
        public static final com.mojang.serialization.Codec<Properties> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(com.mojang.serialization.Codec.LONG.fieldOf("default_book_capacity")
                        .forGetter(Properties::defaultBookCapacity)).apply(instance, Properties::new));

        private final long defaultBookCapacity;

        private Properties(long defaultBookCapacity) {
            this.defaultBookCapacity = defaultBookCapacity;
        }

        public static Properties of() {
            return new Properties(30000L);
        }

        public Properties defaultBookCapacity(long capacity) {
            return new Properties(capacity);
        }

        public long defaultBookCapacity() {
            return defaultBookCapacity;
        }
    }
}
