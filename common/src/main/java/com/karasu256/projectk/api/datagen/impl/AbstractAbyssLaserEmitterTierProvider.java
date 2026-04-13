package com.karasu256.projectk.api.datagen.impl;

import com.karasu256.projectk.api.tier.PKTierInfo;
import com.karasu256.projectk.api.tier.PKTiers;
import com.karasu256.projectk.data.AbyssLaserEmitterTier;
import net.minecraft.data.PackOutput;

public abstract class AbstractAbyssLaserEmitterTierProvider extends AbstractProjectKDataProvider<AbyssLaserEmitterTier> {
    public AbstractAbyssLaserEmitterTierProvider(PackOutput output, String name) {
        super(output, name);
    }

    protected void add(PKTierInfo info, long capacity, long concentration, int lifetime, int pulseInterval) {
        addData(String.format("tier_%s", info.level()),
                new AbyssLaserEmitterTier(info.level(), capacity, concentration, lifetime, pulseInterval));
    }

    protected void add(PKTiers tier, long capacity, long concentration, int lifetime, int pulseInterval) {
        add(tier.getInfo(), capacity, concentration, lifetime, pulseInterval);
    }
}
