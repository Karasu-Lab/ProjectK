package com.karasu256.projectk.client.animation;

import com.karasu256.projectk.api.animation.IRotationAnimSpeed;

public class RotationAnimSpeed extends AnimSpeed implements IRotationAnimSpeed {
    public RotationAnimSpeed(float initialSpeed) {
        super(initialSpeed, "RotationSpeed");
    }

    @Override
    public float getRotationSpeed() {
        return getSpeed();
    }

    @Override
    public void setRotationSpeed(float speed) {
        setSpeed(speed);
    }
}
