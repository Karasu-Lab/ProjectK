package com.karasu256.projectk.registry;

import com.karasu256.projectk.ProjectK;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.karasuniki.karasunikilib.api.registry.IKRegistryTarget;
import net.karasuniki.karasunikilib.api.registry.KRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.function.Supplier;

@KRegistry(modId = ProjectK.MOD_ID, order = 7)
public class EnchantmentsRegistry implements IKRegistryTarget {
    private static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(ProjectK.MOD_ID, Registries.ENCHANTMENT);

    public static void register() {
        ENCHANTMENTS.register();
    }

    public static <T extends Enchantment> RegistrySupplier<T> enchantment(String id, Supplier<T> enchantment) {
        return ENCHANTMENTS.register(id, enchantment);
    }
}
