package com.karasu256.projectk.particle;

import com.karasu256.projectk.energy.AbyssEnergyUtils;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AbyssBurstParticle extends TextureSheetParticle {
    private final SpriteSet sprites;
    private final AbyssBurstParticleOptions options;

    public AbyssBurstParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet sprites, AbyssBurstParticleOptions options) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);
        this.sprites = sprites;
        this.options = options;
        this.friction = 0.98F;
        this.gravity = 0.01F;
        this.xd = xSpeed;
        this.yd = ySpeed;
        this.zd = zSpeed;
        this.quadSize *= 1.5F;
        this.hasPhysics = true;
        this.lifetime = 20 + level.random.nextInt(10);

        int color = AbyssEnergyUtils.getEnergyColor(options.energyId());
        this.rCol = (float) ((color >> 16) & 0xFF) / 255.0F;
        this.gCol = (float) ((color >> 8) & 0xFF) / 255.0F;
        this.bCol = (float) (color & 0xFF) / 255.0F;

        this.setSpriteFromAge(sprites);
    }

    @Override
    @NotNull
    public ParticleRenderType getRenderType() {
        return ProjectKParticleRenderTypes.ABYSS_BURST;
    }

    @Override
    public void tick() {
        super.tick();
        this.setSpriteFromAge(sprites);
    }

    public static class Provider implements ParticleProvider<AbyssBurstParticleOptions> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Nullable
        @Override
        public Particle createParticle(AbyssBurstParticleOptions options, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new AbyssBurstParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, sprites, options);
        }
    }
}
