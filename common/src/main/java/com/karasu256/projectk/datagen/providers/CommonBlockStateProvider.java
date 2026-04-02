package com.karasu256.projectk.datagen.providers;

import com.karasu256.projectk.block.ProjectKBlocks;
import dev.architectury.registry.registries.RegistrarManager;
import net.karasuniki.karasunikilib.api.KarasunikiLib;
import net.karasuniki.karasunikilib.api.data.IEnergy;
import net.karasuniki.karasunikilib.api.registry.KarasunikiRegistries;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public class CommonBlockStateProvider {

    public interface Generator {
        void simpleBlock(Block block);

        void simpleBlockItem(Block block);

        void cubeBottomTop(Block block, String base, String side, String bottom, String top);

        void cubeBottomTop(String name, String side, String bottom, String top);

        void existingModelBlock(Block block, String modelPath);

        void existingModelBlockAllStates(Block block, String modelPath);
    }

    public static void generate(@NotNull Generator generator) {
        generator.simpleBlock(ProjectKBlocks.KARASIUM_ORE.get());
        generator.simpleBlockItem(ProjectKBlocks.KARASIUM_ORE.get());

        generator.simpleBlock(ProjectKBlocks.DEEPSLATE_KARASIUM_ORE.get());
        generator.simpleBlockItem(ProjectKBlocks.DEEPSLATE_KARASIUM_ORE.get());

        generator.cubeBottomTop(ProjectKBlocks.ABYSS_GENERATOR.get(), "abyss_generator", "abyss_energy/side", "abyss_energy/bottom", "abyss_energy/top");
        generator.simpleBlockItem(ProjectKBlocks.ABYSS_GENERATOR.get());

        for (IEnergy energy : RegistrarManager.get(KarasunikiLib.MOD_ID).get(KarasunikiRegistries.ENERGY_REGISTRY_KEY)) {
            String energyId = energy.getId().getPath();
            generator.cubeBottomTop(
                "abyss_generator/" + energyId,
                "abyss_generator/" + energyId + "/side",
                "abyss_generator/" + energyId + "/bottom",
                "abyss_generator/" + energyId + "/top"
            );
        }

        generator.simpleBlock(ProjectKBlocks.ABYSS_CORE.get());
        generator.simpleBlockItem(ProjectKBlocks.ABYSS_CORE.get());

        generator.existingModelBlock(ProjectKBlocks.ABYSS_MAGIC_TABLE.get(), "projectk:block/abyss_magic_table");
        generator.simpleBlockItem(ProjectKBlocks.ABYSS_MAGIC_TABLE.get());

        generator.existingModelBlockAllStates(ProjectKBlocks.ABYSS_ENERGY_CABLE.get(), "projectk:block/abyss_energy_cable");
        generator.simpleBlockItem(ProjectKBlocks.ABYSS_ENERGY_CABLE.get());
    }
}
