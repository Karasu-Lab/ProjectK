package com.karasu256.projectk.registry;

import com.karasu256.projectk.entity.AbyssEnergyEntity;
import com.karasu256.projectk.utils.Id;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import static com.karasu256.projectk.ProjectK.MOD_ID;

public class EntitiesRegistry {
    private static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(MOD_ID, Registries.ENTITY_TYPE);

    public static final RegistrySupplier<EntityType<AbyssEnergyEntity>> ABYSS_ENERGY_ENTITY = ENTITIES.register(Id.id("abyss_energy_entity"),
            () -> EntityType.Builder.of(AbyssEnergyEntity::new, MobCategory.MISC)
                    .sized(0.1f, 0.1f)
                    .build(Id.id("abyss_energy_entity").toString()));

    public static void register() {
        ENTITIES.register();
    }
}
