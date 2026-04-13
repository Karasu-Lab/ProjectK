package com.karasu256.projectk.block.entity;

import com.karasu256.projectk.block.custom.AbyssEnchantRemover;
import com.karasu256.projectk.block.entity.impl.AbstractAbyssMachineBlockEntity;
import com.karasu256.projectk.data.AbyssEnergyData;
import com.karasu256.projectk.data.EnergyCapacityData;
import com.karasu256.projectk.data.ProjectKDataComponets;
import com.karasu256.projectk.menu.AbyssEnchantRemoverMenu;
import com.karasu256.projectk.registry.ProjectKMachineCapacities;
import com.karasu256.projectk.registry.ProjectKTags;
import com.karasu256.projectk.utils.Id;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class AbyssEnchantRemoverBlockEntity extends AbstractAbyssMachineBlockEntity implements MenuProvider {
    private boolean suppressRefresh = false;
    private boolean refreshInProgress = false;
    private long defaultBookCapacity;

    public AbyssEnchantRemoverBlockEntity(BlockPos pos, BlockState state) {
        super(ProjectKBlockEntities.ABYSS_ENCHANT_REMOVER.get(), pos, state);
        this.defaultBookCapacity = resolveDefaultBookCapacity(state);
        addItemSlot(Id.id("input"));
        addItemSlot(Id.id("book"));
        addItemSlot(Id.id("output"));
    }

    private static long resolveDefaultBookCapacity(BlockState state) {
        if (state.getBlock() instanceof AbyssEnchantRemover remover) {
            return remover.getDefaultBookCapacity();
        }
        return 30000L;
    }

    public ItemStack getInputItem() {
        return heldItems.get(0).getHeldItem();
    }

    public void setInputItem(ItemStack stack) {
        heldItems.get(0).setHeldItem(stack);
        refreshOutput();
        markDirtyAndSync();
    }

    public ItemStack getBookItem() {
        return heldItems.get(1).getHeldItem();
    }

    public void setBookItem(ItemStack stack) {
        heldItems.get(1).setHeldItem(stack);
        refreshOutput();
        markDirtyAndSync();
    }

    public ItemStack getOutputItem() {
        return heldItems.get(2).getHeldItem();
    }

    public void setOutputItem(ItemStack stack) {
        ItemStack current = getOutputItem();
        if (ItemStack.isSameItemSameComponents(current, stack) && current.getCount() == stack.getCount()) {
            return;
        }
        heldItems.get(2).setHeldItem(stack);
        markDirtyAndSync();
    }

    public boolean canAcceptInput(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        return hasAbyssEnchant(stack);
    }

    public boolean canAcceptBook(ItemStack stack) {
        return stack.is(ProjectKTags.Items.BOOKS);
    }

    public void onTakeOutput(Player player) {
        suppressRefresh = true;
        ItemStack input = getInputItem();
        if (input.isEmpty()) {
            suppressRefresh = false;
            return;
        }
        ItemStack processed = input.copy();
        processed.setCount(1);

        ItemStack book = getBookItem();
        if (!book.isEmpty() && canAcceptBook(book)) {
            transferEnergyToBook(processed, book);
            setBookItem(book);
        }
        removeAbyssEnchants(processed);

        ItemStack nextInput = input.copy();
        nextInput.shrink(1);
        if (nextInput.isEmpty()) {
            setInputItem(ItemStack.EMPTY);
        } else {
            setInputItem(nextInput);
        }

        setOutputItem(ItemStack.EMPTY);
        suppressRefresh = false;
        refreshOutput();
    }

    public void refreshOutput() {
        if (suppressRefresh || refreshInProgress) {
            return;
        }
        refreshInProgress = true;
        ItemStack input = getInputItem();
        if (input.isEmpty() || !hasAbyssEnchant(input)) {
            setOutputItem(ItemStack.EMPTY);
            refreshInProgress = false;
            return;
        }
        ItemStack preview = buildOutputPreview(input, getBookItem());
        setOutputItem(preview);
        refreshInProgress = false;
    }

    private ItemStack buildOutputPreview(ItemStack input, ItemStack book) {
        ItemStack preview = input.copy();
        preview.setCount(1);
        if (!book.isEmpty() && canAcceptBook(book)) {
            ItemStack bookCopy = book.copy();
            transferEnergyToBook(preview, bookCopy);
        }
        removeAbyssEnchants(preview);
        return preview;
    }

    private void removeAbyssEnchants(ItemStack stack) {
        var lookup = level == null ? null : level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        if (lookup == null) {
            return;
        }
        removeAbyssEnchantsFromComponent(stack, lookup, DataComponents.ENCHANTMENTS);
        removeAbyssEnchantsFromComponent(stack, lookup, DataComponents.STORED_ENCHANTMENTS);
    }

    private void removeAbyssEnchantsFromComponent(ItemStack stack, HolderLookup.RegistryLookup<Enchantment> lookup, net.minecraft.core.component.DataComponentType<ItemEnchantments> component) {
        ItemEnchantments base = stack.getOrDefault(component, ItemEnchantments.EMPTY);
        if (base.isEmpty()) {
            return;
        }
        ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(base);
        for (Holder<Enchantment> holder : base.keySet()) {
            if (holder.is(ProjectKTags.Enchantments.ABYSS_ENCHANT) && base.getLevel(holder) > 0) {
                mutable.set(holder, 0);
            }
        }
        ItemEnchantments result = mutable.toImmutable();
        if (result.isEmpty()) {
            stack.set(component, ItemEnchantments.EMPTY);
            if (component == DataComponents.ENCHANTMENTS) {
                stack.set(DataComponents.REPAIR_COST, 0);
            }
        } else {
            stack.set(component, result);
        }
    }

    private boolean hasAbyssEnchant(ItemStack stack) {
        var lookup = level == null ? null : level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        if (lookup == null) {
            return false;
        }
        ItemEnchantments enchants = stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        ItemEnchantments stored = stack.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY);
        return hasTaggedEnchant(enchants) || hasTaggedEnchant(stored);
    }

    private boolean hasTaggedEnchant(ItemEnchantments enchants) {
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

    private void transferEnergyToBook(ItemStack input, ItemStack book) {
        List<AbyssEnergyData> inputList = AbyssEnergyData.readEnergyList(input);
        if (inputList.isEmpty()) {
            return;
        }
        List<AbyssEnergyData> bookList = AbyssEnergyData.readEnergyList(book);
        long capacity = getBookCapacity(book);

        for (int i = 0; i < inputList.size(); ) {
            AbyssEnergyData data = inputList.get(i);
            if (data == null || data.energyId() == null || !data.hasPositiveAmount()) {
                inputList.remove(i);
                continue;
            }
            int bookIndex = findEnergyIndex(bookList, data.energyId());
            long current = bookIndex >= 0 ? bookList.get(bookIndex).amountOrZero() : 0L;
            long allowed = Math.max(0L, capacity - current);
            long moved = Math.min(allowed, data.amountOrZero());
            if (moved > 0) {
                long nextAmount = current + moved;
                if (bookIndex >= 0) {
                    bookList.set(bookIndex, new AbyssEnergyData(data.energyId(), nextAmount));
                } else {
                    bookList.add(new AbyssEnergyData(data.energyId(), nextAmount));
                }
                long remaining = data.amountOrZero() - moved;
                if (remaining <= 0) {
                    inputList.remove(i);
                    continue;
                }
                inputList.set(i, new AbyssEnergyData(data.energyId(), remaining));
            }
            i++;
        }

        AbyssEnergyData.writeEnergyList(book, bookList);
        AbyssEnergyData.writeEnergyList(input, inputList);
    }

    private long getBookCapacity(ItemStack book) {
        EnergyCapacityData cap = book.get(ProjectKDataComponets.ENERGY_CAPACITY_DATA_COMPONENT_TYPE.get());
        return (cap == null || cap.isInfinite()) ? defaultBookCapacity : cap.get();
    }

    private int findEnergyIndex(List<AbyssEnergyData> list, ResourceLocation id) {
        for (int i = 0; i < list.size(); i++) {
            AbyssEnergyData data = list.get(i);
            if (data != null && id.equals(data.energyId())) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.projectk.abyss_enchant_remover");
    }

    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory inventory, Player player) {
        return new AbyssEnchantRemoverMenu(syncId, inventory, this);
    }

    @Override
    protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        super.saveAdditional(nbt, registries);
        nbt.putLong("default_book_capacity", defaultBookCapacity);
    }

    @Override
    protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        super.loadAdditional(nbt, registries);
        if (nbt.contains("default_book_capacity")) {
            defaultBookCapacity = nbt.getLong("default_book_capacity");
        }
    }
}
