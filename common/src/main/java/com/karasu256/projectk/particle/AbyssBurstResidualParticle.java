package com.karasu256.projectk.particle;

import com.karasu256.projectk.energy.AbyssEnergyUtils;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class AbyssBurstResidualParticle extends TextureSheetParticle {
    private final SpriteSet sprites;
    private final AbyssBurstResidualParticleOptions options;
    protected float quadSize;

    public AbyssBurstResidualParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet sprites, AbyssBurstResidualParticleOptions options) {
        super(level, x, y, z, 0, 0, 0);
        this.sprites = sprites;
        this.options = options;
        this.lifetime = 20;
        this.hasPhysics = false;
        this.quadSize = 0.3f + level.random.nextFloat() * 0.1f;
        this.gravity = 0.0F;
        this.xd = xSpeed;
        this.yd = ySpeed;
        this.zd = zSpeed;

        int color = AbyssEnergyUtils.getEnergyColor(options.energyId());
        this.rCol = (float) ((color >> 16) & 0xFF) / 255.0F;
        this.gCol = (float) ((color >> 8) & 0xFF) / 255.0F;
        this.bCol = (float) (color & 0xFF) / 255.0F;
        this.alpha = 1.0F;
        this.setSize(0.5f, 0.5f);

        this.setSpriteFromAge(sprites);
    }

    @Override
    public void tick() {
        super.tick();
        float ageRatio = (float) this.age / (float) this.lifetime;
        this.setSprite(sprites.get((int) ((1.0F - ageRatio) * 7), 7));
    }

    @Override
    @NotNull
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void render(VertexConsumer consumer, Camera camera, float partialTicks) {
        super.render(consumer, camera, partialTicks);
        Vec3 camPos = camera.getPosition();
        float x = (float) (Mth.lerp(partialTicks, this.xo, this.x) - camPos.x);
        float y = (float) (Mth.lerp(partialTicks, this.yo, this.y) - camPos.y);
        float z = (float) (Mth.lerp(partialTicks, this.zo, this.z) - camPos.z);

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

        float alpha = 1.0f - (float) this.age / (float) this.lifetime;
        int light = this.getLightColor(partialTicks);
        consumer.addVertex(vector3fs[0].x, vector3fs[0].y, vector3fs[0].z).setUv(0, 1)
                .setColor(this.rCol, this.gCol, this.bCol, alpha).setLight(light);
        consumer.addVertex(vector3fs[1].x, vector3fs[1].y, vector3fs[1].z).setUv(0, 0)
                .setColor(this.rCol, this.gCol, this.bCol, alpha).setLight(light);
        consumer.addVertex(vector3fs[2].x, vector3fs[2].y, vector3fs[2].z).setUv(1, 0)
                .setColor(this.rCol, this.gCol, this.bCol, alpha).setLight(light);
        consumer.addVertex(vector3fs[3].x, vector3fs[3].y, vector3fs[3].z).setUv(1, 1)
                .setColor(this.rCol, this.gCol, this.bCol, alpha).setLight(light);
    }

    public static class Provider implements ParticleProvider<AbyssBurstResidualParticleOptions> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Nullable
        @Override
        public Particle createParticle(AbyssBurstResidualParticleOptions options, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new AbyssBurstResidualParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, sprites, options);
        }
    }
}
