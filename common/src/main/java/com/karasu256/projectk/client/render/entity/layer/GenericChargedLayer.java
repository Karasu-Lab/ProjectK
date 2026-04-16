package com.karasu256.projectk.client.render.entity.layer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EnergySwirlLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PowerableMob;

@Environment(EnvType.CLIENT)
public class GenericChargedLayer<T extends LivingEntity & PowerableMob, M extends EntityModel<T>> extends EnergySwirlLayer<T, M> {
    private static final ResourceLocation POWER_LOCATION = ResourceLocation.withDefaultNamespace(
            "textures/entity/creeper/creeper_armor.png");

    public GenericChargedLayer(RenderLayerParent<T, M> renderer) {
        super(renderer);
    }

    @Override
    protected float xOffset(float f) {
        return f * 0.01F;
    }

    @Override
    protected ResourceLocation getTextureLocation() {
        return POWER_LOCATION;
    }

    @Override
    protected EntityModel<T> model() {
        return this.getParentModel();
    }
}
