package com.karasu256.projectk.energy;

import com.karasu256.projectk.api.nbt.INbtData;
import net.minecraft.resources.ResourceLocation;

public interface IEnergy extends INbtData {
    long getValue();

    @Override
    default ResourceLocation getNbtId() {
        return getId();
    }

    ResourceLocation getId();
}
