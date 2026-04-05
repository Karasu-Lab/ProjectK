package com.karasu256.projectk.block.entity.impl;

import com.karasu256.projectk.client.animation.RotationAnimSpeed;
import com.karasu256.projectk.data.AbyssEnergyData;
import com.karasu256.projectk.energy.AbyssEnergy;
import com.karasu256.projectk.energy.IAbyssEnergy;
import com.karasu256.projectk.energy.IProjectKEnergy;
import com.karasu256.projectk.utils.Id;
import net.karasuniki.karasunikilib.api.block.entity.impl.AbstractEnergyBlockEntity;
import net.karasuniki.karasunikilib.api.client.model.animation.IRotationAnimSpeed;
import net.karasuniki.karasunikilib.api.data.impl.EnergyValue;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractPKEnergyBlockEntity<T extends IProjectKEnergy> extends AbstractEnergyBlockEntity<T> implements IRotationAnimSpeed {
    protected final IRotationAnimSpeed rotationSpeed = new RotationAnimSpeed(1.0f);
    protected final T pkEnergy;
    protected long maxEnergyCapacity;

    public AbstractPKEnergyBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, long capacity) {
        super(type, pos, state, capacity);
        this.maxEnergyCapacity = capacity;
        this.pkEnergy = createEnergy();
        this.energy = (EnergyValue) this.pkEnergy;
    }

    protected abstract T createEnergy();

    protected boolean canOutputEnergy() {
        return false;
    }

    public IRotationAnimSpeed getRotationAnimSpeed() {
        return rotationSpeed;
    }

    @Override
    public T getEnergyType() {
        return pkEnergy;
    }

    @Override
    public float getRotationSpeed() {
        return rotationSpeed.getRotationSpeed();
    }

    @Override
    public void setRotationSpeed(float speed) {
        rotationSpeed.setRotationSpeed(speed);
        setChanged();
        sync();
    }

    @Override
    public float getSpeed() {
        return rotationSpeed.getSpeed();
    }

    @Override
    public void setSpeed(float speed) {
        rotationSpeed.setSpeed(speed);
        setChanged();
        sync();
    }

    @Override
    public ResourceLocation getNbtId() {
        return Id.id("energy_block_entity");
    }

    @Override
    public long insert(ResourceLocation id, long maxAmount, boolean simulate) {
        if (!canAcceptEnergy(id)) return 0;
        long capped = Math.min(maxAmount, getMaxEnergyCapacity() - pkEnergy.getValue());
        if (capped <= 0) return 0;
        long inserted = pkEnergy instanceof AbyssEnergy ae
            ? ae.insert(id, capped, getMaxEnergyCapacity(), simulate)
                : pkEnergy.insert(id, capped, simulate);
        if (inserted > 0 && !simulate) {
            setChanged();
            sync();
        }
        return inserted;
    }

    @Override
    public long extract(ResourceLocation id, long maxAmount, boolean simulate) {
        long extracted = pkEnergy.extract(id, maxAmount, simulate);
        if (extracted > 0 && !simulate) {
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
        return getMaxEnergyCapacity();
    }

    protected long getMaxEnergyCapacity() {
        return maxEnergyCapacity;
    }

    protected void setMaxEnergyCapacity(long capacity) {
        this.maxEnergyCapacity = capacity;
        if (energy != null) {
            energy.setCapacity(capacity);
        }
        if (pkEnergy != null && pkEnergy.getValue() > capacity) {
            pkEnergy.setValue(capacity);
            setChanged();
            sync();
        }
    }

    @Override
    public long getAmount() {
        return pkEnergy.getValue();
    }

    @Override
    public void setEnergy(long newValue) {
    }

    protected boolean canAcceptEnergy(@Nullable ResourceLocation id) {
        if (id == null) return false;
        if (pkEnergy instanceof IAbyssEnergy) {
            return IAbyssEnergy.isAbyssEnergyId(id);
        }
        return true;
    }

    @Nullable
    protected ResourceLocation getAbyssEnergyId() {
        if (pkEnergy instanceof IAbyssEnergy) {
            return pkEnergy.getId();
        }
        return null;
    }

    protected void applyAbyssEnergyData(ItemStack stack, long amount) {
        ResourceLocation energyId = getAbyssEnergyId();
        if (energyId == null) {
            return;
        }
        AbyssEnergyData.applyToStack(stack, energyId, amount);
    }

    @Override
    protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        super.saveAdditional(nbt, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        super.loadAdditional(nbt, registries);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag nbt = super.getUpdateTag(registries);
        writeNbt(nbt, registries);
        return nbt;
    }

    @Override
    @Nullable
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void readNbt(CompoundTag nbt, HolderLookup.Provider registries) {
        super.loadAdditional(nbt, registries);
        rotationSpeed.readNbt(nbt, registries);
        if (pkEnergy instanceof AbyssEnergy abyssEnergy) {
            abyssEnergy.readNbt(nbt, registries);
        }
    }

    @Override
    public void writeNbt(CompoundTag nbt, HolderLookup.Provider registries) {
        super.saveAdditional(nbt, registries);
        rotationSpeed.writeNbt(nbt, registries);
        if (pkEnergy instanceof AbyssEnergy abyssEnergy) {
            abyssEnergy.writeNbt(nbt, registries);
        }
    }
}
