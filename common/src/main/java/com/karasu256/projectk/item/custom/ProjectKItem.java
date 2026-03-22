package com.karasu256.projectk.item.custom;

import com.karasu256.projectk.data.EMCData;
import com.karasu256.projectk.data.ProjectKDataComponets;
import net.minecraft.world.item.Item;

import java.math.BigInteger;
import java.util.Optional;

public class ProjectKItem extends Item implements IEMCConfig {
    public ProjectKItem(Properties properties) {
        super(properties);
    }

    @Override
    public Optional<EMCData> getEMC() {
        return Optional.ofNullable(this.components().get(ProjectKDataComponets.EMC_DATACOMPONENT_TYPE.get()));
    }

    public static class Properties extends Item.Properties {
        public Properties emc(long amount) {
            return this.emc(BigInteger.valueOf(amount));
        }

        public Properties emc(BigInteger amount) {
            this.component(ProjectKDataComponets.EMC_DATACOMPONENT_TYPE.get(), new EMCData(amount));
            return this;
        }
    }
}
