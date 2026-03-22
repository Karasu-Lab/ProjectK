package com.karasu256.projectk.neoforge.datagen;

import com.karasu256.projectk.datagen.ProjectKCommonDataGenerator;
import com.karasu256.projectk.neoforge.datagen.providers.NeoForgeModIntegrationProvider;
import net.minecraft.data.DataGenerator;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import static com.karasu256.projectk.ProjectK.MOD_ID;

@EventBusSubscriber(modid = MOD_ID)
public class ProjectKNeoForgeDataGenerator {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();

        ProjectKCommonDataGenerator.gatherData(factory -> generator.addProvider(event.includeServer(), factory), event.getLookupProvider());

        generator.addProvider(
                event.includeServer(),
                new NeoForgeModIntegrationProvider(generator.getPackOutput(), event.getLookupProvider())
        );
    }
}
