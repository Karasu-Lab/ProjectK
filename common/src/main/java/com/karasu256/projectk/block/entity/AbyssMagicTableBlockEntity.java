package com.karasu256.projectk.block.entity;

import com.karasu256.projectk.block.entity.impl.AbstractAbyssMachineBlockEntity;
import com.karasu256.projectk.data.AbyssEnergyData;
import com.karasu256.projectk.energy.EnergyKeys;
import com.karasu256.projectk.energy.ProjectKEnergies;
import com.karasu256.projectk.menu.AbyssMagicTableMenu;
import com.karasu256.projectk.recipe.AbyssMagicTableRecipe;
import com.karasu256.projectk.recipe.ProjectKRecipes;
import com.karasu256.projectk.registry.ProjectKMachineCapacities;
import com.karasu256.projectk.utils.Id;
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

public class AbyssMagicTableBlockEntity extends AbstractAbyssMachineBlockEntity implements MenuProvider {
    private static final int BASE_CRAFT_TIME = 100;
    private static final int MAX_TIER = 3;
    private static final int DEFAULT_TIER = 1;
    private int progress = 0;
    @Nullable
    private ResourceLocation lockedRecipeId;

    public AbyssMagicTableBlockEntity(BlockPos pos, BlockState state) {
        super(ProjectKBlockEntities.ABYSS_MAGIC_TABLE.get(), pos, state, ProjectKMachineCapacities.ABYSS_MAGIC_TABLE);
        addItemSlot(Id.id("input"));
        addItemSlot(Id.id("output"));
    }

    public static void tick(Level level, BlockPos pos, BlockState state, AbyssMagicTableBlockEntity be) {
        if (level.isClientSide)
            return;
        be.serverTick();
    }


    private void serverTick() {
        ItemStack input = getInputItem();
        if (input.isEmpty()) {
            resetProgress();
            return;
        }

        long currentAmount = getEnergyAmount();
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
            markDirtyAndSync();
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
        ItemStack output = getOutputItem();
        if (output.isEmpty()) {
            setOutputItem(result);
        } else {
            output.grow(result.getCount());
            setOutputItem(output);
        }
    }

    private boolean canAcceptOutput(ItemStack result) {
        if (result.isEmpty()) {
            return false;
        }
        ItemStack output = getOutputItem();
        if (output.isEmpty()) {
            return true;
        }
        if (!ItemStack.isSameItemSameComponents(output, result)) {
            return false;
        }
        int maxStack = Math.min(output.getMaxStackSize(), result.getMaxStackSize());
        return output.getCount() + result.getCount() <= maxStack;
    }

    private void lockRecipe(RecipeHolder<AbyssMagicTableRecipe> recipe) {
        lockedRecipeId = recipe.id();
        progress = 0;
        markDirtyAndSync();
    }

    private void resetProgress() {
        progress = 0;
        lockedRecipeId = null;
        markDirtyAndSync();
    }

    public ItemStack getInputItem() {
        return heldItems.getFirst().getHeldItem();
    }

    public void setInputItem(ItemStack stack) {
        heldItems.getFirst().setHeldItem(stack);
        markDirtyAndSync();
    }

    public ItemStack getOutputItem() {
        return heldItems.get(1).getHeldItem();
    }

    public void setOutputItem(ItemStack stack) {
        heldItems.get(1).setHeldItem(stack);
        markDirtyAndSync();
    }

    public int getDataValue(int index) {
        return switch (index) {
            case 0 -> progress;
            case 1 -> getCraftTime();
            case 2 -> (int) getEnergyAmount();
            case 3 -> (int) (getEnergyAmount() >>> 32);
            case 4 -> (int) (long) getEnergyCapacity();
            case 5 -> (int) ((long) getEnergyCapacity() >>> 32);
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
        saveNbt(nbt, EnergyKeys.PROGRESS, progress, registries);
        saveNbt(nbt, EnergyKeys.LOCKED_RECIPE, lockedRecipeId, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        super.loadAdditional(nbt, registries);
        Integer p = loadNbt(nbt, EnergyKeys.PROGRESS, Integer.class, registries);
        progress = p != null ? p : 0;
        lockedRecipeId = loadNbt(nbt, EnergyKeys.LOCKED_RECIPE, ResourceLocation.class, registries);
    }
}
