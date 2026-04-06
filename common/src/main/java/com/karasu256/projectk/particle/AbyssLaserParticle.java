package com.karasu256.projectk.particle;

import com.karasu256.projectk.fluid.ProjectKFluids;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AbyssLaserParticle extends Particle {
    private final ResourceLocation energyId;
    private final Vec3 origin;
    private final Vec3 target;
    private final LaserParticleOptions options;

    protected AbyssLaserParticle(ClientLevel level, double x, double y, double z, LaserParticleOptions options) {
        super(level, x, y, z);
        this.options = options;
        this.energyId = options.energyId();
        this.origin = new Vec3(x, y, z);
        this.target = options.target();
        this.lifetime = options.lifetime();
        this.hasPhysics = false;
        this.gravity = 0;
        this.xd = 0;
        this.yd = 0;
        this.zd = 0;
        this.setBoundingBox(new AABB(origin, target).inflate(10.0));
    }

    @Override
    public void tick() {
        if (this.age++ >= this.lifetime) {
            this.remove();
        }
    }

    @Override
    @NotNull
    public ParticleRenderType getRenderType() {
        return ProjectKParticleRenderTypes.LASER;
    }

    @Override
    public void render(VertexConsumer consumer, Camera camera, float partialTicks) {
        Vec3 camPos = camera.getPosition();
        
        Vec3 relOrigin = origin.subtract(camPos);
        Vec3 relTarget = target.subtract(camPos);

        Vec3 beam = relTarget.subtract(relOrigin);
        if (beam.lengthSqr() < 0.0001)
            return;

        Vec3 viewDir = camera.getPosition().subtract(origin);
        Vec3 cross = beam.cross(viewDir);
        if (cross.lengthSqr() < 0.0001) {
            cross = beam.cross(new Vec3(0, 1, 0));
            if (cross.lengthSqr() < 0.0001) {
                cross = beam.cross(new Vec3(1, 0, 0));
            }
        }
        Vec3 side = cross.normalize().scale(options.scale());

        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                .apply(ProjectKFluids.getAttributes(energyId).getSourceTexture());

        float u0 = sprite.getU0();
        float u1 = sprite.getU1();
        float v0 = sprite.getV0();
        float v1 = sprite.getV1();

        float r = 1f, g = 1f, b = 1f, a = 1.0f;

        addVertex(consumer, relOrigin.subtract(side), u0, v0, r, g, b, a);
        addVertex(consumer, relOrigin.add(side), u1, v0, r, g, b, a);
        addVertex(consumer, relTarget.add(side), u1, v1, r, g, b, a);
        addVertex(consumer, relTarget.subtract(side), u0, v1, r, g, b, a);

        Vec3 cross2 = beam.cross(side);
        if (cross2.lengthSqr() >= 0.0001) {
            Vec3 side2 = cross2.normalize().scale(options.scale());
            addVertex(consumer, relOrigin.subtract(side2), u0, v0, r, g, b, a);
            addVertex(consumer, relOrigin.add(side2), u1, v0, r, g, b, a);
            addVertex(consumer, relTarget.add(side2), u1, v1, r, g, b, a);
            addVertex(consumer, relTarget.subtract(side2), u0, v1, r, g, b, a);
        }
    }

    private void addVertex(VertexConsumer consumer, Vec3 pos, float u, float v, float r, float g, float b, float a) {
        consumer.addVertex((float) pos.x, (float) pos.y, (float) pos.z).setUv(u, v).setColor(r, g, b, a);
    }

    public static class Provider implements ParticleProvider<LaserParticleOptions> {
        @Nullable
        @Override
        public Particle createParticle(LaserParticleOptions options, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new AbyssLaserParticle(level, x, y, z, options);
        }
    }
}
