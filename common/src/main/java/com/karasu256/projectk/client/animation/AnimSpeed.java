package com.karasu256.projectk.client.animation;

import com.karasu256.projectk.api.animation.IAnimSpeed;
import com.karasu256.projectk.api.nbt.AbstractNbtReadAndWrite;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;

public class AnimSpeed extends AbstractNbtReadAndWrite implements IAnimSpeed {
    private float speed;
    protected String nbtKey = "AnimSpeed";

    public AnimSpeed(float initialSpeed) {
        this.speed = initialSpeed;
    }

    public AnimSpeed(float initialSpeed, String nbtKey) {
        this.speed = initialSpeed;
        this.nbtKey = nbtKey;
    }

    @Override
    public float getSpeed() {
        return speed;
    }

    @Override
    public void setSpeed(float speed) {
        this.speed = speed;
    }

    @Override
    public void readNbt(CompoundTag nbt, HolderLookup.Provider registries) {
        if (nbt.contains(nbtKey)) {
            speed = nbt.getFloat(nbtKey);
        }
    }

    @Override
    public void writeNbt(CompoundTag nbt, HolderLookup.Provider registries) {
        nbt.putFloat(nbtKey, speed);
    }
}
