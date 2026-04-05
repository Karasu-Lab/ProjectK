package com.karasu256.projectk.neoforge.datagen;

import com.karasu256.projectk.datagen.ProjectKCommonDataGenerator;
import com.karasu256.projectk.datagen.providers.*;
import com.karasu256.projectk.neoforge.datagen.providers.NeoForgeBlockStateProvider;
import com.karasu256.projectk.neoforge.datagen.providers.NeoForgeModIntegrationProvider;
import com.karasu256.projectk.neoforge.datagen.providers.NeoForgeRecipeProvider;
import com.karasu256.projectk.neoforge.datagen.providers.ProjectEConversionProvider;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

import static com.karasu256.projectk.ProjectK.MOD_ID;

@EventBusSubscriber(modid = MOD_ID)
public class ProjectKNeoForgeDataGenerator {

    @SubscribeEvent
    public static void gatherData(@NotNull GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();

        ProjectKCommonDataGenerator.gatherData(new ProjectKCommonDataGenerator.ProviderRegistry() {
            @Override
            public <T extends DataProvider> T register(DataProvider.Factory<T> factory) {
                return generator.addProvider(event.includeServer(), factory);
            }
        }, event.getLookupProvider(), false);

        generator.addProvider(event.includeServer(), (DataProvider.Factory<NamedProvider>) output -> new NamedProvider(
                new NeoForgeModIntegrationProvider(output, event.getLookupProvider()),
                "ProjectK NeoForge Integration Recipes"));
        generator.addProvider(event.includeServer(), (DataProvider.Factory<NamedProvider>) output -> new NamedProvider(
                new NeoForgeRecipeProvider(output, event.getLookupProvider()), "ProjectK NeoForge Recipes"));
        generator.addProvider(event.includeServer(), (DataProvider.Factory<NamedProvider>) output -> new NamedProvider(
                new AbyssMagicTableRecipeProvider(output, event.getLookupProvider()),
                "ProjectK Abyss Magic Table Recipes"));
        generator.addProvider(event.includeClient(),
                new NeoForgeBlockStateProvider(generator.getPackOutput(), event.getExistingFileHelper()));
        generator.addProvider(event.includeClient(), new ProjectKBlockModelProvider(generator.getPackOutput()));
        generator.addProvider(event.includeClient(), new ProjectKLanguageProvider(generator.getPackOutput()));
        generator.addProvider(event.includeClient(),
                new ProjectEConversionProvider(generator.getPackOutput(), event.getLookupProvider()));
        generator.addProvider(event.includeServer(),
                new InBiomeInBlockCraftingProvider(generator.getPackOutput(), event.getLookupProvider()));
        generator.addProvider(event.includeServer(),
                new AbyssAlchemyBlendRecipeProvider(generator.getPackOutput(), event.getLookupProvider()));
        generator.addProvider(event.includeServer(), (DataProvider.Factory<NamedProvider>) output -> new NamedProvider(
                new AbyssSynthesizerRecipeProvider(output, event.getLookupProvider()),
                "ProjectK Abyss Synthesizer Recipes"));
    }

    private record NamedProvider(DataProvider delegate, String name) implements DataProvider {
        @Override
        @NotNull
        public CompletableFuture<?> run(@NotNull CachedOutput output) {
            return delegate.run(output);
        }

        @Override
        @NotNull
        public String getName() {
            return name;
        }
    }
}
