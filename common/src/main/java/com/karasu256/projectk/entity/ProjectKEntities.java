package com.karasu256.projectk.entity;

import com.karasu256.projectk.ProjectK;
import com.karasu256.projectk.utils.Id;
import dev.architectury.registry.registries.RegistrySupplier;
import net.karasuniki.karasunikilib.api.registry.IKRegistryInitializerTarget;
import net.karasuniki.karasunikilib.api.registry.KEntityRegistry;
import net.karasuniki.karasunikilib.api.registry.KRegistryInitializer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

@KRegistryInitializer(modId = ProjectK.MOD_ID, order = 0)
public class ProjectKEntities implements IKRegistryInitializerTarget {
    public static final RegistrySupplier<EntityType<AbyssEnergyEntity>> ABYSS_ENERGY_ENTITY = KEntityRegistry.entity(ProjectK.MOD_ID, "abyss_energy_entity",
            () -> EntityType.Builder.of(AbyssEnergyEntity::new, MobCategory.MISC)
                    .sized(0.1f, 0.1f)
                    .build(Id.id("abyss_energy_entity").toString()));

    public static void init() {
    }
}
