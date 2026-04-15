package com.karasu256.projectk.particle;

import com.karasu256.projectk.ProjectK;
import com.mojang.serialization.MapCodec;
import dev.architectury.registry.registries.RegistrySupplier;
import net.karasuniki.karasunikilib.api.registry.IKRegistryTarget;
import net.karasuniki.karasunikilib.api.registry.KRegistry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

@KRegistry(modId = ProjectK.MOD_ID, order = 1)
public class ProjectKParticles implements IKRegistryTarget {
    public static RegistrySupplier<ParticleType<AbyssParticleOptions>> ABYSS_PARTICLE;
    public static RegistrySupplier<ParticleType<AbyssPortalParticleOptions>> ABYSS_PORTAL_PARTICLE;
    public static RegistrySupplier<ParticleType<AbyssLaserParticleOptions>> ABYSS_LASER_PARTICLE;
    public static RegistrySupplier<ParticleType<AbyssBurstParticleOptions>> ABYSS_BURST_PARTICLE;
    public static RegistrySupplier<ParticleType<AbyssBurstResidualParticleOptions>> ABYSS_BURST_RESIDUAL_PARTICLE;

    public static void register() {
    }
}
