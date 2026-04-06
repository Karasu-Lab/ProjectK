package com.karasu256.projectk.block.entity;

import com.karasu256.projectk.block.custom.AbyssEnchantRemover;
import com.karasu256.projectk.data.AbyssEnergyData;
import com.karasu256.projectk.data.EnergyCapacityData;
import com.karasu256.projectk.data.ProjectKDataComponets;
import com.karasu256.projectk.energy.EnergyKeys;
import com.karasu256.projectk.menu.AbyssEnchantRemoverMenu;
import com.karasu256.projectk.registry.ProjectKTags;
import com.karasu256.projectk.utils.Id;
import net.karasuniki.karasunikilib.api.block.entity.impl.KarasuCoreBlockEntity;
import net.karasuniki.karasunikilib.api.data.impl.HeldItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public class AbyssEnchantRemoverBlockEntity extends KarasuCoreBlockEntity implements MenuProvider {
    private static final String ENERGY_LIST_KEY = EnergyKeys.ENERGY_LIST.toString();

    private final HeldItem inputItem = new HeldItem(Id.id("enchant_remover_input"));
    private final HeldItem bookItem = new HeldItem(Id.id("enchant_remover_book"));
    private ItemStack outputItem = ItemStack.EMPTY;
    private boolean suppressRefresh = false;
    private boolean refreshInProgress = false;

    private long defaultBookCapacity;

    public AbyssEnchantRemoverBlockEntity(BlockPos pos, BlockState state) {
        super(ProjectKBlockEntities.ABYSS_ENCHANT_REMOVER.get(), pos, state);
        this.defaultBookCapacity = resolveDefaultBookCapacity(state);
    }

    private static long resolveDefaultBookCapacity(BlockState state) {
        if (state.getBlock() instanceof AbyssEnchantRemover remover) {
            return remover.getDefaultBookCapacity();
        }
        return 30000L;
    }

    public ItemStack getInputItem() {
        return inputItem.getHeldItem();
    }

    public void setInputItem(ItemStack stack) {
        inputItem.setHeldItem(stack);
        refreshOutput();
    }

    public ItemStack getBookItem() {
        return bookItem.getHeldItem();
    }

    public void setBookItem(ItemStack stack) {
        bookItem.setHeldItem(stack);
        refreshOutput();
    }

    public ItemStack getOutputItem() {
        return outputItem;
    }

    public void setOutputItem(ItemStack stack) {
        if (ItemStack.isSameItemSameComponents(outputItem, stack) && outputItem.getCount() == stack.getCount()) {
            return;
        }
        outputItem = stack;
        setChanged();
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
        List<AbyssEnergyData> inputList = readEnergyList(input);
        if (inputList.isEmpty()) {
            return;
        }
        List<AbyssEnergyData> bookList = readEnergyList(book);
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

        writeEnergyList(book, bookList);
        writeEnergyList(input, inputList);
    }

    private long getBookCapacity(ItemStack book) {
        EnergyCapacityData cap = book.get(ProjectKDataComponets.ENERGY_CAPACITY_DATA_COMPONENT_TYPE.get());
        return cap == null ? defaultBookCapacity : cap.capacity();
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

    private List<AbyssEnergyData> readEnergyList(ItemStack stack) {
        List<AbyssEnergyData> list = new ArrayList<>();
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (tag.contains(ENERGY_LIST_KEY, Tag.TAG_LIST)) {
            ListTag listTag = tag.getList(ENERGY_LIST_KEY, Tag.TAG_COMPOUND);
            for (int i = 0; i < listTag.size(); i++) {
                AbyssEnergyData.CODEC.parse(NbtOps.INSTANCE, listTag.getCompound(i)).result().ifPresent(list::add);
            }
        }
        if (list.isEmpty()) {
            AbyssEnergyData data = stack.get(ProjectKDataComponets.ABYSS_ENERGY_DATA_COMPONENT_TYPE.get());
            if (data != null && data.energyId() != null && data.hasPositiveAmount()) {
                list.add(data);
            }
        }
        return list;
    }

    private void writeEnergyList(ItemStack stack, List<AbyssEnergyData> list) {
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (list.isEmpty()) {
            stack.remove(ProjectKDataComponets.ABYSS_ENERGY_DATA_COMPONENT_TYPE.get());
            tag.remove(ENERGY_LIST_KEY);
            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
            return;
        }
        if (list.size() == 1) {
            AbyssEnergyData data = list.get(0);
            AbyssEnergyData.applyToStack(stack, data.energyId(), data.amount());
            tag.remove(ENERGY_LIST_KEY);
            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
            return;
        }
        stack.remove(ProjectKDataComponets.ABYSS_ENERGY_DATA_COMPONENT_TYPE.get());
        ListTag listTag = new ListTag();
        for (AbyssEnergyData data : list) {
            AbyssEnergyData.CODEC.encodeStart(NbtOps.INSTANCE, data).result()
                    .ifPresent(element -> listTag.add(element));
        }
        tag.put(ENERGY_LIST_KEY, listTag);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
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
        inputItem.writeNbt(nbt, registries);
        bookItem.writeNbt(nbt, registries);
        if (!outputItem.isEmpty()) {
            nbt.put("output_item", outputItem.save(registries));
        }
    }

    @Override
    protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        super.loadAdditional(nbt, registries);
        if (nbt.contains("default_book_capacity")) {
            defaultBookCapacity = nbt.getLong("default_book_capacity");
        }
        inputItem.readNbt(nbt, registries);
        bookItem.readNbt(nbt, registries);
        if (nbt.contains("output_item")) {
            outputItem = ItemStack.parse(registries, nbt.getCompound("output_item")).orElse(ItemStack.EMPTY);
        } else {
            outputItem = ItemStack.EMPTY;
        }
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
}
