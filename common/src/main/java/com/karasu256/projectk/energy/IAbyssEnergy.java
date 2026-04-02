package com.karasu256.projectk.energy;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.jetbrains.annotations.NotNull;

public interface IAbyssEnergy extends IProjectKEnergy {
    static long calculateEnergy(@NotNull LivingEntity entity) {
        float maxHealth = entity.getMaxHealth();
        double attackDamage = 0;
        if (entity.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE)) {
            attackDamage = entity.getAttributeValue(Attributes.ATTACK_DAMAGE);
        }
        return (long) (maxHealth * (1.0 + attackDamage * 0.1));
    }

    static boolean isAbyssEnergyId(@NotNull ResourceLocation id) {
        return id.equals(ProjectKEnergies.ABYSS_ENERGY.get().getId())
                || id.equals(ProjectKEnergies.YIN_ABYSS_ENERGY.get().getId())
                || id.equals(ProjectKEnergies.YANG_ABYSS_ENERGY.get().getId());
    }
}
