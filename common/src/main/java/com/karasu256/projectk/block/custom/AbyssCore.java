package com.karasu256.projectk.block.custom;

import com.karasu256.projectk.block.entity.AbyssCoreBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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
        return new AbyssCoreBlockEntity(pos, state);
    }

    @Override
    @NotNull
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (level.getBlockEntity(pos) instanceof AbyssCoreBlockEntity core) {
            core.loadFromStack(stack);
        }
    }

    @Override
    public @NotNull List<ItemStack> getDrops(BlockState state, net.minecraft.world.level.storage.loot.LootParams.Builder builder) {
        List<ItemStack> drops = super.getDrops(state, builder);
        BlockEntity blockEntity = builder.getOptionalParameter(net.minecraft.world.level.storage.loot.parameters.LootContextParams.BLOCK_ENTITY);
        if (!(blockEntity instanceof AbyssCoreBlockEntity core)) {
            return drops;
        }
        for (ItemStack drop : drops) {
            if (drop.is(asItem())) {
                core.applyDropData(drop);
            }
        }
        return drops;
    }

    @Override
    public ItemStack getCloneItemStack(net.minecraft.world.level.LevelReader level, BlockPos pos, BlockState state) {
        ItemStack stack = super.getCloneItemStack(level, pos, state);
        if (level.getBlockEntity(pos) instanceof AbyssCoreBlockEntity core) {
            core.applyDropData(stack);
        }
        return stack;
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
