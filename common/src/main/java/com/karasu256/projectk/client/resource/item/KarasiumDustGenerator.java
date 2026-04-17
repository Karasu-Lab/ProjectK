package com.karasu256.projectk.client.resource.item;

import com.karasu256.projectk.client.resource.impl.AbstractResourceGenerator;
import com.karasu256.projectk.utils.Id;
import net.mehvahdjukaar.moonlight.api.resources.pack.ResourceSink;
import net.mehvahdjukaar.moonlight.api.resources.textures.TextureImage;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

public class KarasiumDustGenerator extends AbstractResourceGenerator {

    @Override
    public void generate(ResourceManager manager, ResourceSink sink) {
        ResourceLocation textureId = Id.id("item/karasium_dust");
        ResourceLocation baseId = ResourceLocation.withDefaultNamespace("item/redstone");

        try (TextureImage texture = TextureImage.open(manager, baseId)) {
            brightenedAdditiveTint(texture, 0xADFF2F);
            sink.addTexture(textureId, texture);
            sink.addItemModel(Id.id("karasium_dust"), createGeneratedModel(textureId));
        } catch (Exception ignored) {
        }
    }
}
