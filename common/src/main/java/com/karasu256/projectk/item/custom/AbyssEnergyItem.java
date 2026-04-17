package com.karasu256.projectk.item.custom;

import com.karasu256.projectk.data.AbyssEnergyData;
import com.karasu256.projectk.data.EnergyCapacityData;
import com.karasu256.projectk.energy.IProjectKEnergy;
import com.karasu256.projectk.energy.ProjectKEnergies;
import com.karasu256.projectk.item.IVariantItem;
import dev.architectury.registry.registries.RegistrarManager;
import net.karasuniki.karasunikilib.api.KarasunikiLib;
import net.karasuniki.karasunikilib.api.registry.KarasunikiRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AbyssEnergyItem extends ProjectKItem {
    public AbyssEnergyItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean shouldSkipDefault() {
        return true;
    }

    @Override
    public void displayVariants(CreativeModeTab.Output output) {
        for (ProjectKEnergies.EnergyDefinition def : ProjectKEnergies.getDefinitions()) {
            ItemStack stack = new ItemStack(this);
            AbyssEnergyData.applyToStack(stack, def.id(), def.defaultAmount());
            output.accept(stack);
        }
    }

    public static Component buildTooltip(ResourceLocation energyId, long amount, @Nullable EnergyCapacityData capacityData) {
        Component energyName = resolveEnergyName(energyId);
        boolean isInfinite = ProjectKEnergies.isInfinite(amount, capacityData);

        if (isInfinite) {
            return energyName.copy().append(" ").append(Component.translatable("tooltip.projectk.infinite"));
        }
        if (capacityData == null) {
            return energyName.copy().append(" ").append(String.format("%,d", amount));
        }
        long cap = capacityData.get();
        Component energyLine = Component.literal(String.format("%,d / %,d", amount, cap));
        return energyName.copy().append(" ").append(energyLine);
    }

    public static List<Component> toolTipComponents(ResourceLocation energyId, long amount, @Nullable EnergyCapacityData capacityData) {
        if (energyId == null) {
            return List.of();
        }
        Component energyName = resolveEnergyName(energyId);
        boolean isInfinite = ProjectKEnergies.isInfinite(amount, capacityData);

        if (isInfinite) {
            return List.of(energyName, Component.translatable("tooltip.projectk.infinite"));
        }
        if (capacityData == null) {
            return List.of(energyName, Component.literal(String.format("%,d", amount)));
        }
        long cap = capacityData.get();
        Component energyLine = Component.literal(String.format("%,d / %,d", amount, cap));
        return List.of(energyName, energyLine);
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
