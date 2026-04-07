package com.karasu256.projectk.block.entity;

import com.karasu256.projectk.block.custom.AbyssAlchemyBlendMachine;
import com.karasu256.projectk.block.entity.impl.AbstractAbyssMachineBlockEntity;
import com.karasu256.projectk.data.AbyssEnergyData;
import com.karasu256.projectk.energy.EnergyKeys;
import com.karasu256.projectk.energy.ProjectKEnergies;
import com.karasu256.projectk.menu.AbyssAlchemyBlendMachineMenu;
import com.karasu256.projectk.recipe.AbyssAlchemyBlendRecipe;
import com.karasu256.projectk.recipe.ProjectKRecipes;
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

public class AbyssAlchemyBlendMachineBlockEntity extends AbstractAbyssMachineBlockEntity implements MenuProvider {
    private static final int BASE_CRAFT_TIME = 120;
    private static final int MAX_TIER = 3;
    private static final int DEFAULT_TIER = 1;
    private int progress = 0;
    @Nullable
    private ResourceLocation lockedRecipeId;

    public AbyssAlchemyBlendMachineBlockEntity(BlockPos pos, BlockState state) {
        super(ProjectKBlockEntities.ABYSS_ALCHEMY_BLEND_MACHINE.get(), pos, state, resolveCapacity(state));
        addItemSlot(Id.id("input"));
        addItemSlot(Id.id("output"));
    }

    public static void tick(Level level, BlockPos pos, BlockState state, AbyssAlchemyBlendMachineBlockEntity be) {
        if (level.isClientSide)
            return;
        be.serverTick();
    }

    private static long resolveCapacity(BlockState state) {
        if (state.getBlock() instanceof AbyssAlchemyBlendMachine machine) {
            return machine.getCapacity();
        }
        return 0L;
    }

    private void serverTick() {
        ItemStack input = getInputItem();
        if (input.isEmpty()) {
            resetProgress();
            return;
        }

        RecipeHolder<AbyssAlchemyBlendRecipe> activeHolder = getLockedRecipe();
        AbyssAlchemyBlendRecipe activeRecipe = activeHolder == null ? null : activeHolder.value();
        if (activeRecipe == null) {
            RecipeHolder<AbyssAlchemyBlendRecipe> startRecipe = findMatchingRecipe(input);
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
            RecipeHolder<AbyssAlchemyBlendRecipe> switchRecipe = findMatchingRecipe(input);
            if (switchRecipe != null && !switchRecipe.id().equals(activeHolder.id())) {
                lockRecipe(switchRecipe);
                activeRecipe = switchRecipe.value();
            }
        }

        if (!hasEnoughEnergy(activeRecipe)) {
            resetProgress();
            return;
        }

        int previous = progress;
        progress++;
        if (progress != previous) {
            markDirtyAndSync();
        }
        if (progress >= getCraftTime(activeRecipe)) {
            craft(activeRecipe);
            resetProgress();
        }
    }

    private boolean hasEnoughEnergy(AbyssAlchemyBlendRecipe recipe) {
        long first = getEnergyAmount(recipe.energyId1());
        long second = getEnergyAmount(recipe.energyId2());
        return first >= recipe.energyAmount1() && second >= recipe.energyAmount2();
    }

    public int getCraftTime(AbyssAlchemyBlendRecipe recipe) {
        return getCraftTimeForTier(BASE_CRAFT_TIME);
    }

    private void craft(AbyssAlchemyBlendRecipe recipe) {
        ItemStack result = recipe.result().copy();
        ResourceLocation resultEnergyId = resolveResultEnergyId(recipe);
        long resultAmount = recipe.energyAmount1() + recipe.energyAmount2();
        AbyssEnergyData.applyToStack(result, resultEnergyId, resultAmount);
        if (!canAcceptOutput(result)) {
            return;
        }
        ItemStack input = getInputItem();
        int needed = recipe.input().count();
        if (input.getCount() < needed) {
            return;
        }
        long extracted1 = extract(recipe.energyId1(), recipe.energyAmount1(), false);
        long extracted2 = extract(recipe.energyId2(), recipe.energyAmount2(), false);
        if (extracted1 < recipe.energyAmount1() || extracted2 < recipe.energyAmount2()) {
            return;
        }
        if (input.getCount() == needed) {
            setInputItem(ItemStack.EMPTY);
        } else {
            input.shrink(needed);
            setInputItem(input);
        }
        ItemStack outputItem = getOutputItem();
        if (outputItem.isEmpty()) {
            setOutputItem(result);
        } else {
            outputItem.grow(result.getCount());
            setOutputItem(outputItem);
        }
    }

