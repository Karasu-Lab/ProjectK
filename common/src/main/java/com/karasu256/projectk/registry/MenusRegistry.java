package com.karasu256.projectk.registry;

import com.karasu256.projectk.ProjectK;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.karasuniki.karasunikilib.api.registry.IKRegistryTarget;
import net.karasuniki.karasunikilib.api.registry.KRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;

import java.util.function.Supplier;

@KRegistry(modId = ProjectK.MOD_ID, order = 9)
public class MenusRegistry implements IKRegistryTarget {
    private static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ProjectK.MOD_ID, Registries.MENU);

    public static void register() {
        MENUS.register();
    }

    public static <T extends MenuType<?>> RegistrySupplier<T> menu(String id, Supplier<T> menu) {
        return MENUS.register(id, menu);
    }
}
