package com.karasu256.projectk.registry;

import com.karasu256.projectk.ProjectK;
import com.karasu256.projectk.block.ProjectKBlocks;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.karasuniki.karasunikilib.api.registry.IKRegistryTarget;
import net.karasuniki.karasunikilib.api.registry.KRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.ArrayList;
import java.util.List;

@KRegistry(modId = ProjectK.MOD_ID, order = 7)
public class CreativeTabsRegistry implements IKRegistryTarget {
    private static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(ProjectK.MOD_ID, Registries.CREATIVE_MODE_TAB);
    private static final List<RegistrySupplier<? extends ItemLike>> ITEMS = new ArrayList<>();

    public static final RegistrySupplier<CreativeModeTab> TAB = TABS.register(id(), () -> CreativeModeTab.builder(CreativeModeTab.Row.BOTTOM, ITEMS.size()).title(title()).icon(CreativeTabsRegistry::icon).displayItems(CreativeTabsRegistry::displayItems).build());

    private static String id() {
        return ProjectK.MOD_ID;
    }

    private static Component title() {
        return Component.translatable("category.projectk");
    }

    private static ItemStack icon() {
        return new ItemStack(ProjectKBlocks.ABYSS_GENERATOR.get());
    }

    private static void displayItems(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output) {
        for (RegistrySupplier<? extends ItemLike> item : ITEMS) {
            output.accept(item.get());
        }
    }

    public static <T extends Item> RegistrySupplier<T> tab(RegistrySupplier<T> registrySupplier) {
        ITEMS.add(registrySupplier);
        return registrySupplier;
    }

    public static void register() {
        TABS.register();
    }
}
