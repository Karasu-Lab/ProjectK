package com.karasu256.projectk.block.entity;

import com.karasu256.karasulab.karasucore.api.IHeldItem;
import com.karasu256.karasulab.karasucore.api.client.model.animation.IRotationAnimSpeed;
import com.karasu256.karasulab.karasucore.api.data.IEnergy;
import com.karasu256.projectk.client.animation.RotationAnimSpeed;
import com.karasu256.karasulab.karasucore.utils.Id;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AbstractEnergyBlockEntity<T extends IEnergy> extends com.karasu256.karasulab.karasucore.api.block.entity.impl.AbstractEnergyBlockEntity<T> implements IRotationAnimSpeed, IHeldItem {
    protected final IRotationAnimSpeed rotationSpeed = new RotationAnimSpeed(1.0f);
    protected ItemStack heldItem = ItemStack.EMPTY;

    public AbstractEnergyBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, long capacity) {
        super(type, pos, state, capacity);
    }

    public IRotationAnimSpeed getRotationAnimSpeed() {
        return rotationSpeed;
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
    public ItemStack getHeldItem() {
        return heldItem;
    }

    @Override
    public void setHeldItem(ItemStack stack) {
        this.heldItem = stack;
        setChanged();
        sync();
    }

    @Override
    public ResourceLocation getNbtId() {
        return Id.id("energy_block_entity");
    }

    @Override
    public long getAmount() {
        return energy;
    }

    @Override
    public void setEnergy(long newValue) {
        this.energy = newValue;
        setChanged();
        sync();
    }

    @Override
    public void readNbt(CompoundTag nbt, HolderLookup.Provider registries) {
        rotationSpeed.readNbt(nbt, registries);
        if (nbt.contains("held_item")) {
            heldItem = ItemStack.parse(registries, nbt.getCompound("held_item")).orElse(ItemStack.EMPTY);
        } else {
            heldItem = ItemStack.EMPTY;
        }
    }

    @Override
    public void writeNbt(CompoundTag nbt, HolderLookup.Provider registries) {
        rotationSpeed.writeNbt(nbt, registries);
        if (!heldItem.isEmpty()) {
            nbt.put("held_item", heldItem.save(registries));
        }
    }

    @Override
    protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        super.saveAdditional(nbt, registries);
        writeNbt(nbt, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        super.loadAdditional(nbt, registries);
        readNbt(nbt, registries);
    }
}
