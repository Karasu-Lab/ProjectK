package com.karasu256.projectk.block.entity;

import com.karasu256.projectk.energy.AbyssEnergy;
import com.karasu256.projectk.energy.IAbyssEnergy;
import com.karasu256.projectk.registry.BlockEntitiesRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AbyssGeneratorBlockEntity extends BlockEntity implements IAbyssEnergy {
    private long energy;
    private final long capacity = 10000;
    private static final String ENERGY_TAG = "Energy";
    private static final String ITEM_TAG = "HeldItem";

    private ItemStack heldItem = ItemStack.EMPTY;
    private float rotationSpeed = 1.0f;

    public AbyssGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntitiesRegistry.ABYSS_GENERATOR.get(), pos, state);
    }

    public ItemStack getHeldItem() {
        return heldItem;
    }

    public void setHeldItem(ItemStack stack) {
        this.heldItem = stack;
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public float getRotationSpeed() {
        return rotationSpeed;
    }

    public void setRotationSpeed(float speed) {
        this.rotationSpeed = speed;
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    public long insert(long maxAmount, boolean simulate) {
        long received = Math.min(capacity - energy, maxAmount);
        if (!simulate) {
            energy += received;
            setChanged();
        }
        return received;
    }

    @Override
    public long extract(long maxAmount, boolean simulate) {
        long extracted = Math.min(energy, maxAmount);
        if (!simulate) {
            energy -= extracted;
            setChanged();
        }
        return extracted;
    }

    @Override
    public long getAmount() {
        return energy;
    }

    @Override
    public long getCapacity() {
        return capacity;
    }

    @Override
    public AbyssEnergy getAbyssEnergy() {
        return new AbyssEnergy(energy);
    }

    @Override
    protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        super.saveAdditional(nbt, registries);
        nbt.putLong(ENERGY_TAG, energy);
        if (!heldItem.isEmpty()) {
            nbt.put(ITEM_TAG, heldItem.save(registries));
        }
        nbt.putFloat("RotationSpeed", rotationSpeed);
    }

    @Override
    protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        super.loadAdditional(nbt, registries);
        energy = nbt.getLong(ENERGY_TAG);
        if (nbt.contains(ITEM_TAG)) {
            heldItem = ItemStack.parse(registries, nbt.getCompound(ITEM_TAG)).orElse(ItemStack.EMPTY);
        } else {
            heldItem = ItemStack.EMPTY;
        }
        if (nbt.contains("RotationSpeed")) {
            rotationSpeed = nbt.getFloat("RotationSpeed");
        }
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }
}
