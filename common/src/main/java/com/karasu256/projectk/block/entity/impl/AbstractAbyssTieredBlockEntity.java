package com.karasu256.projectk.block.entity.impl;

import com.karasu256.projectk.api.machine.IMachineCapacity;
import com.karasu256.projectk.energy.IMaxEnergyInfo;
import com.karasu256.projectk.energy.ITierInfo;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AbstractAbyssTieredBlockEntity extends AbstractAbyssNbtBlockEntity implements ITierInfo, IMaxEnergyInfo {
    protected int tier;
    protected long maxEnergy;
    protected final RegistrySupplier<? extends IMachineCapacity> capacitySupplier;

    public AbstractAbyssTieredBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, RegistrySupplier<? extends IMachineCapacity> capacitySupplier) {
        super(type, pos, state);
        this.capacitySupplier = capacitySupplier;
        this.tier = getDefaultTier();
        resolveMaxEnergy();
    }

    @Override
    public int getTier() {
        return tier;
    }

    @Override
    public void setTier(int tier) {
        this.tier = clampTier(tier);
        resolveMaxEnergy();
        markDirtyAndSync();
    }

    @Override
    public int getMaxTier() {
        return 1;
    }

    @Override
    public int getDefaultTier() {
        return 1;
    }

    @Override
    public long getMaxEnergy() {
        return maxEnergy;
    }

    @Override
    public void setMaxEnergy(long maxEnergy) {
        this.maxEnergy = maxEnergy;
        markDirtyAndSync();
    }

    protected void resolveMaxEnergy() {
        if (capacitySupplier != null && capacitySupplier.isPresent()) {
            this.maxEnergy = capacitySupplier.get().getCapacity(this.tier);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        super.saveAdditional(nbt, registries);
        saveTier(nbt);
    }

    @Override
    protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        super.loadAdditional(nbt, registries);
        loadTier(nbt);
        resolveMaxEnergy();
    }
}
