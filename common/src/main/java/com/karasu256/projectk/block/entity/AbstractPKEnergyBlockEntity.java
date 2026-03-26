package com.karasu256.projectk.block.entity;

import com.karasu256.karasulab.karasucore.api.block.entity.impl.AbstractEnergyBlockEntity;
import com.karasu256.karasulab.karasucore.api.client.model.animation.IRotationAnimSpeed;
import com.karasu256.karasulab.karasucore.api.data.IEnergy;
import com.karasu256.projectk.client.animation.RotationAnimSpeed;
import com.karasu256.projectk.utils.Id;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AbstractPKEnergyBlockEntity<T extends IEnergy> extends AbstractEnergyBlockEntity<T> implements IRotationAnimSpeed {
    protected final IRotationAnimSpeed rotationSpeed = new RotationAnimSpeed(1.0f);

    public AbstractPKEnergyBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, long capacity) {
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
    public ResourceLocation getNbtId() {
        return Id.id("energy_block_entity");
    }

    @Override
    protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        readNbt(nbt, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        writeNbt(nbt, registries);
    }

    @Override
    public void readNbt(CompoundTag nbt, HolderLookup.Provider registries) {
        super.saveAdditional(nbt, registries);
        rotationSpeed.writeNbt(nbt, registries);
    }

    @Override
    public void writeNbt(CompoundTag nbt, HolderLookup.Provider registries) {
        super.loadAdditional(nbt, registries);
        rotationSpeed.readNbt(nbt, registries);
    }
}
