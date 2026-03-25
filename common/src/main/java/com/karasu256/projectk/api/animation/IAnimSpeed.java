package com.karasu256.projectk.api.animation;

import com.karasu256.projectk.api.nbt.INbtReadable;
import com.karasu256.projectk.api.nbt.INbtWritable;

public interface IAnimSpeed extends INbtReadable, INbtWritable {
    float getSpeed();

    void setSpeed(float speed);
}
