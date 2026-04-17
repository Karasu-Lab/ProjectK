package com.karasu256.projectk.datagen.providers;

import com.karasu256.projectk.block.BlockMaterials;
import com.karasu256.projectk.block.ProjectKBlocks;
import com.karasu256.projectk.energy.ProjectKEnergies;
import dev.architectury.registry.registries.RegistrarManager;
import net.karasuniki.karasunikilib.api.KarasunikiLib;
import net.karasuniki.karasunikilib.api.data.IEnergy;
import net.karasuniki.karasunikilib.api.registry.KarasunikiRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;

public class CommonBlockStateProvider {
    public static void generate(Generator gen) {
        ProjectKBlocks.getBlockModelInfos().forEach((supplier, info) -> {
            if (ProjectKBlocks.getBlockSets().values().stream()
                    .anyMatch(variants -> variants.containsValue(supplier))) {
                return;
            }
            Block block = supplier.get();
            if (info instanceof ProjectKBlocks.ModelInfo.Simple) {
                gen.simpleBlock(block);
                gen.simpleBlockItem(block);
            } else if (info instanceof ProjectKBlocks.ModelInfo.Existing(String path)) {
                gen.existingModelBlock(block, path);
                gen.simpleBlockItem(block, path);
            } else if (info instanceof ProjectKBlocks.ModelInfo.CubeBottomTop(
                    String base, String side, String bottom, String top
            )) {
                gen.cubeBottomTop(block, base, side, bottom, top);
                gen.simpleBlockItem(block);
            }
        });

        ProjectKBlocks.getBlockSets().forEach((fullBlockSupplier, variants) -> {
            Block fullBlock = fullBlockSupplier.get();
            gen.fullBlockByFamily(fullBlock);
            gen.simpleBlockItem(fullBlock);

            Block stairs = variants.get(BlockMaterials.STAIR).get();
            Block slab = variants.get(BlockMaterials.HALF).get();
            gen.stairsBlock((StairBlock) stairs, fullBlock);
            gen.slabBlock((SlabBlock) slab, fullBlock);
        });

        for (IEnergy energy : RegistrarManager.get(KarasunikiLib.MOD_ID)
                .get(KarasunikiRegistries.ENERGY_REGISTRY_KEY)) {
            String energyId = energy.getId().getPath();
            gen.cubeBottomTop("abyss_generator/" + energyId, "abyss_generator/" + energyId + "/side",
                    "abyss_generator/" + energyId + "/bottom", "abyss_generator/" + energyId + "/top");
        }

        for (ProjectKEnergies.EnergyDefinition definition : ProjectKEnergies.getDefinitions()) {
            String fluidId = "fluid_" + definition.idPath();
            gen.existingModelBlockAllStates(ProjectKBlocks.getFluidBlock(definition.id()).get(),
                    "projectk:block/" + fluidId);
        }

        gen.multipartCable(ProjectKBlocks.ABYSS_ENERGY_CABLE.get(), "abyss_energy_cable");
        gen.simpleBlockItem(ProjectKBlocks.ABYSS_ENERGY_CABLE.get());

        gen.directionalModeBlock(ProjectKBlocks.ABYSS_LASER_EMITTER.get(), "abyss_laser_emitter");
        gen.simpleBlockItem(ProjectKBlocks.ABYSS_LASER_EMITTER.get());

        gen.activeBlock(ProjectKBlocks.ABYSS_PORTAL.get(), "projectk:block/abyss_portal",
                "projectk:block/abyss_portal_active");
        gen.simpleBlockItem(ProjectKBlocks.ABYSS_PORTAL.get());
    }

    public interface Generator {
        void simpleBlock(Block block);

        void fullBlockByFamily(Block block);

        void simpleBlockItem(Block block);

        void simpleBlockItem(Block block, String modelPath);

        void cubeBottomTop(Block block, String base, String side, String bottom, String top);

        void cubeBottomTop(String name, String side, String bottom, String top);

        void existingModelBlock(Block block, String modelPath);

        void existingModelBlockAllStates(Block block, String modelPath);

        void multipartCable(Block block, String id);

        void directionalModeBlock(Block block, String baseName);

        void activeBlock(Block block, String modelPath, String activeModelPath);

        void stairsBlock(StairBlock block, Block fullBlock);

        void slabBlock(SlabBlock block, Block fullBlock);
    }
}
