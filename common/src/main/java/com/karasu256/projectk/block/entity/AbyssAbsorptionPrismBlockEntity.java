package com.karasu256.projectk.block.entity;

import com.karasu256.projectk.block.entity.impl.AbstractAbyssMachineBlockEntity;
import com.karasu256.projectk.data.AbyssEnergyData;
import com.karasu256.projectk.data.ProjectKDataComponets;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AbyssAbsorptionPrismBlockEntity extends AbstractAbyssMachineBlockEntity implements ILaserEnergyNbtStorage {

    public AbyssAbsorptionPrismBlockEntity(BlockPos pos, BlockState state) {
        super(ProjectKBlockEntities.ABYSS_ABSORPTION_PRISM.get(), pos, state, 1000L, 1);
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

    public void loadFromStack(ItemStack stack) {
        energies.clear();
        AbyssEnergyData data = stack.get(ProjectKDataComponets.ABYSS_ENERGY_DATA_COMPONENT_TYPE.get());
        if (data != null && data.energyId() != null) {
            energies.add(data);
        }
        List<AbyssEnergyData> list = AbyssEnergyData.readEnergyList(stack);
        for (AbyssEnergyData d : list) {
            if (energies.stream().noneMatch(e -> e.energyId().equals(d.energyId()))) {
                energies.add(d);
            }
        }
        markDirtyAndSync();
    }

    public void applyDropData(ItemStack stack) {
        if (energies.isEmpty()) {
            return;
        }
        for (AbyssEnergyData data : energies) {
            AbyssEnergyData.applyToStack(stack, data.energyId(), data.amount());
        }
    }
}
