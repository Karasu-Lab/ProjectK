package com.karasu256.projectk.block.entity;

import com.karasu256.projectk.block.custom.AbyssSynthesizer;
import com.karasu256.projectk.block.entity.impl.AbstractAbyssMachineBlockEntity;
import com.karasu256.projectk.data.AbyssEnergyData;
import com.karasu256.projectk.energy.EnergyKeys;
import com.karasu256.projectk.menu.AbyssSynthesizerMenu;
import com.karasu256.projectk.recipe.AbyssSynthesizerRecipe;
import com.karasu256.projectk.recipe.ProjectKRecipes;
import com.karasu256.projectk.utils.Id;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class AbyssSynthesizerBlockEntity extends AbstractAbyssMachineBlockEntity implements MenuProvider, Container {
    private static final int MAX_TYPES = 64;
    private static final int MAX_PROGRESS = 100;
    private int progress = 0;
    private final ContainerData data;

    public AbyssSynthesizerBlockEntity(BlockPos pos, BlockState state) {
        super(ProjectKBlockEntities.ABYSS_SYNTHESIZER.get(), pos, state, resolveCapacity(state));
        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> progress;
                    case 1 -> getMaxProgress();
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                if (index == 0)
                    progress = value;
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
        for (int i = 0; i < 7; i++) {
            addItemSlot(Id.id("slot_" + i));
        }
    }

    @SuppressWarnings("unused")
    public static void tick(Level level, BlockPos pos, BlockState state, AbyssSynthesizerBlockEntity be) {
        if (level.isClientSide)
            return;
        be.serverTick();
    }

    private static long resolveCapacity(BlockState state) {
        if (state.getBlock() instanceof AbyssSynthesizer synthesizer) {
            return synthesizer.getCapacity();
        }
        return 10000L;
    }

    private void serverTick() {
        var recipe = findMatchingRecipe();
        if (recipe != null && canCraft(recipe.value())) {
            progress++;
            if (progress >= getMaxProgress()) {
                craft(recipe.value());
                progress = 0;
            }
            markDirtyAndSync();
        } else {
            if (progress > 0) {
                progress = 0;
                markDirtyAndSync();
            }
        }
    }

    public int getMaxProgress() {
        return getCraftTimeForTier(MAX_PROGRESS);
    }

    private RecipeHolder<AbyssSynthesizerRecipe> findMatchingRecipe() {
        if (level == null)
            return null;
        var recipes = level.getRecipeManager().getAllRecipesFor(ProjectKRecipes.ABYSS_SYNTHESIZER.get());
        for (var holder : recipes) {
            if (holder.value().matchesContainer(this) && holder.value().checkEnergies(energies)) {
                return holder;
            }
        }
        return null;
    }

    private boolean canCraft(AbyssSynthesizerRecipe recipe) {
        ItemStack result = recipe.result();
        ItemStack currentOutput = getItem(0);
        if (currentOutput.isEmpty())
            return true;
        if (!ItemStack.isSameItemSameComponents(currentOutput, result))
            return false;
        if (currentOutput.getCount() + result.getCount() > result.getMaxStackSize())
            return false;
        long totalEnergy = getEnergyList().stream().mapToLong(AbyssEnergyData::amountOrZero).sum();
        return totalEnergy >= 6000L;
    }

    private void craft(AbyssSynthesizerRecipe recipe) {
        for (var req : recipe.inputs()) {
            int toConsume = req.count();
            for (int i = 1; i <= 6; i++) {
                ItemStack stack = getItem(i);
                if (!stack.isEmpty() && req.test(stack)) {
                    int take = Math.min(stack.getCount(), toConsume);
                    stack.shrink(take);
                    toConsume -= take;
                    if (toConsume <= 0) {
                        break;
                    }
                }
            }
        }
        for (var req : recipe.energies()) {
            extract(req.energyId(), req.amountOrZero(), false);
        }
        if (recipe.minMachineDistinctEnergies() > 0) {
            long toExtract = recipe.minMachineDistinctEnergies() * recipe.minMachineAmountPerEnergy();
            for (var entry : new ArrayList<>(energies)) {
                long take = Math.min(entry.amountOrZero(), toExtract);
                if (take > 0) {
                    extract(entry.energyId(), take, false);
                    toExtract -= take;
                    if (toExtract <= 0)
                        break;
                }
            }
        }
        if (recipe.minMachineTotalEnergy() > 0) {
            long remainingToExtract = recipe.minMachineTotalEnergy();
            for (var entry : new ArrayList<>(energies)) {
                long available = entry.amountOrZero();
                long take = Math.min(available, remainingToExtract);
                if (take > 0) {
                    extract(entry.energyId(), take, false);
                    remainingToExtract -= take;
                    if (remainingToExtract <= 0) {
                        break;
                    }
                }
            }
        }
        ItemStack result = recipe.result().copy();
        if (getItem(0).isEmpty()) {
            setItem(0, result);
        } else {
            getItem(0).grow(result.getCount());
        }
        markDirtyAndSync();
    }

    @Override
    public int getMaxEnergyTypes() {
        return MAX_TYPES;
    }

    public void dumpEnergy() {
        if (!energies.isEmpty()) {
            energies.clear();
            markDirtyAndSync();
        }
    }

    public int getProgress() {
        return progress;
    }

    @Override
    public int getContainerSize() {
        return heldItems.size();
    }

    @Override
    public boolean isEmpty() {
        return heldItems.stream().allMatch(h -> h.getHeldItem().isEmpty());
    }

    @Override
    public ItemStack getItem(int slot) {
        return heldItems.get(slot).getHeldItem();
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack result = heldItems.get(slot).getHeldItem().split(amount);
        if (!result.isEmpty())
            markDirtyAndSync();
        return result;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        ItemStack stack = heldItems.get(slot).getHeldItem();
        heldItems.get(slot).setHeldItem(ItemStack.EMPTY);
        return stack;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        heldItems.get(slot).setHeldItem(stack);
        if (stack.getCount() > getMaxStackSize()) {
            stack.setCount(getMaxStackSize());
        }
        markDirtyAndSync();
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public void clearContent() {
        for (var heldItem : heldItems) {
            heldItem.setHeldItem(ItemStack.EMPTY);
        }
        markDirtyAndSync();
    }

    @Override
    protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        super.saveAdditional(nbt, registries);
        saveNbt(nbt, EnergyKeys.PROGRESS, progress, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        super.loadAdditional(nbt, registries);
        Integer p = loadNbt(nbt, EnergyKeys.PROGRESS, Integer.class, registries);
        progress = p != null ? p : 0;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.projectk.abyss_synthesizer");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory inventory, Player player) {
        return new AbyssSynthesizerMenu(syncId, inventory, this, this.data);
    }
}
