package com.karasu256.projectk.energy;


import net.karasuniki.karasunikilib.api.block.ICableInputable;
import net.karasuniki.karasunikilib.api.block.ICableOutputable;
import net.karasuniki.karasunikilib.api.data.ICapacity;
import net.karasuniki.karasunikilib.api.data.IEnergy;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public interface IProjectKEnergy extends ICableOutputable, ICableInputable, IEnergy, ICapacity {
    @Override
    long insert(ResourceLocation id, long maxAmount, boolean simulate);

    @Override
    long extract(ResourceLocation id, long maxAmount, boolean simulate);

    default String getTranslationKey() {
        return "energy." + getId().getNamespace() + "." + getId().getPath();
    }

    Component getName();

    Component getFormatted();
}
