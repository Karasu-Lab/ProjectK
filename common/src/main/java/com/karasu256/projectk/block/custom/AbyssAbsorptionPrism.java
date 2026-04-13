package com.karasu256.projectk.block.custom;

import com.karasu256.projectk.block.entity.AbyssAbsorptionPrismBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AbyssAbsorptionPrism extends Block implements EntityBlock, ILaserEnergyReactiveBlock {
    public AbyssAbsorptionPrism(Properties properties) {
        super(properties);
    }

    @Override
    public void onLaserHit(Level level, BlockPos pos, BlockState state, net.minecraft.resources.ResourceLocation energyId, long amount) {
        if (level.getBlockEntity(pos) instanceof AbyssAbsorptionPrismBlockEntity prism) {
            prism.addOrIncrease(energyId, amount);
        }
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AbyssAbsorptionPrismBlockEntity(pos, state);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof AbyssAbsorptionPrismBlockEntity prism) {
            prism.loadFromStack(stack);
        }
    }

    @Override
    public @NotNull List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        List<ItemStack> drops = super.getDrops(state, builder);
        BlockEntity blockEntity = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (!(blockEntity instanceof AbyssAbsorptionPrismBlockEntity prism) || prism.getPrimaryEnergyId() == null) {
            return drops;
        }
        for (ItemStack drop : drops) {
            if (drop.is(asItem())) {
                prism.applyDropData(drop);
            }
        }
        return drops;
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        ItemStack stack = super.getCloneItemStack(level, pos, state);
        if (level.getBlockEntity(pos) instanceof AbyssAbsorptionPrismBlockEntity prism) {
            prism.applyDropData(stack);
        }
        return stack;
    }
}
