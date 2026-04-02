package com.karasu256.projectk.menu;

import com.karasu256.projectk.ProjectK;
import com.karasu256.projectk.registry.MenusRegistry;
import dev.architectury.registry.registries.RegistrySupplier;
import net.karasuniki.karasunikilib.api.registry.IKRegistryInitializerTarget;
import net.karasuniki.karasunikilib.api.registry.KRegistryInitializer;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;

@KRegistryInitializer(modId = ProjectK.MOD_ID, order = 5)
public class ProjectKMenus implements IKRegistryInitializerTarget {
    public static final RegistrySupplier<MenuType<AbyssMagicTableMenu>> ABYSS_MAGIC_TABLE = MenusRegistry.menu("abyss_magic_table", () -> new MenuType<>(AbyssMagicTableMenu::new, FeatureFlags.VANILLA_SET));

    public static void init() {
    }
}
