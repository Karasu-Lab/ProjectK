package com.karasu256.projectk.energy;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.jetbrains.annotations.NotNull;

public interface IAbyssEnergy extends IProjectKEnergy {
    AbyssEnergy getAbyssEnergy();

    static long calculateEnergy(@NotNull LivingEntity entity) {
        float maxHealth = entity.getMaxHealth();
        double attackDamage = 0;
        if (entity.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE)) {
            attackDamage = entity.getAttributeValue(Attributes.ATTACK_DAMAGE);
        }
        return (long) (maxHealth * (1.0 + attackDamage * 0.1));
    }
}
