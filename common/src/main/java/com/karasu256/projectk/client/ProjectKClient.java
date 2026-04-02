package com.karasu256.projectk.client;

import com.karasu256.projectk.block.entity.ProjectKBlockEntities;
import com.karasu256.projectk.client.render.block.AbyssEnergyCableBlockEntityRenderer;
import com.karasu256.projectk.client.render.block.geckolib.AbyssGeoBlockRenderer;
import com.karasu256.projectk.client.render.entity.AbyssEnergyEntityRenderer;
import com.karasu256.projectk.client.screen.AbyssMagicTableScreen;
import com.karasu256.projectk.entity.ProjectKEntities;
import com.karasu256.projectk.menu.ProjectKMenus;
import com.karasu256.projectk.particle.AbyssParticle;
import com.karasu256.projectk.particle.ProjectKParticles;
import net.minecraft.client.gui.screens.MenuScreens;
import dev.architectury.registry.client.level.entity.EntityRendererRegistry;
import dev.architectury.registry.client.particle.ParticleProviderRegistry;
import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;

public class ProjectKClient {
    public static void init() {
        EntityRendererRegistry.register(ProjectKEntities.ABYSS_ENERGY_ENTITY, AbyssEnergyEntityRenderer::new);
        MenuScreens.register(ProjectKMenus.ABYSS_MAGIC_TABLE.get(), AbyssMagicTableScreen::new);
    }

    public static void initLate() {
        ParticleProviderRegistry.register(ProjectKParticles.ABYSS_PARTICLE.get(), AbyssParticle.Provider::new);
        BlockEntityRendererRegistry.register(ProjectKBlockEntities.ABYSS_CORE.get(), context -> new AbyssGeoBlockRenderer<>());
        BlockEntityRendererRegistry.register(ProjectKBlockEntities.ABYSS_ENERGY_CABLE.get(), AbyssEnergyCableBlockEntityRenderer::new);
    }
}