    private ResourceLocation resolveResultEnergyId(AbyssAlchemyBlendRecipe recipe) {
        long amount1 = recipe.energyAmount1();
        long amount2 = recipe.energyAmount2();
        if (amount1 > amount2) {
            return recipe.energyId1();
        }
        if (amount2 > amount1) {
            return recipe.energyId2();
        }
        return ProjectKEnergies.getEnergyIdByKind(ProjectKEnergies.EnergyKind.NEUTRAL);
    }

    private boolean canAcceptOutput(ItemStack result) {
        if (result.isEmpty()) {
            return false;
        }
        ItemStack outputItem = getOutputItem();
        if (outputItem.isEmpty()) {
            return true;
        }
        if (!ItemStack.isSameItemSameComponents(outputItem, result)) {
            return false;
        }
        int maxStack = Math.min(outputItem.getMaxStackSize(), result.getMaxStackSize());
        return outputItem.getCount() + result.getCount() <= maxStack;
    }

    private void lockRecipe(RecipeHolder<AbyssAlchemyBlendRecipe> recipe) {
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
        return heldItems.get(0).getHeldItem();
    }

    public void setInputItem(ItemStack stack) {
        heldItems.get(0).setHeldItem(stack);
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
            case 1 -> getCraftTime(getLockedRecipe() != null ? getLockedRecipe().value() : null);
            case 2 -> (int) getEnergyAmountByIndex(0);
            case 3 -> (int) (getEnergyAmountByIndex(0) >>> 32);
            case 4 -> (int) (long) getEnergyCapacity();
            case 5 -> (int) (long) (getEnergyCapacity() >>> 32);
            case 6 -> (int) getEnergyAmountByIndex(1);
            case 7 -> (int) (getEnergyAmountByIndex(1) >>> 32);
            case 8 -> (int) (long) getEnergyCapacity();
            case 9 -> (int) (long) (getEnergyCapacity() >>> 32);
            case 10 -> getEnergyIdByIndex(0) == null ? 0 : ProjectKEnergies.getModelIndex(getEnergyIdByIndex(0));
            case 11 -> getEnergyIdByIndex(1) == null ? 0 : ProjectKEnergies.getModelIndex(getEnergyIdByIndex(1));
            default -> 0;
        };
    }

    private long getEnergyAmountByIndex(int index) {
        AbyssEnergyData data = getEnergyByIndex(index);
        return data == null ? 0L : data.amountOrZero();
    }

    private ResourceLocation getEnergyIdByIndex(int index) {
        AbyssEnergyData data = getEnergyByIndex(index);
        return data == null ? null : data.energyId();
    }

    public void setDataValue(int index, int value) {
        if (index == 0) {
            progress = value;
        }
    }

    public int getProgress() {
        return progress;
    }

    public int getMaxProgress() {
        return getLockedRecipe() != null ? getCraftTime(getLockedRecipe().value()) : BASE_CRAFT_TIME;
    }

    @Override
    public int getMaxTier() {
        return MAX_TIER;
    }

    @Override
    public int getDefaultTier() {
        return DEFAULT_TIER;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.projectk.abyss_alchemy_blend_machine");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory inventory, Player player) {
        return new AbyssAlchemyBlendMachineMenu(syncId, inventory, this);
    }

    @Override
    public int getMaxEnergyTypes() {
        return 2;
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

    @Nullable
    @SuppressWarnings("unchecked")
    private RecipeHolder<AbyssAlchemyBlendRecipe> getLockedRecipe() {
        if (lockedRecipeId == null || level == null) {
            return null;
        }
        return (RecipeHolder<AbyssAlchemyBlendRecipe>) level.getRecipeManager().byKey(lockedRecipeId)
                .filter(holder -> holder.value() instanceof AbyssAlchemyBlendRecipe).orElse(null);
    }

    @Nullable
    private RecipeHolder<AbyssAlchemyBlendRecipe> findMatchingRecipe(ItemStack input) {
        if (level == null)
            return null;
        List<RecipeHolder<AbyssAlchemyBlendRecipe>> recipes = level.getRecipeManager()
                .getAllRecipesFor(ProjectKRecipes.ABYSS_ALCHEMY_BLEND.get());
        for (RecipeHolder<AbyssAlchemyBlendRecipe> holder : recipes) {
            AbyssAlchemyBlendRecipe recipe = holder.value();
            if (!recipe.matchesInput(input))
                continue;
            return holder;
        }
        return null;
    }
}
