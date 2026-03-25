package com.karasu256.projectk.client.animation;

import com.karasu256.projectk.api.animation.IAnimSpeed;
import com.karasu256.projectk.api.nbt.AbstractNbtReadAndWrite;
import com.karasu256.projectk.utils.Id;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public class AnimSpeed extends AbstractNbtReadAndWrite implements IAnimSpeed {
    private float speed;

    public AnimSpeed(float initialSpeed) {
        this.speed = initialSpeed;
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
        String key = getNbtId().toString();
        if (nbt.contains(key)) {
            speed = nbt.getFloat(key);
        }
    }

    @Override
    public void writeNbt(CompoundTag nbt, HolderLookup.Provider registries) {
        nbt.putFloat(getNbtId().toString(), speed);
    }

    @Override
    public ResourceLocation getNbtId() {
        return Id.id("anim_speed");
    }
}
