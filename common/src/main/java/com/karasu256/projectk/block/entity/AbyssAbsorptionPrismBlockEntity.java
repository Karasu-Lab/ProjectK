package com.karasu256.projectk.block.entity;

import com.karasu256.projectk.block.entity.impl.AbstractAbyssMachineBlockEntity;
import com.karasu256.projectk.data.AbyssEnergyData;
import com.karasu256.projectk.registry.ProjectKMachineCapacities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AbyssAbsorptionPrismBlockEntity extends AbstractAbyssMachineBlockEntity implements ILaserEnergyNbtStorage {

    public AbyssAbsorptionPrismBlockEntity(BlockPos pos, BlockState state) {
        super(ProjectKBlockEntities.ABYSS_ABSORPTION_PRISM.get(), pos, state,
                ProjectKMachineCapacities.ABYSS_ABSORPTION_PRISM, 1);
    }

    public List<AbyssEnergyData> getEnergies() {
        return energies;
    }

    @Nullable
    public ResourceLocation getPrimaryEnergyId() {
        return getAbyssEnergyId();
    }

    @Override
    public void addOrIncrease(ResourceLocation energyId, long amount) {
        insert(energyId, amount, false);
    }

    @Override
    public void readEnergies(CompoundTag nbt, HolderLookup.Provider provider) {
        readEnergyListNbt(nbt);
    }

    @Override
    public void writeEnergies(CompoundTag nbt, HolderLookup.Provider provider) {
        writeEnergyListNbt(nbt);
    }
}
