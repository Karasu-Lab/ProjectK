package com.karasu256.projectk.block.entity;

import com.karasu256.projectk.block.custom.AbyssEnchanter;
import com.karasu256.projectk.block.entity.impl.AbstractAbyssMachineBlockEntity;
import com.karasu256.projectk.data.AbyssEnchanterTier;
import com.karasu256.projectk.data.AbyssEnchanterTierManager;
import com.karasu256.projectk.data.AbyssEnergyData;
import com.karasu256.projectk.enchant.ProjectKEnchantments;
import com.karasu256.projectk.energy.ProjectKEnergies;
import com.karasu256.projectk.menu.AbyssEnchanterMenu;
import com.karasu256.projectk.registry.ProjectKMachineCapacities;
import com.karasu256.projectk.registry.ProjectKTags;
import com.karasu256.projectk.utils.Id;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AbyssEnchanterBlockEntity extends AbstractAbyssMachineBlockEntity implements MenuProvider {
    private static final int OPTION_COUNT = 3;
    private static final int MAX_TIER = 3;
    private static final int DEFAULT_TIER = 1;

    public AbyssEnchanterBlockEntity(BlockPos pos, BlockState state) {
        super(ProjectKBlockEntities.ABYSS_ENCHANTER.get(), pos, state, ProjectKMachineCapacities.ABYSS_ENCHANTER);
        addItemSlot(Id.id("input"));
        addItemSlot(Id.id("output"));
    }

    public static void tick(Level level, BlockPos pos, BlockState state, AbyssEnchanterBlockEntity be) {
    }



    public boolean applyEnchantment(int option, Player player) {
        if (level == null || level.isClientSide) {
            return false;
        }
        if (option < 0 || option >= OPTION_COUNT) {
            return false;
        }
        ItemStack input = getInputItem();
        if (input.isEmpty() || isValidInput(input)) {
            return false;
        }
        if (isInputEnchanted(input)) {
            return false;
        }
        if (!canAcceptOutput()) {
            return false;
        }

        AbyssEnchanterTier tier = getTier(option);
        if (tier == null) {
            return false;
        }
        if (getEnergyAmount() < tier.cost()) {
            return false;
        }

        enchant(input, tier);
        return true;
    }

    private boolean canAcceptOutput() {
        return getOutputItem().isEmpty();
    }

    @Nullable
    private AbyssEnchanterTier getTier(int index) {
        List<AbyssEnchanterTier> tiers = new ArrayList<>(AbyssEnchanterTierManager.getTiers());
        if (tiers.isEmpty()) {
            return null;
        }
        tiers.sort(Comparator.comparingInt(AbyssEnchanterTier::level));
        if (index < 0 || index >= tiers.size()) {
            return null;
        }
        return tiers.get(index);
    }

    private void enchant(ItemStack input, AbyssEnchanterTier tier) {
        ResourceLocation energyId = getAbyssEnergyId();
        if (energyId == null) {
            return;
        }
        long extracted = extract(energyId, tier.cost(), false);
        if (extracted < tier.cost()) {
            return;
        }

        ItemStack result = buildResult(input, tier);
        if (result.isEmpty()) {
            return;
        }

        if (input.getCount() == 1) {
            setInputItem(ItemStack.EMPTY);
        } else {
            ItemStack next = input.copy();
            next.shrink(1);
            setInputItem(next);
        }

        setOutputItem(result);
        markDirtyAndSync();
    }

    private ItemStack buildResult(ItemStack input, AbyssEnchanterTier tier) {
        ItemStack result;
        if (input.is(ProjectKTags.Items.BOOKS) || input.is(Items.BOOK)) {
            result = new ItemStack(Items.ENCHANTED_BOOK);
        } else {
            result = input.copy();
            result.setCount(1);
        }

        var enchantmentRegistry = level == null ? null : level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        if (enchantmentRegistry == null) {
            return ItemStack.EMPTY;
        }
        var enchantment = enchantmentRegistry.getOrThrow(ProjectKEnchantments.ABYSS_BOOSTER_KEY);
        boolean isBook = result.is(Items.ENCHANTED_BOOK);
        var component = isBook ? DataComponents.STORED_ENCHANTMENTS : DataComponents.ENCHANTMENTS;
        ItemEnchantments base = result.getOrDefault(component, ItemEnchantments.EMPTY);
        ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(base);
        mutable.set(enchantment, tier.level());
        result.set(component, mutable.toImmutable());
        AbyssEnergyData.applyToStack(result, getAbyssEnergyId(), tier.cost() / 2);
        return result;
    }

    public ItemStack getInputItem() {
        return heldItems.get(0).getHeldItem();
    }

    public void setInputItem(ItemStack stack) {
        heldItems.get(0).setHeldItem(stack);
    }

    public ItemStack getOutputItem() {
        return heldItems.get(1).getHeldItem();
    }

    public void setOutputItem(ItemStack stack) {
        heldItems.get(1).setHeldItem(stack);
    }

    @Override
    public int getMaxTier() {
        return MAX_TIER;
    }

    @Override
    public int getDefaultTier() {
        return DEFAULT_TIER;
    }

    public int getDataValue(int index) {
        if (!shouldExposeOptions()) {
            return switch (index) {
                case 6 -> (int) getEnergyAmount();
                case 7 -> (int) (getEnergyAmount() >>> 32);
                case 8 -> (int) (long) getEnergyCapacity();
                case 9 -> (int) (long) (getEnergyCapacity() >>> 32);
                case 10 -> getAbyssEnergyId() == null ? 0 : ProjectKEnergies.getModelIndex(getAbyssEnergyId());
                case 11, 12, 13 -> -1;
                default -> 0;
            };
        }
        return switch (index) {
            case 0 -> getTierLevel(0);
            case 1 -> getTierLevel(1);
            case 2 -> getTierLevel(2);
            case 3 -> getTierCost(0);
            case 4 -> getTierCost(1);
            case 5 -> getTierCost(2);
            case 6 -> (int) getEnergyAmount();
            case 7 -> (int) (getEnergyAmount() >>> 32);
            case 8 -> (int) (long) getEnergyCapacity();
            case 9 -> (int) (long) (getEnergyCapacity() >>> 32);
            case 10 -> getAbyssEnergyId() == null ? 0 : ProjectKEnergies.getModelIndex(getAbyssEnergyId());
            case 11 -> getOptionEnchantmentId(0);
            case 12 -> getOptionEnchantmentId(1);
            case 13 -> getOptionEnchantmentId(2);
            case 14 -> getOptionEnchantmentLevel(0);
            case 15 -> getOptionEnchantmentLevel(1);
            case 16 -> getOptionEnchantmentLevel(2);
            default -> 0;
        };
    }

    public void setDataValue(int index, int value) {
    }

    private int getTierLevel(int index) {
        AbyssEnchanterTier tier = getTier(index);
        return tier == null ? 0 : tier.level();
    }

    private int getTierCost(int index) {
        AbyssEnchanterTier tier = getTier(index);
        return tier == null ? 0 : (int) Math.min(Integer.MAX_VALUE, tier.cost());
    }

    private boolean shouldExposeOptions() {
        ItemStack input = getInputItem();
        if (input.isEmpty() || isValidInput(input)) {
            return false;
        }
        return !isInputEnchanted(input);
    }

    private boolean isInputEnchanted(ItemStack stack) {
        ItemEnchantments enchants = stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        ItemEnchantments stored = stack.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY);
        return hasAbyssEnchant(enchants) || hasAbyssEnchant(stored);
    }

    private boolean hasAbyssEnchant(ItemEnchantments enchants) {
        if (enchants.isEmpty()) {
            return false;
        }
        for (Holder<Enchantment> holder : enchants.keySet()) {
            if (holder.is(ProjectKTags.Enchantments.ABYSS_ENCHANT) && enchants.getLevel(holder) > 0) {
                return true;
            }
        }
        return false;
    }

    private int getOptionEnchantmentId(int index) {
        Enchantment enchantment = getOptionEnchantment(index);
        if (enchantment == null) {
            return -1;
        }
        if (level == null) {
            return -1;
        }
        return level.registryAccess().registryOrThrow(Registries.ENCHANTMENT).getId(enchantment);
    }

    private int getOptionEnchantmentLevel(int index) {
        AbyssEnchanterTier tier = getTier(index);
        return tier == null ? 0 : tier.level();
    }

    @Nullable
    private Enchantment getOptionEnchantment(int index) {
        if (level == null) {
            return null;
        }
        var registry = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        return registry.getOrThrow(ProjectKEnchantments.ABYSS_BOOSTER_KEY).value();
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.projectk.abyss_enchanter");
    }

    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory inventory, Player player) {
        return new AbyssEnchanterMenu(syncId, inventory, this);
    }

    private boolean isValidInput(ItemStack stack) {
        if (stack.is(ProjectKTags.Items.BOOKS) || stack.is(Items.BOOK)) {
            return false;
        }
        return !stack.is(ItemTags.AXES) && !stack.is(ItemTags.HOES) && !stack.is(ItemTags.PICKAXES) && !stack.is(
                ItemTags.SHOVELS) && !stack.is(ItemTags.SWORDS) && !stack.is(ItemTags.TRIMMABLE_ARMOR) && !stack.is(
                Items.MACE) && !stack.is(Items.TRIDENT);
    }
}
