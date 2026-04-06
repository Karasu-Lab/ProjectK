package com.karasu256.projectk.menu;

import com.karasu256.projectk.block.ProjectKBlocks;
import com.karasu256.projectk.block.entity.AbyssStorageBlockEntity;
import com.karasu256.projectk.data.AbyssEnergyData;
import com.karasu256.projectk.data.ProjectKDataComponets;
import com.karasu256.projectk.energy.EnergyKeys;
import com.karasu256.projectk.energy.ProjectKEnergies;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

import java.util.ArrayList;
import java.util.List;

public class AbyssStorageMenu extends AbstractContainerMenu {
    private static final int SLOT_INPUT = 0;
    private static final int SLOT_CHARGE = 1;
    private static final int CONTAINER_SIZE = 2;
    private static final int DATA_SIZE = 13;

    private final Container container;
    private final ContainerData data;
    private final ContainerLevelAccess access;
    private final AbyssStorageBlockEntity blockEntity;

    public AbyssStorageMenu(int syncId, Inventory inventory) {
        this(syncId, inventory, new SimpleContainer(CONTAINER_SIZE), new SimpleContainerData(DATA_SIZE),
                ContainerLevelAccess.NULL, null);
    }

    public AbyssStorageMenu(int syncId, Inventory inventory, AbyssStorageBlockEntity blockEntity) {
        this(syncId, inventory, new StorageContainer(blockEntity), new StorageData(blockEntity),
                ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos()), blockEntity);
    }

    private AbyssStorageMenu(int syncId, Inventory inventory, Container container, ContainerData data, ContainerLevelAccess access, AbyssStorageBlockEntity blockEntity) {
        super(ProjectKMenus.ABYSS_STORAGE.get(), syncId);
        this.container = container;
        this.data = data;
        this.access = access;
        this.blockEntity = blockEntity;

        addSlot(new EnergyInputSlot(container, SLOT_INPUT, 20, 47, blockEntity));
        addSlot(new EnergyChargeSlot(container, SLOT_CHARGE, 44, 47, blockEntity));

        addPlayerInventory(inventory);
        addPlayerHotbar(inventory);

        addDataSlots(data);
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(access, player, ProjectKBlocks.ABYSS_STORAGE.get());
    }

    public int getActiveIndex() {
        return data.get(0);
    }

    public int getMaxTypes() {
        return data.get(1);
    }

    public long getEnergyAmount(int index) {
        int base = 2 + index * 2;
        int low = data.get(base);
        int high = data.get(base + 1);
        return (long) high << 32 | (low & 0xffffffffL);
    }

    public ResourceLocation getEnergyId(int index) {
        return ProjectKEnergies.getEnergyIdByModelIndex(data.get(8 + index));
    }

    public long getCapacity() {
        int low = data.get(11);
        int high = data.get(12);
        return (long) high << 32 | (low & 0xffffffffL);
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (blockEntity == null) {
            return false;
        }
        return blockEntity.toggleActive(id, player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (slot == null || !slot.hasItem()) {
            return stack;
        }
        ItemStack slotStack = slot.getItem();
        stack = slotStack.copy();

        if (index < CONTAINER_SIZE) {
            if (!moveItemStackTo(slotStack, CONTAINER_SIZE, slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else {
            if (slots.get(SLOT_INPUT).mayPlace(slotStack)) {
                if (!moveItemStackTo(slotStack, SLOT_INPUT, SLOT_INPUT + 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!moveItemStackTo(slotStack, SLOT_CHARGE, SLOT_CHARGE + 1, false)) {
                return ItemStack.EMPTY;
            }
        }

        if (slotStack.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        return stack;
    }

    private boolean canAcceptEnergyItem(ItemStack stack) {
        List<AbyssEnergyData> entries = readEnergyList(stack);
        if (entries.isEmpty()) {
            return false;
        }
        int types = 0;
        for (int i = 0; i < 3; i++) {
            if (getEnergyId(i) != null) {
                types++;
            }
        }
        for (AbyssEnergyData data : entries) {
            if (data == null || data.energyId() == null || !data.hasPositiveAmount()) {
                continue;
            }
            for (int i = 0; i < 3; i++) {
                ResourceLocation existing = getEnergyId(i);
                if (existing != null && existing.equals(data.energyId())) {
                    return true;
                }
            }
            if (types < getMaxTypes()) {
                return true;
            }
        }
        return false;
    }

    private List<AbyssEnergyData> readEnergyList(ItemStack stack) {
        List<AbyssEnergyData> list = new ArrayList<>();
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        String key = EnergyKeys.ENERGY_LIST.toString();
        if (tag.contains(key, Tag.TAG_LIST)) {
            ListTag listTag = tag.getList(key, Tag.TAG_COMPOUND);
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

    private void addPlayerInventory(Inventory inventory) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(inventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory inventory) {
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(inventory, col, 8 + col * 18, 142));
        }
    }

    private static class StorageContainer extends SimpleContainer {
        private final AbyssStorageBlockEntity blockEntity;

        public StorageContainer(AbyssStorageBlockEntity blockEntity) {
            super(CONTAINER_SIZE);
            this.blockEntity = blockEntity;
        }

        @Override
        public ItemStack getItem(int slot) {
            return slot == SLOT_INPUT ? blockEntity.getInputItem() : blockEntity.getChargeItem();
        }

        @Override
        public void setItem(int slot, ItemStack stack) {
            if (slot == SLOT_INPUT) {
                blockEntity.setInputItem(stack);
            } else {
                blockEntity.setChargeItem(stack);
            }
        }

        @Override
        public ItemStack removeItem(int slot, int amount) {
            ItemStack current = getItem(slot);
            ItemStack result = current.split(amount);
            setItem(slot, current);
            return result;
        }

        @Override
        public boolean stillValid(Player player) {
            return true;
        }
    }

    private record StorageData(AbyssStorageBlockEntity blockEntity) implements ContainerData {
        @Override
        public int get(int index) {
            return blockEntity.getDataValue(index);
        }

        @Override
        public void set(int index, int value) {
            blockEntity.setDataValue(index, value);
        }

        @Override
        public int getCount() {
            return DATA_SIZE;
        }
    }
}
