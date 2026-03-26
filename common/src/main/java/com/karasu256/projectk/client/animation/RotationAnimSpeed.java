package com.karasu256.projectk.client.animation;

import com.karasu256.karasulab.karasucore.api.client.model.animation.IRotationAnimSpeed;
import com.karasu256.projectk.utils.Id;
import net.minecraft.resources.ResourceLocation;

public class RotationAnimSpeed extends AnimSpeed implements IRotationAnimSpeed {
    public RotationAnimSpeed(float initialSpeed) {
        super(initialSpeed);
    }

    @Override
    public float getRotationSpeed() {
        return getSpeed();
    }

    @Override
    public void setRotationSpeed(float speed) {
        setSpeed(speed);
    }

    @Override
    public ResourceLocation getNbtId() {
        return Id.id("rotation_speed");
    }
}
