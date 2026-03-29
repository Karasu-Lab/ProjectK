package com.karasu256.projectk.fabric.client;

import net.karasuniki.karasunikilib.bridge.geckolib.GeckoLibHelper;
import com.karasu256.projectk.fabric.bridge.geckolib.GenericGeoBlockRenderer;
import com.karasu256.projectk.block.entity.ProjectKBlockEntities;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

@SuppressWarnings({"deprecation", "rawtypes"})
public class FabricGeckoLibHelper implements GeckoLibHelper {
    @SuppressWarnings("unchecked")
    @Override
    public <T extends BlockEntity> void registerBlockRenderer(BlockEntityType<T> type) {
        if (type == ProjectKBlockEntities.ABYSS_CORE.get()) {
            BlockEntityRendererRegistry.register(type, context -> new GenericGeoBlockRenderer());
        }
    }
}
