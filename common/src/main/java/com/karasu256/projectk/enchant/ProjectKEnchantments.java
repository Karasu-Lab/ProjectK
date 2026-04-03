package com.karasu256.projectk.enchant;

import com.karasu256.projectk.ProjectK;
import com.karasu256.projectk.utils.Id;
import com.karasu256.projectk.registry.EnchantmentsRegistry;
import dev.architectury.registry.registries.RegistrySupplier;
import net.karasuniki.karasunikilib.api.registry.IKRegistryInitializerTarget;
import net.karasuniki.karasunikilib.api.registry.KRegistryInitializer;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;

@KRegistryInitializer(modId = ProjectK.MOD_ID, order = 6)
public class ProjectKEnchantments implements IKRegistryInitializerTarget {
    public static final ResourceKey<Enchantment> ABYSS_BOOSTER_KEY = ResourceKey.create(Registries.ENCHANTMENT, Id.id("abyss_booster"));
    public static final RegistrySupplier<Enchantment> ABYSS_BOOSTER = EnchantmentsRegistry.enchantment("abyss_booster", AbyssBoosterEnchantment::create);

    public static void init() {
    }
}
