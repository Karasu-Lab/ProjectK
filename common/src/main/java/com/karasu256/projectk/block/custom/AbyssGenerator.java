package com.karasu256.projectk.block.custom;

import com.karasu256.projectk.block.entity.AbyssGeneratorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AbyssGenerator extends AbstractGeneratorBlock {
    public AbyssGenerator() {
        super(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE));
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AbyssGeneratorBlockEntity(pos, state);
    }

    @Override
    @NotNull
    protected InteractionResult useWithoutItem(BlockState state, @NotNull Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.isClientSide) return InteractionResult.SUCCESS;

        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof AbyssGeneratorBlockEntity generatorBe) {
            ItemStack heldItem = generatorBe.getHeldItem();
            ItemStack playerItem = player.getMainHandItem();

            if (heldItem.isEmpty()) {
                if (!playerItem.isEmpty()) {
                    generatorBe.setHeldItem(playerItem.split(1));
                    return InteractionResult.SUCCESS;
                }
            } else {
                if (player.getInventory().add(heldItem)) {
                    generatorBe.setHeldItem(ItemStack.EMPTY);
                } else {
                    player.drop(heldItem, false);
                    generatorBe.setHeldItem(ItemStack.EMPTY);
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
            if (be instanceof AbyssGeneratorBlockEntity generatorBe) {
                Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), generatorBe.getHeldItem());
            }
            super.onRemove(state, level, pos, newState, movedByPiston);
        }
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return super.getTicker(level, state, blockEntityType);
    }
}
