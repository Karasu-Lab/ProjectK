package com.karasu256.projectk.particle;

import com.karasu256.projectk.ProjectK;
import com.karasu256.projectk.energy.AbyssEnergyUtils;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class AbyssBurstParticle extends Particle {
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(ProjectK.MOD_ID,
            "textures/particle/abyss_burst.png");
    private final AbyssBurstParticleOptions options;
    protected float quadSize;

    public AbyssBurstParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, AbyssBurstParticleOptions options) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);
        this.options = options;
        this.friction = 0.98F;
        this.gravity = 0.01F;
        this.xd = xSpeed;
        this.yd = ySpeed;
        this.zd = zSpeed;
        this.quadSize = 0.125F * (this.random.nextFloat() * 0.5F + 0.5F) * 1.5F;
        this.hasPhysics = true;
        this.lifetime = 20 + level.random.nextInt(10);

        int color = AbyssEnergyUtils.getEnergyColor(options.energyId());
        this.rCol = (float) ((color >> 16) & 0xFF) / 255.0F;
        this.gCol = (float) ((color >> 8) & 0xFF) / 255.0F;
        this.bCol = (float) (color & 0xFF) / 255.0F;
        this.alpha = 1.0F;
    }

    @Override
    @NotNull
    public ParticleRenderType getRenderType() {
        return ProjectKParticleRenderTypes.ABYSS_BURST;
    }

    @Override
    public void render(VertexConsumer consumer, Camera camera, float partialTicks) {
        Vec3 camPos = camera.getPosition();
        float x = (float) (Mth.lerp(partialTicks, this.xo, this.x) - camPos.x);
        float y = (float) (Mth.lerp(partialTicks, this.yo, this.y) - camPos.y);
        float z = (float) (Mth.lerp(partialTicks, this.zo, this.z) - camPos.z());

        Quaternionf quaternion = camera.rotation();
        Vector3f[] vector3fs = new Vector3f[]{new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F,
                0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)};
        float scale = this.quadSize;

        for (int i = 0; i < 4; ++i) {
            Vector3f v = vector3fs[i];
            v.rotate(quaternion);
            v.mul(scale);
            v.add(x, y, z);
        }

        int light = this.getLightColor(partialTicks);
        consumer.addVertex(vector3fs[0].x, vector3fs[0].y, vector3fs[0].z).setUv(0, 1)
                .setColor(this.rCol, this.gCol, this.bCol, this.alpha).setLight(light);
        consumer.addVertex(vector3fs[1].x, vector3fs[1].y, vector3fs[1].z).setUv(0, 0)
                .setColor(this.rCol, this.gCol, this.bCol, this.alpha).setLight(light);
        consumer.addVertex(vector3fs[2].x, vector3fs[2].y, vector3fs[2].z).setUv(1, 0)
                .setColor(this.rCol, this.gCol, this.bCol, this.alpha).setLight(light);
        consumer.addVertex(vector3fs[3].x, vector3fs[3].y, vector3fs[3].z).setUv(1, 1)
                .setColor(this.rCol, this.gCol, this.bCol, this.alpha).setLight(light);
    }

    @Override
    public void tick() {
        super.tick();
    }

    public static class Provider implements ParticleProvider<AbyssBurstParticleOptions> {
        public Provider(SpriteSet sprites) {
        }

        @Nullable
        @Override
        public Particle createParticle(AbyssBurstParticleOptions options, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new AbyssBurstParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, options);
        }
    }
}
