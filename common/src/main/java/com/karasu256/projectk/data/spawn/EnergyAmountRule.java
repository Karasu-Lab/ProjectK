package com.karasu256.projectk.data.spawn;

import com.karasu256.projectk.energy.IAbyssEnergy;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.LivingEntity;

public record EnergyAmountRule(AmountType type, long fixedAmount) {
    public static final Codec<EnergyAmountRule> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            AmountType.CODEC.fieldOf("type").forGetter(EnergyAmountRule::type),
            Codec.LONG.optionalFieldOf("fixed", 0L).forGetter(EnergyAmountRule::fixedAmount)
    ).apply(instance, EnergyAmountRule::new));

    public long resolve(LivingEntity entity) {
        if (type == AmountType.FIXED) {
            return fixedAmount;
        }
        return IAbyssEnergy.calculateEnergy(entity);
    }

    public enum AmountType {
        BUILT_IN("built_in"),
        FIXED("fixed");

        public static final Codec<AmountType> CODEC = Codec.STRING.comapFlatMap(
                AmountType::fromName,
                AmountType::id
        );
        private final String id;

        AmountType(String id) {
            this.id = id;
        }

        private static DataResult<AmountType> fromName(String name) {
            String normalized = name.toLowerCase();
            for (AmountType type : values()) {
                if (type.id.equals(normalized)) {
                    return DataResult.success(type);
                }
            }
            return DataResult.error(() -> "Unknown amount type: " + name);
        }

        public String id() {
            return id;
        }
    }
}
