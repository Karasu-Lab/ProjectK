package com.karasu256.projectk.item.custom;

import com.karasu256.projectk.data.AbyssEnergyData;
import com.karasu256.projectk.data.EMCData;
import com.karasu256.projectk.data.ProjectKDataComponets;
import com.karasu256.projectk.item.IVariantItem;
import dev.architectury.registry.registries.RegistrySupplier;
import net.karasuniki.karasunikilib.api.data.IEnergy;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.math.BigInteger;
import java.util.Optional;

public class ProjectKItem extends Item implements IEMCConfig, IVariantItem {
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

        public <T extends IEnergy> Properties abyssEnergy(RegistrySupplier<T> energy, long amount) {
            if (energy == null) {
                return this;
            }
            return abyssEnergy(energy.getId(), amount);
        }

        public Properties abyssEnergy(ResourceLocation energyId, long amount) {
            if (amount < 0) {
                this.component(ProjectKDataComponets.ABYSS_ENERGY_DATA_COMPONENT_TYPE.get(), null);
                return this;
            }
            if (amount == 0 || energyId == null) {
                return this;
            }
            this.component(ProjectKDataComponets.ABYSS_ENERGY_DATA_COMPONENT_TYPE.get(), new AbyssEnergyData(energyId, amount));
            return this;
        }
    }
}
