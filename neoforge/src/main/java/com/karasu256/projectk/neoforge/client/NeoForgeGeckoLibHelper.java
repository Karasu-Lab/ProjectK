package com.karasu256.projectk.neoforge.client;

import com.karasu256.karasulab.karasucore.bridge.geckolib.GeckoLibHelper;
import com.karasu256.projectk.neoforge.bridge.geckolib.GenericGeoBlockRenderer;
import com.karasu256.projectk.registry.BlockEntitiesRegistry;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

public class NeoForgeGeckoLibHelper implements GeckoLibHelper {
    private static EntityRenderersEvent.RegisterRenderers event;

    public static void setEvent(EntityRenderersEvent.RegisterRenderers event) {
        NeoForgeGeckoLibHelper.event = event;
    }

    public static EntityRenderersEvent.RegisterRenderers getEvent() {
        return event;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends BlockEntity> void registerBlockRenderer(BlockEntityType<T> type) {
        if (type == BlockEntitiesRegistry.ABYSS_CORE.get()) {
            if (event != null) {
                event.registerBlockEntityRenderer(type, context -> new GenericGeoBlockRenderer());
            }
        }
    }
}
