package com.karasu256.projectk.block.entity.impl;

import com.karasu256.projectk.data.AbyssEnergyData;
import com.karasu256.projectk.energy.IMultiEnergyStorage;
import net.karasuniki.karasunikilib.api.block.ICableInputable;
import net.karasuniki.karasunikilib.api.block.ICableOutputable;
import net.karasuniki.karasunikilib.api.block.entity.impl.KarasuCoreBlockEntity;
import net.karasuniki.karasunikilib.api.data.ICapacity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractAbyssEnergyMachineBlockEntity extends KarasuCoreBlockEntity implements ICableInputable, ICableOutputable, ICapacity, IMultiEnergyStorage {
    protected final List<AbyssEnergyData> energies = new ArrayList<>();
    protected long capacity;

    protected AbstractAbyssEnergyMachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, long capacity) {
        super(type, pos, state);
        this.capacity = capacity;
    }

    protected int maxEnergyTypes() {
        return 1;
    }

    protected boolean canOutputEnergy() {
        return false;
    }

    @Override
    public List<AbyssEnergyData> getEnergyList() {
        return energies;
    }

    @Override
    public long getEnergyCapacity() {
        return capacity;
    }

    @Override
    public int getMaxEnergyTypes() {
        return maxEnergyTypes();
    }

    @Override
    public long insert(ResourceLocation id, long maxAmount, boolean simulate) {
        if (id == null || maxAmount <= 0) {
            return 0;
        }
        int index = findEnergyIndex(id);
        if (index < 0 && getEnergyTypeCount() >= maxEnergyTypes()) {
            return 0;
        }
        long current = index >= 0 ? energies.get(index).amountOrZero() : 0L;
        long received = Math.min(capacity - current, maxAmount);
        if (received <= 0) {
            return 0;
        }
        if (!simulate) {
            long nextAmount = current + received;
            if (index >= 0) {
                energies.set(index, new AbyssEnergyData(id, nextAmount));
            } else {
                energies.add(new AbyssEnergyData(id, nextAmount));
            }
            setChanged();
            sync();
        }
        return received;
    }

    @Override
    public long extract(ResourceLocation id, long maxAmount, boolean simulate) {
        if (id == null || maxAmount <= 0) {
            return 0;
        }
        int index = findEnergyIndex(id);
        if (index < 0) {
            return 0;
        }
        long current = energies.get(index).amountOrZero();
        long extracted = Math.min(current, maxAmount);
        if (extracted <= 0) {
            return 0;
        }
        if (!simulate) {
            long remaining = current - extracted;
            if (remaining <= 0) {
                energies.remove(index);
            } else {
                energies.set(index, new AbyssEnergyData(id, remaining));
            }
            setChanged();
            sync();
        }
        return extracted;
    }

    @Override
    public long extract(ResourceLocation id, long maxAmount, boolean simulate, @Nullable Direction side) {
        if (side != null && !canOutputEnergy()) {
            return 0;
        }
        return extract(id, maxAmount, simulate);
    }

    @Override
    public long getCapacity() {
        return capacity;
    }

    public ResourceLocation getEnergyId1() {
        AbyssEnergyData data = getEnergyByIndex(0);
        return data == null || !data.hasPositiveAmount() ? null : data.energyId();
    }

    public ResourceLocation getEnergyId2() {
        AbyssEnergyData data = getEnergyByIndex(1);
        return data == null || !data.hasPositiveAmount() ? null : data.energyId();
    }

    public long getEnergyAmount1() {
        AbyssEnergyData data = getEnergyByIndex(0);
        return data == null ? 0L : data.amountOrZero();
    }

    public long getEnergyAmount2() {
        AbyssEnergyData data = getEnergyByIndex(1);
        return data == null ? 0L : data.amountOrZero();
    }

    protected long getEnergyAmount(ResourceLocation id) {
        int index = findEnergyIndex(id);
        if (index < 0) {
            return 0L;
        }
        return energies.get(index).amountOrZero();
    }

    protected void writeEnergyNbt(CompoundTag nbt, HolderLookup.Provider registries) {
        writeEnergyListNbt(nbt);
    }

    protected void readEnergyNbt(CompoundTag nbt, HolderLookup.Provider registries) {
        readEnergyListNbt(nbt);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag nbt = super.getUpdateTag(registries);
        saveAdditional(nbt, registries);
        return nbt;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
