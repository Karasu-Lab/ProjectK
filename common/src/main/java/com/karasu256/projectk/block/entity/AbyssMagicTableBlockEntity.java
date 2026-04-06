package com.karasu256.projectk.block.entity;

import com.karasu256.projectk.block.custom.AbyssMagicTable;
import com.karasu256.projectk.block.entity.impl.AbstractPKEnergyBlockEntity;
import com.karasu256.projectk.compat.wthit.IWthitCustomEnergy;
import com.karasu256.projectk.data.AbyssEnergyData;
import com.karasu256.projectk.energy.*;
import com.karasu256.projectk.menu.AbyssMagicTableMenu;
import com.karasu256.projectk.recipe.AbyssMagicTableRecipe;
import com.karasu256.projectk.recipe.ProjectKRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AbyssMagicTableBlockEntity extends AbstractPKEnergyBlockEntity<AbyssEnergy> implements MenuProvider, IEnergyListHolder, IMaxEnrgyInfo, ITierInfo, IWthitCustomEnergy {
    private static final int BASE_CRAFT_TIME = 100;
    private static final int MAX_TIER = 3;
    private static final int DEFAULT_TIER = 1;
    private final long baseMaxEnergy;
    private int progress = 0;
    @Nullable
    private ResourceLocation lockedRecipeId;
    private ItemStack outputItem = ItemStack.EMPTY;
    private long maxEnergy;
    private int tier;

    public AbyssMagicTableBlockEntity(BlockPos pos, BlockState state) {
        super(ProjectKBlockEntities.ABYSS_MAGIC_TABLE.get(), pos, state, resolveCapacity(state));
        this.baseMaxEnergy = resolveCapacity(state);
        this.tier = DEFAULT_TIER;
        refreshMaxEnergy();
    }

    public static void tick(Level level, BlockPos pos, BlockState state, AbyssMagicTableBlockEntity be) {
        if (level.isClientSide)
            return;
        be.serverTick();
    }

    private static long resolveCapacity(BlockState state) {
        if (state.getBlock() instanceof AbyssMagicTable magicTable) {
            return magicTable.getCapacity();
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

    private void serverTick() {
        ItemStack input = getInputItem();
        if (input.isEmpty()) {
            resetProgress();
            return;
        }

        long currentAmount = getAmount();
        ResourceLocation energyId = getAbyssEnergyId();
        if (energyId == null || currentAmount <= 0) {
            resetProgress();
            return;
        }

        RecipeHolder<AbyssMagicTableRecipe> activeHolder = getLockedRecipe();
        AbyssMagicTableRecipe activeRecipe = activeHolder == null ? null : activeHolder.value();
        if (activeRecipe == null) {
            RecipeHolder<AbyssMagicTableRecipe> startRecipe = findMatchingRecipe(energyId, currentAmount, input);
            if (startRecipe == null) {
                resetProgress();
                return;
            }
            lockRecipe(startRecipe);
            activeRecipe = startRecipe.value();
        } else {
            if (!activeRecipe.matchesInput(input)) {
                resetProgress();
                return;
            }
            if (currentAmount < activeRecipe.energyAmount()) {
                resetProgress();
                return;
            }
            RecipeHolder<AbyssMagicTableRecipe> switchRecipe = findMatchingRecipe(energyId, currentAmount, input);
            if (switchRecipe != null && !switchRecipe.id().equals(activeHolder.id())) {
                lockRecipe(switchRecipe);
                activeRecipe = switchRecipe.value();
            }
        }

        int previous = progress;
        progress++;
        if (progress != previous) {
            setChanged();
            sync();
        }
        if (progress >= getCraftTime()) {
            craft(activeRecipe);
            resetProgress();
        }
    }

    private void craft(AbyssMagicTableRecipe recipe) {
        ItemStack result = recipe.result().copy();
        AbyssEnergyData.applyToStack(result, recipe.energyId(), recipe.energyAmount());
        if (!canAcceptOutput(result)) {
            return;
        }
        ItemStack input = getInputItem();
        int needed = recipe.input().count();
        if (input.getCount() < needed) {
            return;
        }
        long extracted = extract(recipe.energyId(), recipe.energyAmount(), false);
        if (extracted < recipe.energyAmount()) {
            return;
        }
        if (input.getCount() == needed) {
            setInputItem(ItemStack.EMPTY);
        } else {
            input.shrink(needed);
            setInputItem(input);
        }
        if (outputItem.isEmpty()) {
            setOutputItem(result);
        } else {
            outputItem.grow(result.getCount());
            setOutputItem(outputItem);
        }
    }

    private boolean canAcceptOutput(ItemStack result) {
        if (result.isEmpty()) {
            return false;
        }
        if (outputItem.isEmpty()) {
            return true;
        }
        if (!ItemStack.isSameItemSameComponents(outputItem, result)) {
            return false;
        }
        int maxStack = Math.min(outputItem.getMaxStackSize(), result.getMaxStackSize());
        return outputItem.getCount() + result.getCount() <= maxStack;
    }

    private void lockRecipe(RecipeHolder<AbyssMagicTableRecipe> recipe) {
        lockedRecipeId = recipe.id();
        progress = 0;
        setChanged();
        sync();
    }

    private void resetProgress() {
        progress = 0;
        lockedRecipeId = null;
        setChanged();
        sync();
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

    public int getDataValue(int index) {
        return switch (index) {
            case 0 -> progress;
            case 1 -> getCraftTime();
            case 2 -> (int) getAmount();
            case 3 -> (int) (getAmount() >>> 32);
            case 4 -> (int) getCapacity();
            case 5 -> (int) (getCapacity() >>> 32);
            case 6 -> getAbyssEnergyId() == null ? 0 : ProjectKEnergies.getModelIndex(getAbyssEnergyId());
            default -> 0;
        };
    }

    public int getProgress() {
        return progress;
    }

    public int getMaxProgress() {
        return getCraftTime();
    }

    private int getCraftTime() {
        return getCraftTimeForTier(BASE_CRAFT_TIME);
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
    }

    @Override
    public int getMaxTier() {
        return MAX_TIER;
    }

    @Override
    public int getDefaultTier() {
        return DEFAULT_TIER;
    }

    public void setDataValue(int index, int value) {
        if (index == 0) {
            progress = value;
        }
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.projectk.abyss_magic_table");
    }

    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory inventory, Player player) {
        return new AbyssMagicTableMenu(syncId, inventory, this);
    }

    @Nullable
    @SuppressWarnings("unchecked")
    private RecipeHolder<AbyssMagicTableRecipe> getLockedRecipe() {
        if (lockedRecipeId == null || level == null) {
            return null;
        }
        return (RecipeHolder<AbyssMagicTableRecipe>) level.getRecipeManager().byKey(lockedRecipeId)
                .filter(holder -> holder.value() instanceof AbyssMagicTableRecipe).orElse(null);
    }

    @Nullable
    private RecipeHolder<AbyssMagicTableRecipe> findMatchingRecipe(ResourceLocation energyId, long currentAmount, ItemStack input) {
        if (level == null)
            return null;
        List<RecipeHolder<AbyssMagicTableRecipe>> recipes = level.getRecipeManager()
                .getAllRecipesFor(ProjectKRecipes.ABYSS_MAGIC_TABLE.get());
        for (RecipeHolder<AbyssMagicTableRecipe> holder : recipes) {
            AbyssMagicTableRecipe recipe = holder.value();
            if (!recipe.energyId().equals(energyId))
                continue;
            if (recipe.energyAmount() > currentAmount)
                continue;
            if (!recipe.matchesInput(input))
                continue;
            return holder;
        }
        return null;
    }

    @Override
    protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        super.saveAdditional(nbt, registries);
        nbt.putInt(EnergyKeys.MAGIC_TABLE_PROGRESS.toString(), progress);
        saveTier(nbt);
        saveMaxEnergy(nbt);
        if (lockedRecipeId != null) {
            nbt.putString(EnergyKeys.MAGIC_TABLE_LOCKED_RECIPE.toString(), lockedRecipeId.toString());
        }
        if (!outputItem.isEmpty()) {
            nbt.put(EnergyKeys.MAGIC_TABLE_OUTPUT_ITEM.toString(), outputItem.save(registries));
        }
    }

    @Override
    protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        super.loadAdditional(nbt, registries);
        progress = nbt.getInt(EnergyKeys.MAGIC_TABLE_PROGRESS.toString());
        loadTier(nbt);
        loadMaxEnergy(nbt);
        refreshMaxEnergy();
        lockedRecipeId = nbt.contains(EnergyKeys.MAGIC_TABLE_LOCKED_RECIPE.toString()) ? ResourceLocation.parse(
                nbt.getString(EnergyKeys.MAGIC_TABLE_LOCKED_RECIPE.toString())) : null;
        outputItem = nbt.contains(EnergyKeys.MAGIC_TABLE_OUTPUT_ITEM.toString()) ? ItemStack.parse(registries,
                        nbt.getCompound(EnergyKeys.MAGIC_TABLE_OUTPUT_ITEM.toString()))
                .orElse(ItemStack.EMPTY) : ItemStack.EMPTY;
    }

    @Override
    public boolean shouldShowDefaultEnergyTooltip() {
        return false;
    }
}
