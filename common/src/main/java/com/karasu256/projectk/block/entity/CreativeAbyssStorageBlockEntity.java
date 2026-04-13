package com.karasu256.projectk.block.entity;

import com.karasu256.projectk.data.AbyssEnergyData;
import com.karasu256.projectk.registry.ProjectKMachineCapacities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CreativeAbyssStorageBlockEntity extends AbyssStorageBlockEntity {
    public CreativeAbyssStorageBlockEntity(BlockPos pos, BlockState state) {
        super(ProjectKBlockEntities.CREATIVE_ABYSS_STORAGE.get(), pos, state, ProjectKMachineCapacities.INFINITIE);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, CreativeAbyssStorageBlockEntity be) {
        if (level.isClientSide)
            return;
        be.serverTick();
    }

    @Override
    public List<EnergyEntry> getEnergyEntries() {
        if (!getEnergyList().isEmpty() && getActiveEnergyIndex() < 0) {
            setActiveEnergyIndex(0);
        }
        return super.getEnergyEntries();
    }

    @Override
    public long insert(ResourceLocation id, long maxAmount, boolean simulate) {
        if (id == null || maxAmount <= 0)
            return 0;

        if (getEnergyTypeCount() >= 1 && findEnergyIndex(id) < 0)
            return 0;

        if (!simulate) {
            if (findEnergyIndex(id) < 0) {
                getEnergyList().add(new AbyssEnergyData(id, Long.MAX_VALUE));
                if (getActiveEnergyIndex() < 0) {
                    setActiveEnergyIndex(0);
                }
            }
            markDirtyAndSync();
        }
        return maxAmount;
    }

    @Override
    public long extract(ResourceLocation id, long maxAmount, boolean simulate) {
        if (id == null || maxAmount <= 0)
            return 0;

        if (findEnergyIndex(id) >= 0) {
            return maxAmount;
        }
        return 0;
    }

    @Override
    public long extract(ResourceLocation id, long maxAmount, boolean simulate, @Nullable Direction side) {
        return extract(id, maxAmount, simulate);
    }

    @Override
    public int getMaxEnergyTypes() {
        return 1;
    }

    @Override
    public boolean upgradeTier() {
        return false;
    }

    @Override
    public int getMaxTier() {
        return getDefaultTier();
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.projectk.creative_abyss_storage");
    }
}
