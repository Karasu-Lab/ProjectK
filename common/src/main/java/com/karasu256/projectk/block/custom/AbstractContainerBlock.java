package com.karasu256.projectk.block.custom;

import com.karasu256.projectk.block.entity.AbstractContainerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractContainerBlock extends AbstractAnimatableBlock {
    public AbstractContainerBlock(Properties properties) {
        super(properties);
    }

    @Override
    @NotNull
    protected InteractionResult useWithoutItem(BlockState state, @NotNull Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.isClientSide) return InteractionResult.SUCCESS;

        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof AbstractContainerBlockEntity containerBe) {
            ItemStack heldItem = containerBe.getHeldItem();
            ItemStack playerItem = player.getMainHandItem();

            if (heldItem.isEmpty()) {
                if (!playerItem.isEmpty()) {
                    containerBe.setHeldItem(playerItem.split(1));
                    return InteractionResult.SUCCESS;
                }
            } else {
                if (player.getInventory().add(heldItem)) {
                    containerBe.setHeldItem(ItemStack.EMPTY);
                } else {
                    player.drop(heldItem, false);
                    containerBe.setHeldItem(ItemStack.EMPTY);
                }
                return InteractionResult.SUCCESS;
            }
        }
        return super.useWithoutItem(state, level, pos, player, hitResult);
    }

    @Override
    public void onRemove(@NotNull BlockState state, Level level, BlockPos pos, @NotNull BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock())) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof AbstractContainerBlockEntity containerBe) {
                Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), containerBe.getHeldItem());
            }
            super.onRemove(state, level, pos, newState, movedByPiston);
        }
    }
}
