package com.karasu256.projectk.block.custom;

import com.karasu256.projectk.block.entity.AbyssCoreBlockEntityProvider;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AbyssCore extends BaseEntityBlock {
    public static final MapCodec<AbyssCore> CODEC = simpleCodec(AbyssCore::new);

    public AbyssCore(Properties properties) {
        super(properties);
    }

    @Override
    @NotNull
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return AbyssCoreBlockEntityProvider.create(pos, state);
    }

    @Override
    @NotNull
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    @NotNull
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Block.box(4.0, 0.0, 4.0, 12.0, 8.0, 12.0);
    }

    @Override
    @NotNull
    protected VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
        return Block.box(4.0, 0.0, 4.0, 12.0, 8.0, 12.0);
    }

    @Override
    @NotNull
    protected VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Block.box(4.0, 0.0, 4.0, 12.0, 8.0, 12.0);
    }
}
