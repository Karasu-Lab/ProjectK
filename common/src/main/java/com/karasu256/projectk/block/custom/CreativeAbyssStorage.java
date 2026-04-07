package com.karasu256.projectk.block.custom;

import com.karasu256.projectk.block.entity.CreativeAbyssStorageBlockEntity;
import com.karasu256.projectk.block.entity.ProjectKBlockEntities;
import com.karasu256.projectk.data.AbyssEnergyData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CreativeAbyssStorage extends AbyssStorage {
    public CreativeAbyssStorage(BlockBehaviour.Properties blockProperties, AbyssStorage.Properties properties) {
        super(blockProperties, properties);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CreativeAbyssStorageBlockEntity(pos, state);
    }

    @Override
    @NotNull
    protected InteractionResult useWithoutItem(BlockState state, @NotNull Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.isClientSide)
            return InteractionResult.SUCCESS;

        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof CreativeAbyssStorageBlockEntity storage) {
            player.openMenu(storage);
            return InteractionResult.SUCCESS;
        }
        return super.useWithoutItem(state, level, pos, player, hitResult);
    }

    @Override
    public void setPlacedBy(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, @Nullable LivingEntity placer, @NotNull ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (level.isClientSide)
            return;
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof CreativeAbyssStorageBlockEntity storage) {
            storage.readEnergyListNbt(stack.getOrDefault(DataComponents.BLOCK_ENTITY_DATA, CustomData.EMPTY).copyTag());
            List<AbyssEnergyData> entries = AbyssEnergyData.readEnergyList(stack);
            for (AbyssEnergyData data : entries) {
                storage.insert(data.energyId(), data.amountOrZero(), false);
            }
            storage.setChanged();
            storage.sync();
        }
    }

    @Override
    public @Nullable <T extends BlockEntity> net.minecraft.world.level.block.entity.BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null : createTickerHelper(type, ProjectKBlockEntities.CREATIVE_ABYSS_STORAGE.get(),
                CreativeAbyssStorageBlockEntity::tick);
    }
}
