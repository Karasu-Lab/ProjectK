package com.karasu256.projectk.energy;

import com.karasu256.projectk.utils.Id;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record AbyssEnergy(long value) implements IEnergy {
    @Override
    public long getValue() {
        return value;
    }

    @Override
    @NotNull
    public ResourceLocation getId() {
        return Id.id("abyss_energy");
    }

    @Override
    @NotNull
    public ResourceLocation getNbtId() {
        return getId();
    }
}
