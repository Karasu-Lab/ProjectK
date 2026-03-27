package com.karasu256.projectk.client.animation;

import com.karasu256.projectk.utils.Id;
import net.karasuniki.karasunikilib.api.client.model.animation.IAnimSpeed;
import net.karasuniki.karasunikilib.api.data.impl.AbstractNbtReadAndWrite;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public class AnimSpeed extends AbstractNbtReadAndWrite implements IAnimSpeed {
    private float speed;

    public AnimSpeed(float speed) {
        this.speed = speed;
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
    public ResourceLocation getNbtId() {
        return Id.id("anim_speed");
    }

    @Override
    public void readNbt(CompoundTag nbt, HolderLookup.Provider registries) {
        if (nbt.contains(getNbtId().toString())) {
            speed = nbt.getFloat(getNbtId().toString());
        }
    }

    @Override
    public void writeNbt(CompoundTag nbt, HolderLookup.Provider registries) {
        nbt.putFloat(getNbtId().toString(), speed);
    }
}
