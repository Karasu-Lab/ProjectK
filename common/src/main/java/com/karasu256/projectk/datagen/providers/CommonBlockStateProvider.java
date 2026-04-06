package com.karasu256.projectk.datagen.providers;

import com.karasu256.projectk.block.ProjectKBlocks;
import com.karasu256.projectk.energy.ProjectKEnergies;
import dev.architectury.registry.registries.RegistrarManager;
import net.karasuniki.karasunikilib.api.KarasunikiLib;
import net.karasuniki.karasunikilib.api.data.IEnergy;
import net.karasuniki.karasunikilib.api.registry.KarasunikiRegistries;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public class CommonBlockStateProvider {

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

        for (ProjectKEnergies.EnergyDefinition definition : ProjectKEnergies.getDefinitions()) {
            String coreId = definition.idPath().replace("_energy", "_core");
            Block core = ProjectKBlocks.getCore(definition.id()).get();
            generator.existingModelBlock(core, "projectk:block/" + coreId);
            generator.simpleBlockItem(core);
        }

        generator.existingModelBlock(ProjectKBlocks.ABYSS_MAGIC_TABLE.get(), "projectk:block/abyss_magic_table");
        generator.simpleBlockItem(ProjectKBlocks.ABYSS_MAGIC_TABLE.get());

        generator.existingModelBlock(ProjectKBlocks.ABYSS_ALCHEMY_BLEND_MACHINE.get(), "projectk:block/abyss_alchemy_blend_machine");
        generator.simpleBlockItem(ProjectKBlocks.ABYSS_ALCHEMY_BLEND_MACHINE.get());

        generator.existingModelBlock(ProjectKBlocks.ABYSS_ENCHANTER.get(), "projectk:block/abyss_enchanter");
        generator.simpleBlockItem(ProjectKBlocks.ABYSS_ENCHANTER.get());

        generator.existingModelBlock(ProjectKBlocks.ABYSS_CHARGER.get(), "projectk:block/abyss_charger");
        generator.simpleBlockItem(ProjectKBlocks.ABYSS_CHARGER.get());

        generator.existingModelBlock(ProjectKBlocks.ABYSS_STORAGE.get(), "projectk:block/abyss_storage");
        generator.simpleBlockItem(ProjectKBlocks.ABYSS_STORAGE.get());

        generator.existingModelBlock(ProjectKBlocks.ABYSS_ENCHANT_REMOVER.get(), "projectk:block/abyss_enchant_remover");
        generator.simpleBlockItem(ProjectKBlocks.ABYSS_ENCHANT_REMOVER.get());

        generator.multipartCable(ProjectKBlocks.ABYSS_ENERGY_CABLE.get(), "abyss_energy_cable");
        generator.simpleBlockItem(ProjectKBlocks.ABYSS_ENERGY_CABLE.get());

        generator.directionalModeBlock(ProjectKBlocks.ABYSS_LASER_EMITTER.get(), "abyss_laser_emitter");
        generator.simpleBlockItem(ProjectKBlocks.ABYSS_LASER_EMITTER.get());

        generator.simpleBlock(ProjectKBlocks.ABYSS_ABSORPTION_PRISM.get());
        generator.simpleBlockItem(ProjectKBlocks.ABYSS_ABSORPTION_PRISM.get());

        for (ProjectKEnergies.EnergyDefinition definition : ProjectKEnergies.getDefinitions()) {
            String fluidId = "fluid_" + definition.idPath();
            generator.existingModelBlockAllStates(ProjectKBlocks.getFluidBlock(definition.id()).get(), "projectk:block/" + fluidId);
        }
    }

    public interface Generator {
        void simpleBlock(Block block);

        void simpleBlockItem(Block block);

        void cubeBottomTop(Block block, String base, String side, String bottom, String top);

        void cubeBottomTop(String name, String side, String bottom, String top);

        void existingModelBlock(Block block, String modelPath);

        void existingModelBlockAllStates(Block block, String modelPath);

        void multipartCable(Block block, String id);

        void directionalModeBlock(Block block, String baseName);
    }
}
