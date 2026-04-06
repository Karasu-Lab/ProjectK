package com.karasu256.projectk.block.entity;

import com.karasu256.projectk.block.custom.AbyssSynthesizer;
import com.karasu256.projectk.compat.wthit.IWthitCustomEnergy;
import com.karasu256.projectk.data.AbyssEnergyData;
import com.karasu256.projectk.energy.IMultiEnergyStorage;
import com.karasu256.projectk.menu.AbyssSynthesizerMenu;
import com.karasu256.projectk.recipe.AbyssSynthesizerRecipe;
import com.karasu256.projectk.recipe.ProjectKRecipes;
import net.karasuniki.karasunikilib.api.block.ICableInputable;
import net.karasuniki.karasunikilib.api.block.entity.impl.KarasuCoreBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class AbyssSynthesizerBlockEntity extends KarasuCoreBlockEntity implements ICableInputable, MenuProvider, IMultiEnergyStorage, Container, IWthitCustomEnergy {
    private static final int MAX_TYPES = 64;
    private static final int MAX_PROGRESS = 100;
    private final NonNullList<ItemStack> items = NonNullList.withSize(7, ItemStack.EMPTY);
    private final List<AbyssEnergyData> energies = new ArrayList<>();
    private final long capacity;
    private int progress = 0;

    public AbyssSynthesizerBlockEntity(BlockPos pos, BlockState state) {
        super(ProjectKBlockEntities.ABYSS_SYNTHESIZER.get(), pos, state);
        this.capacity = resolveCapacity(state);
    }

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
            if (progress >= MAX_PROGRESS) {
                craft(recipe.value());
                progress = 0;
            }
            setChanged();
        } else {
            if (progress > 0) {
                progress = 0;
                setChanged();
            }
        }
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
        ItemStack currentOutput = items.get(0);
        if (currentOutput.isEmpty())
            return true;
        if (!ItemStack.isSameItemSameComponents(currentOutput, result))
            return false;
        return currentOutput.getCount() + result.getCount() <= result.getMaxStackSize();
    }

    private void craft(AbyssSynthesizerRecipe recipe) {
        for (var req : recipe.inputs()) {
            int toConsume = req.count();
            for (int i = 1; i <= 6; i++) {
                ItemStack stack = items.get(i);
                if (!stack.isEmpty() && req.ingredient().test(stack)) {
                    int take = Math.min(stack.getCount(), toConsume);
                    stack.shrink(take);
                    toConsume -= take;
                    if (toConsume <= 0)
                        break;
                }
            }
        }
        for (var req : recipe.energies()) {
            extractInternal(req.energyId(), req.amountOrZero(), false);
        }
        ItemStack result = recipe.result().copy();
        if (items.get(0).isEmpty()) {
            items.set(0, result);
        } else {
            items.get(0).grow(result.getCount());
        }
        setChanged();
        sync();
    }

    @Override
    public long insert(ResourceLocation id, long maxAmount, boolean simulate) {
        if (id == null || maxAmount <= 0)
            return 0;

        long totalCurrent = energies.stream().mapToLong(AbyssEnergyData::amountOrZero).sum();
        long received = Math.min(capacity - totalCurrent, maxAmount);

        if (received <= 0)
            return 0;

        int index = findEnergyIndex(id);
        if (index < 0 && getEnergyTypeCount() >= MAX_TYPES) {
            return 0;
        }

        if (!simulate) {
            long current = index >= 0 ? energies.get(index).amountOrZero() : 0L;
            long nextAmount = current + received;
            if (index >= 0) {
                energies.set(index, new AbyssEnergyData(id, nextAmount));
            } else {
                energies.add(new AbyssEnergyData(id, nextAmount));
            }
            setChanged();
            sync();
        }
        return received;
    }

    public long extractInternal(ResourceLocation id, long maxAmount, boolean simulate) {
        int index = findEnergyIndex(id);
        if (index < 0)
            return 0;

        AbyssEnergyData data = energies.get(index);
        long extracted = Math.min(data.amountOrZero(), maxAmount);

        if (extracted > 0 && !simulate) {
            long remaining = data.amountOrZero() - extracted;
            if (remaining <= 0) {
                energies.remove(index);
            } else {
                energies.set(index, new AbyssEnergyData(id, remaining));
            }
            setChanged();
            sync();
        }
        return extracted;
    }

    public void dumpEnergy() {
        if (!energies.isEmpty()) {
            energies.clear();
            setChanged();
            sync();
        }
    }

    @Override
    public List<AbyssEnergyData> getEnergyList() {
        return energies;
    }

    @Override
    public long getEnergyCapacity() {
        return capacity;
    }

    @Override
    public int getMaxEnergyTypes() {
        return MAX_TYPES;
    }

    @Override
    public int getContainerSize() {
        return items.size();
    }

    @Override
    public boolean isEmpty() {
        return items.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getItem(int slot) {
        return items.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack result = ContainerHelper.removeItem(items, slot, amount);
        if (!result.isEmpty())
            setChanged();
        return result;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(items, slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        items.set(slot, stack);
        if (stack.getCount() > getMaxStackSize()) {
            stack.setCount(getMaxStackSize());
        }
        setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public void clearContent() {
        items.clear();
        setChanged();
    }

    @Override
    protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        super.saveAdditional(nbt, registries);
        writeEnergyListNbt(nbt);
        nbt.putInt("Progress", progress);
        ContainerHelper.saveAllItems(nbt, items, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        super.loadAdditional(nbt, registries);
        readEnergyListNbt(nbt);
        progress = nbt.getInt("Progress");
        items.clear();
        ContainerHelper.loadAllItems(nbt, items, registries);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        saveAdditional(tag, registries);
        return tag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.projectk.abyss_synthesizer");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory inventory, Player player) {
        return new AbyssSynthesizerMenu(syncId, inventory, this);
    }

    @Override
    public boolean shouldShowDefaultEnergyTooltip() {
        return false;
    }
}
