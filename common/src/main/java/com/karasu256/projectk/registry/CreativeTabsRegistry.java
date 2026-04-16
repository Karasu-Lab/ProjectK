package com.karasu256.projectk.registry;

import com.karasu256.projectk.ProjectK;
import com.karasu256.projectk.block.ProjectKBlocks;
import com.karasu256.projectk.data.AbyssEnergyData;
import com.karasu256.projectk.energy.ProjectKEnergies;
import com.karasu256.projectk.item.ProjectKItems;
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
import java.util.function.Supplier;

@KRegistry(modId = ProjectK.MOD_ID, order = 7)
public class CreativeTabsRegistry implements IKRegistryTarget {
    private static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(ProjectK.MOD_ID,
            Registries.CREATIVE_MODE_TAB);

    public interface TabCategory {
        String id();

        void add(RegistrySupplier<? extends ItemLike> item);

        void addStack(Supplier<ItemStack> stack);

        void display(CreativeModeTab.ItemDisplayParameters params, CreativeModeTab.Output output);
    }

    public abstract static class AbstractTabCategory implements TabCategory {
        private final String id;
        protected final List<RegistrySupplier<? extends ItemLike>> items = new ArrayList<>();
        protected final List<Supplier<ItemStack>> stacks = new ArrayList<>();
        private final TabCategory parent;

        public AbstractTabCategory(String id) {
            this(id, null);
        }

        public AbstractTabCategory(String id, TabCategory parent) {
            this.id = id;
            this.parent = parent;
        }

        @Override
        public String id() {
            return id;
        }

        @Override
        public void add(RegistrySupplier<? extends ItemLike> item) {
            items.add(item);
            if (parent != null)
                parent.add(item);
        }

        @Override
        public void addStack(Supplier<ItemStack> stack) {
            stacks.add(stack);
            if (parent != null)
                parent.addStack(stack);
        }

        @Override
        public void display(CreativeModeTab.ItemDisplayParameters params, CreativeModeTab.Output output) {
            items.forEach(i -> output.accept(i.get()));
            stacks.forEach(s -> output.accept(s.get()));
            displayExtra(params, output);
        }

        protected void displayExtra(CreativeModeTab.ItemDisplayParameters params, CreativeModeTab.Output output) {
        }

        public abstract ItemStack getIcon();
    }

    public static final TabCategory GENERAL = new AbstractTabCategory("general") {
        @Override
        public ItemStack getIcon() {
            return new ItemStack(ProjectKBlocks.ABYSS_GENERATOR.get());
        }

        @Override
        protected void displayExtra(CreativeModeTab.ItemDisplayParameters params, CreativeModeTab.Output output) {
            displayAbyssCores(output);
            displayCreativeStorage(output);
        }
    };

    public static final TabCategory BUILDING_BLOCKS = new DelegatingCategory("building_blocks", GENERAL) {
        @Override
        public ItemStack getIcon() {
            return new ItemStack(ProjectKBlocks.KARASIUM_ORE.get());
        }
    };

    public static final TabCategory INGOT = new DelegatingCategory("ingot", GENERAL) {
        @Override
        public ItemStack getIcon() {
            return new ItemStack(ProjectKItems.KARASIUM.get());
        }
    };

    public static final TabCategory FLUIDS = new DelegatingCategory("fluids", GENERAL) {
        @Override
        public ItemStack getIcon() {
            return new ItemStack(ProjectKItems.ABYSS_ENERGY_BUCKETS.values().iterator().next().get());
        }
    };

    public static final TabCategory MACHINES = new DelegatingCategory("machines", GENERAL) {
        @Override
        public ItemStack getIcon() {
            return new ItemStack(ProjectKBlocks.ABYSS_MAGIC_TABLE.get());
        }

        @Override
        protected void displayExtra(CreativeModeTab.ItemDisplayParameters params, CreativeModeTab.Output output) {
            displayCreativeStorage(output);
        }
    };

    public static final TabCategory MATERIALS = new DelegatingCategory("materials", GENERAL) {
        @Override
        public ItemStack getIcon() {
            return new ItemStack(ProjectKItems.ABYSS_WRENCH.get());
        }

        @Override
        protected void displayExtra(CreativeModeTab.ItemDisplayParameters params, CreativeModeTab.Output output) {
            displayAbyssCores(output);
        }
    };

    private static abstract class DelegatingCategory extends AbstractTabCategory {
        public DelegatingCategory(String id, TabCategory parent) {
            super(id, parent);
        }
    }

    static {
        registerTab(GENERAL);
        registerTab(BUILDING_BLOCKS);
        registerTab(INGOT);
        registerTab(FLUIDS);
        registerTab(MACHINES);
        registerTab(MATERIALS);
    }

    private static void registerTab(TabCategory category) {
        TABS.register(category.id(), () -> CreativeModeTab.builder(CreativeModeTab.Row.BOTTOM, 0)
                .title(Component.translatable("category.projectk." + category.id()))
                .icon(((AbstractTabCategory) category)::getIcon).displayItems(category::display).build());
    }

    private static void displayAbyssCores(CreativeModeTab.Output output) {
        for (ProjectKEnergies.EnergyDefinition def : ProjectKEnergies.getDefinitions()) {
            ItemStack stack = new ItemStack(ProjectKBlocks.ABYSS_CORE.get());
            AbyssEnergyData.applyToStack(stack, def.id(), 0L);
            output.accept(stack);
        }
    }

    private static void displayCreativeStorage(CreativeModeTab.Output output) {
        for (ProjectKEnergies.EnergyDefinition def : ProjectKEnergies.getDefinitions()) {
            ItemStack stack = new ItemStack(ProjectKBlocks.CREATIVE_ABYSS_STORAGE.get());
            AbyssEnergyData.applyToStack(stack, def.id(), Long.MAX_VALUE / 2);
            output.accept(stack);
        }
    }

    public static <T extends Item> RegistrySupplier<T> tab(RegistrySupplier<T> registrySupplier, TabCategory category) {
        category.add(registrySupplier);
        return registrySupplier;
    }

    public static Supplier<ItemStack> tabStack(Supplier<ItemStack> stackSupplier, TabCategory category) {
        category.addStack(stackSupplier);
        return stackSupplier;
    }

    public static void register() {
        TABS.register();
    }
}
