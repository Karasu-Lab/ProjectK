package com.karasu256.projectk.neoforge.datagen;

import com.karasu256.projectk.datagen.ProjectKCommonDataGenerator;
import com.karasu256.projectk.neoforge.datagen.providers.NeoForgeBlockStateProvider;
import com.karasu256.projectk.neoforge.datagen.providers.NeoForgeModIntegrationProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import static com.karasu256.projectk.ProjectK.MOD_ID;

@EventBusSubscriber(modid = MOD_ID)
public class ProjectKNeoForgeDataGenerator {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();

        ProjectKCommonDataGenerator.gatherData(new ProjectKCommonDataGenerator.ProviderRegistry() {
            @Override
            public <T extends DataProvider> T register(DataProvider.Factory<T> factory) {
                return generator.addProvider(event.includeServer(), factory);
            }
        }, event.getLookupProvider(), false);

        generator.addProvider(
                event.includeServer(),
                new NeoForgeModIntegrationProvider(generator.getPackOutput(), event.getLookupProvider())
        );

        generator.addProvider(
                event.includeClient(),
                new NeoForgeBlockStateProvider(generator.getPackOutput(), event.getExistingFileHelper())
        );
    }
}
