package com.karasu256.projectk.client.resource.block;

import com.karasu256.projectk.client.resource.impl.AbstractResourceGenerator;
import net.mehvahdjukaar.moonlight.api.resources.pack.ResourceSink;
import net.mehvahdjukaar.moonlight.api.resources.textures.Palette;
import net.mehvahdjukaar.moonlight.api.resources.textures.Respriter;
import net.mehvahdjukaar.moonlight.api.resources.textures.TextureImage;
import net.mehvahdjukaar.moonlight.api.resources.textures.TextureOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

public class PolishedBlockGenerator extends AbstractResourceGenerator {
    private final ResourceLocation blockResourceLocation;
    private final ResourceLocation outputResourceLocation;

    public PolishedBlockGenerator(ResourceLocation blockResourceLocation, ResourceLocation outputResourceLocation) {
        super();
        this.blockResourceLocation = blockResourceLocation;
        this.outputResourceLocation = outputResourceLocation;
    }

    @Override
    public void generate(ResourceManager manager, ResourceSink sink) {
        ResourceLocation templateId = getBaseBlockId();

        try (TextureImage netherrack = TextureImage.open(manager,
                blockResourceLocation); TextureImage template = TextureImage.open(manager, templateId)) {

            TextureOps.grayscale(template);

            Palette palette = Palette.fromImage(netherrack);
            try (TextureImage recolored = Respriter.of(template).recolor(palette)) {
                sink.addTexture(outputResourceLocation, recolored);
            }

        } catch (Exception ignored) {
        }
    }

    protected ResourceLocation getBaseBlockId() {
        return ResourceLocation.withDefaultNamespace("block/polished_andesite");
    }
}
