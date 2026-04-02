package com.karasu256.projectk.item.custom;

import com.karasu256.projectk.energy.IProjectKEnergy;
import dev.architectury.registry.registries.RegistrarManager;
import net.karasuniki.karasunikilib.api.KarasunikiLib;
import net.karasuniki.karasunikilib.api.registry.KarasunikiRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class AbyssEnergyItem extends ProjectKItem {
    public AbyssEnergyItem(Properties properties) {
        super(properties);
    }

    public static Component buildTooltip(ResourceLocation energyId, long amount) {
        Component energyName = resolveEnergyName(energyId);
        return Component.translatable("energy.projectk.abyss_energy_format", energyName)
                .append(Component.literal(" " + amount));
    }

    public static Component resolveEnergyName(ResourceLocation energyId) {
        var registrar = RegistrarManager.get(KarasunikiLib.MOD_ID).get(KarasunikiRegistries.ENERGY_REGISTRY_KEY);
        var energy = registrar.get(energyId);
        if (energy instanceof IProjectKEnergy pkEnergy) {
            return pkEnergy.getName();
        }
        return Component.translatable("energy." + energyId.getNamespace() + "." + energyId.getPath());
    }
}
