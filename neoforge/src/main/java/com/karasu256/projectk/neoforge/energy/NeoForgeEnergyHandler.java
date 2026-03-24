package com.karasu256.projectk.neoforge.energy;

import com.karasu256.projectk.ProjectK;
import com.karasu256.projectk.energy.IAbyssEnergy;
import com.karasu256.projectk.registry.BlockEntitiesRegistry;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber(modid = ProjectK.MOD_ID)
public class NeoForgeEnergyHandler {
    @SubscribeEvent
    public static void registerCapabilities(@NotNull RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                BlockEntitiesRegistry.K_GENERATOR.get(),
                (be, side) -> new AbyssEnergyForgeWrapper(be)
        );
    }
}
