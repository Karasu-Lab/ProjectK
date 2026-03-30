package com.karasu256.projectk.neoforge.client;

import com.karasu256.projectk.neoforge.bridge.geckolib.GenericGeoBlockRenderer;
import com.karasu256.projectk.block.entity.ProjectKBlockEntities;
import net.karasuniki.karasunikilib.bridge.geckolib.GeckoLibHelper;
import net.karasuniki.karasunikilib.neoforge.client.KarasunikiLibNeoForgeClient;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

public class NeoForgeGeckoLibHelper implements GeckoLibHelper {

    public static EntityRenderersEvent.RegisterRenderers getEvent() {
        return KarasunikiLibNeoForgeClient.getCurrentEvent();
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public <T extends BlockEntity> void registerBlockRenderer(BlockEntityType<T> type) {
        EntityRenderersEvent.RegisterRenderers registerRenderers = getEvent();
        if (registerRenderers != null) {
            registerRenderers.registerBlockEntityRenderer(type, context -> new GenericGeoBlockRenderer());
        }
    }
}
