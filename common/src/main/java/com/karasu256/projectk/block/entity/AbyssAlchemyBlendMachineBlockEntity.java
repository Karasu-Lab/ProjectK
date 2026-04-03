package com.karasu256.projectk.block.entity;

import com.karasu256.projectk.block.custom.AbyssAlchemyBlendMachine;
import com.karasu256.projectk.data.AbyssEnergyData;
import com.karasu256.projectk.energy.EnergyKeys;
import com.karasu256.projectk.energy.ProjectKEnergies;
import com.karasu256.projectk.menu.AbyssAlchemyBlendMachineMenu;
import com.karasu256.projectk.recipe.AbyssAlchemyBlendRecipe;
import com.karasu256.projectk.recipe.ProjectKRecipes;
import net.karasuniki.karasunikilib.api.data.impl.HeldItem;
import com.karasu256.projectk.block.entity.impl.AbstractAbyssEnergyMachineBlockEntity;
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

public class AbyssAlchemyBlendMachineBlockEntity extends AbstractAbyssEnergyMachineBlockEntity implements MenuProvider {
    private static final int CRAFT_TIME = 120;
    private final HeldItem inputItem = new HeldItem();
    private ItemStack outputItem = ItemStack.EMPTY;

    private int progress = 0;
    @Nullable
    private ResourceLocation lockedRecipeId;

    public AbyssAlchemyBlendMachineBlockEntity(BlockPos pos, BlockState state) {
        super(ProjectKBlockEntities.ABYSS_ALCHEMY_BLEND_MACHINE.get(), pos, state, resolveCapacity(state));
    }

    public static void tick(Level level, BlockPos pos, BlockState state, AbyssAlchemyBlendMachineBlockEntity be) {
        if (level.isClientSide) return;
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
            setChanged();
            sync();
        }
        if (progress >= CRAFT_TIME) {
            craft(activeRecipe);
            resetProgress();
        }
    }

    private boolean hasEnoughEnergy(AbyssAlchemyBlendRecipe recipe) {
        long first = getEnergyAmount(recipe.energyId1());
        long second = getEnergyAmount(recipe.energyId2());
        return first >= recipe.energyAmount1() && second >= recipe.energyAmount2();
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
        return inputItem.getHeldItem();
    }

    public void setInputItem(ItemStack stack) {
        inputItem.setHeldItem(stack);
        setChanged();
        sync();
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
            case 1 -> CRAFT_TIME;
            case 2 -> (int) getEnergyAmount1();
            case 3 -> (int) (getEnergyAmount1() >>> 32);
            case 4 -> (int) getEnergyCapacity1();
            case 5 -> (int) (getEnergyCapacity1() >>> 32);
            case 6 -> (int) getEnergyAmount2();
            case 7 -> (int) (getEnergyAmount2() >>> 32);
            case 8 -> (int) getEnergyCapacity2();
            case 9 -> (int) (getEnergyCapacity2() >>> 32);
            case 10 -> getEnergyId1() == null ? 0 : ProjectKEnergies.getModelIndex(getEnergyId1());
            case 11 -> getEnergyId2() == null ? 0 : ProjectKEnergies.getModelIndex(getEnergyId2());
            default -> 0;
        };
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
        return CRAFT_TIME;
    }

    public long getEnergyCapacity1() {
        return capacity;
    }

    public long getEnergyCapacity2() {
        return capacity;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.projectk.abyss_alchemy_blend_machine");
    }

    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory inventory, Player player) {
        return new AbyssAlchemyBlendMachineMenu(syncId, inventory, this);
    }

    @Override
    protected int maxEnergyTypes() {
        return 2;
    }

    @Override
    protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        super.saveAdditional(nbt, registries);
        nbt.putInt(EnergyKeys.MAGIC_TABLE_PROGRESS.toString(), progress);
        if (lockedRecipeId != null) {
            nbt.putString(EnergyKeys.MAGIC_TABLE_LOCKED_RECIPE.toString(), lockedRecipeId.toString());
        }
        if (!outputItem.isEmpty()) {
            nbt.put(EnergyKeys.MAGIC_TABLE_OUTPUT_ITEM.toString(), outputItem.save(registries));
        }
        writeEnergyNbt(nbt, registries);
        inputItem.writeNbt(nbt, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        super.loadAdditional(nbt, registries);
        progress = nbt.getInt(EnergyKeys.MAGIC_TABLE_PROGRESS.toString());
        if (nbt.contains(EnergyKeys.MAGIC_TABLE_LOCKED_RECIPE.toString())) {
            lockedRecipeId = ResourceLocation.parse(nbt.getString(EnergyKeys.MAGIC_TABLE_LOCKED_RECIPE.toString()));
        } else {
            lockedRecipeId = null;
        }
        if (nbt.contains(EnergyKeys.MAGIC_TABLE_OUTPUT_ITEM.toString())) {
            outputItem = ItemStack.parse(registries, nbt.getCompound(EnergyKeys.MAGIC_TABLE_OUTPUT_ITEM.toString())).orElse(ItemStack.EMPTY);
        } else {
            outputItem = ItemStack.EMPTY;
        }
        readEnergyNbt(nbt, registries);
        inputItem.readNbt(nbt, registries);
        capacity = resolveCapacity(getBlockState());
    }

    @Nullable
    @SuppressWarnings("unchecked")
    private RecipeHolder<AbyssAlchemyBlendRecipe> getLockedRecipe() {
        if (lockedRecipeId == null || level == null) {
            return null;
        }
        return level.getRecipeManager()
                .byKey(lockedRecipeId)
                .filter(holder -> holder.value() instanceof AbyssAlchemyBlendRecipe)
                .map(holder -> (RecipeHolder<AbyssAlchemyBlendRecipe>) holder)
                .orElse(null);
    }

    @Nullable
    private RecipeHolder<AbyssAlchemyBlendRecipe> findMatchingRecipe(ItemStack input) {
        if (level == null) return null;
        List<RecipeHolder<AbyssAlchemyBlendRecipe>> recipes = level.getRecipeManager()
                .getAllRecipesFor(ProjectKRecipes.ABYSS_ALCHEMY_BLEND.get());
        for (RecipeHolder<AbyssAlchemyBlendRecipe> holder : recipes) {
            AbyssAlchemyBlendRecipe recipe = holder.value();
            if (!recipe.matchesInput(input)) continue;
            return holder;
        }
        return null;
    }
}
