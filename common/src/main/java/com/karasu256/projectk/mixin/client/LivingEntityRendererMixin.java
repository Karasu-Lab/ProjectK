package com.karasu256.projectk.mixin.client;

import com.karasu256.projectk.client.render.entity.layer.GenericChargedLayer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings({"DataFlowIssue", "unchecked", "rawtypes"})
@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>> implements RenderLayerParent<T, M> {
    @Inject(method = "<init>", at = @At("TAIL"))
    private void projectk$onInit(EntityRendererProvider.Context context, M model, float shadowRadius, CallbackInfo ci) {
        this.addLayer(new GenericChargedLayer<>((LivingEntityRenderer) (Object) this));
    }

    @Shadow
    public abstract boolean addLayer(RenderLayer<T, M> layer);
}
