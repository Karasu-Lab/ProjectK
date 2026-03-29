package com.karasu256.projectk.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AbyssParticle extends TextureSheetParticle {
    private final SpriteSet sprites;
    private final AbyssParticleOptions options;
 
    protected AbyssParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet sprites, AbyssParticleOptions options) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);
        this.sprites = sprites;
        this.options = options;
        this.friction = 1.0F;
        this.gravity = 0.0F;
        this.xd = xSpeed;
        this.yd = ySpeed;
        this.zd = zSpeed;
        this.quadSize *= 0.75F;
        this.hasPhysics = false;
        this.lifetime = 40 + level.random.nextInt(20);
        this.setSpriteFromAge(sprites);
    }
 
    @Override
    @NotNull
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }
 
    @Override
    public void tick() {
        super.tick();
        float ageRatio = (float) this.age / (float) this.lifetime;
        this.setSprite(sprites.get((int) ((1.0F - ageRatio) * 7), 7));
    }
 
    public static class Provider implements ParticleProvider<AbyssParticleOptions> {
        private final SpriteSet sprites;
 
        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }
 
        @Nullable
        @Override
        public Particle createParticle(AbyssParticleOptions options, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new AbyssParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, sprites, options);
        }
    }
}
