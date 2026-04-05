package com.karasu256.projectk.block.entity;

import com.karasu256.projectk.block.custom.AbyssEnchanter;
import com.karasu256.projectk.block.entity.impl.AbstractPKEnergyBlockEntity;
import com.karasu256.projectk.data.AbyssEnchanterTier;
import com.karasu256.projectk.data.AbyssEnchanterTierManager;
import com.karasu256.projectk.data.AbyssEnergyData;
import com.karasu256.projectk.enchant.ProjectKEnchantments;
import com.karasu256.projectk.energy.*;
import com.karasu256.projectk.menu.AbyssEnchanterMenu;
import com.karasu256.projectk.registry.ProjectKTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
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

public class AbyssEnchanterBlockEntity extends AbstractPKEnergyBlockEntity<AbyssEnergy> implements MenuProvider, IEnergyListHolder, IMaxEnrgyInfo, ITierInfo {
    private static final int OPTION_COUNT = 3;
    private static final int MAX_TIER = 3;
    private static final int DEFAULT_TIER = 1;
    private final long baseMaxEnergy;
    private ItemStack outputItem = ItemStack.EMPTY;
    private long maxEnergy;
    private int tier;

    public AbyssEnchanterBlockEntity(BlockPos pos, BlockState state) {
        super(ProjectKBlockEntities.ABYSS_ENCHANTER.get(), pos, state, resolveCapacity(state));
        this.baseMaxEnergy = resolveCapacity(state);
        this.tier = DEFAULT_TIER;
        refreshMaxEnergy();
    }

    public static void tick(Level level, BlockPos pos, BlockState state, AbyssEnchanterBlockEntity be) {
    }

    private static long resolveCapacity(BlockState state) {
        if (state.getBlock() instanceof AbyssEnchanter enchanter) {
            return enchanter.getCapacity();
        }
        return 0L;
    }

    @Override
    protected AbyssEnergy createEnergy() {
        return new AbyssEnergy(0L);
    }

    @Override
    public List<EnergyEntry> getEnergyEntries() {
        ResourceLocation id = getAbyssEnergyId();
        if (id == null || getAmount() <= 0) {
            return List.of();
        }
        return List.of(new EnergyEntry(id, getAmount(), getCapacity(), false));
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
        if (getAmount() < tier.cost()) {
            return false;
        }

        enchant(input, tier);
        return true;
    }

    private boolean canAcceptOutput() {
        return outputItem.isEmpty();
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

        outputItem = result;
        setChanged();
        sync();
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
        return getHeldItem();
    }

    public void setInputItem(ItemStack stack) {
        setHeldItem(stack);
    }

    public ItemStack getOutputItem() {
        return outputItem;
    }

    public void setOutputItem(ItemStack stack) {
        outputItem = stack;
        setChanged();
        sync();
    }

    private void refreshMaxEnergy() {
        setMaxEnergy(getTieredMaxEnergy(getTier()));
        setMaxEnergyCapacity(getMaxEnergy());
    }

    @Override
    public long getBaseMaxEnergy() {
        return baseMaxEnergy;
    }

    @Override
    public long getMaxEnergy() {
        return maxEnergy;
    }

    @Override
    public void setMaxEnergy(long maxEnergy) {
        this.maxEnergy = maxEnergy;
    }

    @Override
    public int getTier() {
        return tier;
    }

    @Override
    public void setTier(int tier) {
        this.tier = clampTier(tier);
        refreshMaxEnergy();
        setChanged();
        sync();
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
                case 6 -> (int) getAmount();
                case 7 -> (int) (getAmount() >>> 32);
                case 8 -> (int) getCapacity();
                case 9 -> (int) (getCapacity() >>> 32);
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
            case 6 -> (int) getAmount();
            case 7 -> (int) (getAmount() >>> 32);
            case 8 -> (int) getCapacity();
            case 9 -> (int) (getCapacity() >>> 32);
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

    @Override
    protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        super.saveAdditional(nbt, registries);
        saveTier(nbt);
        saveMaxEnergy(nbt);
        if (!outputItem.isEmpty()) {
            nbt.put(EnergyKeys.MAGIC_TABLE_OUTPUT_ITEM.toString(), outputItem.save(registries));
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag nbt = super.getUpdateTag(registries);
        saveAdditional(nbt, registries);
        return nbt;
    }

    @Override
    protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        super.loadAdditional(nbt, registries);
        loadTier(nbt);
        loadMaxEnergy(nbt);
        refreshMaxEnergy();
        if (nbt.contains(EnergyKeys.MAGIC_TABLE_OUTPUT_ITEM.toString())) {
            outputItem = ItemStack.parse(registries, nbt.getCompound(EnergyKeys.MAGIC_TABLE_OUTPUT_ITEM.toString()))
                    .orElse(ItemStack.EMPTY);
        } else {
            outputItem = ItemStack.EMPTY;
        }
    }
}
