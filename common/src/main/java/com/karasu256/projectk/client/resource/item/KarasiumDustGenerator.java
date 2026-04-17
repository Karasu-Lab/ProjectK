package com.karasu256.projectk.client.resource.item;

import com.karasu256.projectk.client.resource.impl.AbstractResourceGenerator;
import com.karasu256.projectk.utils.Id;
import net.mehvahdjukaar.moonlight.api.resources.pack.ResourceSink;
import net.mehvahdjukaar.moonlight.api.resources.textures.TextureImage;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.FastColor;

public class KarasiumDustGenerator extends AbstractResourceGenerator {
    @Override
    public void generate(ResourceManager manager, ResourceSink sink) {
        ResourceLocation textureId = Id.id("item/karasium_dust");
        ResourceLocation baseId = ResourceLocation.withDefaultNamespace("item/redstone");

        try (TextureImage texture = TextureImage.open(manager, baseId)) {
            additiveYellowGreen(texture);
            sink.addTexture(textureId, texture);
            sink.addItemModel(Id.id("karasium_dust"), createGeneratedModel(textureId));
        } catch (Exception ignored) {
        }
    }

    protected final void additiveYellowGreen(TextureImage img) {
        int tintR = 0xAD;
        int tintG = 0xFF;
        int tintB = 0x2F;

        img.forEachPixel(pixel -> {
            int color = pixel.getValue();
            int alpha = FastColor.ABGR32.alpha(color);
            if (alpha > 0) {
                int r = FastColor.ABGR32.red(color);
                int g = FastColor.ABGR32.green(color);
                int b = FastColor.ABGR32.blue(color);

                int gray = (int) ((r * 0.299f + g * 0.587f + b * 0.114f) * 1.5f);
                gray = Math.min(255, gray);

                int resR = Math.min(255, gray + tintR);
                int resG = Math.min(255, gray + tintG);
                int resB = Math.min(255, gray + tintB);

                pixel.setValue(FastColor.ABGR32.color(alpha, resB, resG, resR));
            }
        });
    }
}
