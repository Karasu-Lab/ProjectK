package com.karasu256.projectk.block.entity.impl;

import com.karasu256.projectk.client.animation.RotationAnimSpeed;
import net.karasuniki.karasunikilib.api.block.entity.impl.KarasuCoreBlockEntity;
import net.karasuniki.karasunikilib.api.client.model.animation.IRotationAnimSpeed;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractAnimatableBlockEntity extends KarasuCoreBlockEntity {
    protected final IRotationAnimSpeed rotationSpeed = new RotationAnimSpeed(1.0f);

    public AbstractAnimatableBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public IRotationAnimSpeed getRotationAnimSpeed() {
        return rotationSpeed;
    }


    @Override
    protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        super.saveAdditional(nbt, registries);
        rotationSpeed.writeNbt(nbt, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        super.loadAdditional(nbt, registries);
        rotationSpeed.readNbt(nbt, registries);
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
