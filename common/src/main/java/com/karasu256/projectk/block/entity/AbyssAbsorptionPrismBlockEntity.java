package com.karasu256.projectk.block.entity;

import com.karasu256.projectk.data.AbyssEnergyData;
import com.karasu256.projectk.data.ProjectKDataComponets;
import com.karasu256.projectk.energy.EnergyKeys;
import com.karasu256.projectk.energy.IEnergyListHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class AbyssAbsorptionPrismBlockEntity extends BlockEntity implements ILaserEnergyNbtStorage, IEnergyListHolder {
    private final List<AbyssEnergyData> energies = new ArrayList<>();

    public AbyssAbsorptionPrismBlockEntity(BlockPos pos, BlockState state) {
        super(ProjectKBlockEntities.ABYSS_ABSORPTION_PRISM.get(), pos, state);
    }

    public List<AbyssEnergyData> getEnergies() {
        return energies;
    }

    @Nullable
    public ResourceLocation getPrimaryEnergyId() {
        return energies.isEmpty() ? null : energies.getFirst().energyId();
    }

    @Override
    public List<EnergyEntry> getEnergyEntries() {
        List<EnergyEntry> entries = new ArrayList<>();
        for (AbyssEnergyData data : energies) {
            if (data.hasPositiveAmount()) {
                entries.add(new EnergyEntry(data.energyId(), data.amountOrZero(), null, false));
            }
        }
        return entries;
    }

    @Override
    public void addOrIncrease(ResourceLocation energyId, long amount) {
        int index = -1;
        for (int i = 0; i < energies.size(); i++) {
            if (energies.get(i).energyId().equals(energyId)) {
                index = i;
                break;
            }
        }

        if (index >= 0) {
            AbyssEnergyData existing = energies.get(index);
            energies.set(index, new AbyssEnergyData(energyId, existing.amountOrZero() + amount));
        } else {
            energies.add(new AbyssEnergyData(energyId, amount));
        }
        setChanged();
        sync();
    }

    private void sync() {
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public void loadFromStack(ItemStack stack) {
        energies.clear();
        AbyssEnergyData data = stack.get(ProjectKDataComponets.ABYSS_ENERGY_DATA_COMPONENT_TYPE.get());
        if (data != null && data.energyId() != null) {
            energies.add(data);
        }
        List<AbyssEnergyData> list = AbyssEnergyData.readEnergyList(stack);
        for (AbyssEnergyData d : list) {
            if (!energies.contains(d)) {
                energies.add(d);
            }
        }
        setChanged();
    }

    public void applyDropData(ItemStack stack) {
        if (energies.isEmpty()) {
            return;
        }
        for (AbyssEnergyData data : energies) {
            AbyssEnergyData.applyToStack(stack, data.energyId(), data.amount());
        }
    }

    @Override
    public void readEnergies(CompoundTag nbt, HolderLookup.Provider provider) {
        energies.clear();
        String key = EnergyKeys.ENERGY_LIST.toString();
        if (nbt.contains(key)) {
            ListTag listTag = nbt.getList(key, Tag.TAG_COMPOUND);
            for (int i = 0; i < listTag.size(); i++) {
                AbyssEnergyData.CODEC.parse(NbtOps.INSTANCE, listTag.getCompound(i)).result().ifPresent(energies::add);
            }
        }
    }

    @Override
    public void writeEnergies(CompoundTag nbt, HolderLookup.Provider provider) {
        if (!energies.isEmpty()) {
            ListTag listTag = new ListTag();
            for (AbyssEnergyData data : energies) {
                AbyssEnergyData.CODEC.encodeStart(NbtOps.INSTANCE, data).result().ifPresent(listTag::add);
            }
            nbt.put(EnergyKeys.ENERGY_LIST.toString(), listTag);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        super.saveAdditional(nbt, registries);
        writeEnergies(nbt, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        super.loadAdditional(nbt, registries);
        readEnergies(nbt, registries);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        writeEnergies(tag, registries);
        return tag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
