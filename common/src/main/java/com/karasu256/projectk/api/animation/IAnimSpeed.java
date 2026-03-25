package com.karasu256.projectk.api.animation;

import com.karasu256.projectk.api.nbt.INbtData;
import com.karasu256.projectk.api.nbt.INbtReadable;
import com.karasu256.projectk.api.nbt.INbtWritable;

public interface IAnimSpeed extends INbtReadable, INbtWritable, INbtData {
    float getSpeed();

    void setSpeed(float speed);
}
