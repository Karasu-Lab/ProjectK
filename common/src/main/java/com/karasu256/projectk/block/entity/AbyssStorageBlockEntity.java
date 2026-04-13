package com.karasu256.projectk.block.entity;

import com.karasu256.projectk.block.custom.AbyssStorage;
import com.karasu256.projectk.block.entity.impl.AbstractAbyssMachineBlockEntity;
import com.karasu256.projectk.data.AbyssEnergyData;
import com.karasu256.projectk.data.EnergyCapacityData;
import com.karasu256.projectk.data.ProjectKDataComponets;
import com.karasu256.projectk.energy.IEnergyItemInput;
import com.karasu256.projectk.energy.IEnergyItemOutput;
import com.karasu256.projectk.energy.IMultiEnergyStorage;
import com.karasu256.projectk.energy.ProjectKEnergies;
import com.karasu256.projectk.menu.AbyssStorageMenu;
import com.karasu256.projectk.utils.Id;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class AbyssStorageBlockEntity extends AbstractAbyssMachineBlockEntity implements MenuProvider, IEnergyItemInput, IEnergyItemOutput {
    private static final int SLOT_COUNT = 3;
    private static Predicate<ToggleContext> TOGGLE_CONDITION = context -> true;
    protected int activeIndex = -1;

    public AbyssStorageBlockEntity(BlockPos pos, BlockState state) {
        this(ProjectKBlockEntities.ABYSS_STORAGE.get(), pos, state);
    }

    protected AbyssStorageBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        this(type, pos, state, resolveCapacity(state));
    }

    protected AbyssStorageBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, long capacity) {
        super(type, pos, state, capacity, resolveMaxTypes(state));
        addItemSlot(Id.id("storage_input"));
        addItemSlot(Id.id("storage_charge"));
    }

    public static void tick(Level level, BlockPos pos, BlockState state, AbyssStorageBlockEntity be) {
        if (level.isClientSide) {
            return;
        }
        be.serverTick();
    }

    private static long resolveCapacity(BlockState state) {
        if (state.getBlock() instanceof AbyssStorage storage) {
            return storage.getCapacity();
        }
        return 0L;
    }

    private static int resolveMaxTypes(BlockState state) {
        if (state.getBlock() instanceof AbyssStorage storage) {
            return storage.getMaxTypes();
        }
        return 0;
    }

    public static void setToggleCondition(Predicate<ToggleContext> condition) {
        TOGGLE_CONDITION = condition == null ? context -> true : condition;
    }

    public static void setToggleCondition(Consumer<ToggleContext> condition) {
        if (condition == null) {
            TOGGLE_CONDITION = context -> true;
            return;
        }
        TOGGLE_CONDITION = context -> {
            condition.accept(context);
            return true;
        };
    }

    protected void serverTick() {
        setInputItem(chargeFromItem(getInputItem()));
        setChargeItem(chargeItem(getChargeItem()));
    }

    @Override
    public long insert(ResourceLocation id, long maxAmount, boolean simulate) {
        long inserted = super.insert(id, maxAmount, simulate);
        if (inserted > 0 && !simulate && activeIndex < 0) {
            activeIndex = findEnergyIndex(id);
        }
        return inserted;
    }

    @Override
    public long extract(ResourceLocation id, long maxAmount, boolean simulate) {
        if (!isActiveEnergy(id)) {
            return 0;
        }
        AbyssEnergyData active = getActiveEnergy();
        if (active == null) {
            return 0;
        }
        long extracted = Math.min(active.amountOrZero(), maxAmount);
        if (extracted <= 0) {
            return 0;
        }
        if (!simulate) {
            long remaining = active.amountOrZero() - extracted;
            if (remaining <= 0) {
                removeEnergyAt(activeIndex);
            } else {
                energies.set(activeIndex, new AbyssEnergyData(active.energyId(), remaining));
            }
            markDirtyAndSync();
        }
        return extracted;
    }

    @Override
    public long extract(ResourceLocation id, long maxAmount, boolean simulate, @Nullable Direction side) {
        return extract(id, maxAmount, simulate);
    }

    @Override
    protected boolean canOutputEnergy() {
        return true;
    }

    public ItemStack getInputItem() {
        return heldItems.getFirst().getHeldItem();
    }

    public void setInputItem(ItemStack stack) {
        heldItems.getFirst().setHeldItem(stack);
        markDirtyAndSync();
    }

    public ItemStack getChargeItem() {
        return heldItems.get(1).getHeldItem();
    }

    public void setChargeItem(ItemStack stack) {
        heldItems.get(1).setHeldItem(stack);
        markDirtyAndSync();
    }

    @Override
    protected void refreshMaxEnergy() {
        super.refreshMaxEnergy();
        clampEnergyAmounts();
    }

    @Override
    public int getActiveEnergyIndex() {
        return activeIndex;
    }

    public void setActiveEnergyIndex(int index) {
        this.activeIndex = index;
        markDirtyAndSync();
    }

    public ResourceLocation getEnergyId1() {
        AbyssEnergyData data = getEnergyByIndex(0);
        return data == null || !data.hasPositiveAmount() ? null : data.energyId();
    }

    public ResourceLocation getEnergyId2() {
        AbyssEnergyData data = getEnergyByIndex(1);
        return data == null || !data.hasPositiveAmount() ? null : data.energyId();
    }

    public ResourceLocation getEnergyId3() {
        AbyssEnergyData data = getEnergyByIndex(2);
        return data == null || !data.hasPositiveAmount() ? null : data.energyId();
    }

    public long getEnergyAmount1() {
        AbyssEnergyData data = getEnergyByIndex(0);
        return data == null ? 0L : data.amountOrZero();
    }

    public long getEnergyAmount2() {
        AbyssEnergyData data = getEnergyByIndex(1);
        return data == null ? 0L : data.amountOrZero();
    }

    public long getEnergyAmount3() {
        AbyssEnergyData data = getEnergyByIndex(2);
        return data == null ? 0L : data.amountOrZero();
    }

    public int getActiveIndex() {
        return activeIndex;
    }

    public boolean toggleActive(int index, Player player) {
        if (index < 0 || index >= SLOT_COUNT) {
            return false;
        }
        AbyssEnergyData data = getEnergyByIndex(index);
        if (data == null || !data.hasPositiveAmount()) {
            return false;
        }
        if (!TOGGLE_CONDITION.test(new ToggleContext(this, player, index))) {
            return false;
        }
        if (activeIndex == index) {
            activeIndex = -1;
        } else {
            activeIndex = index;
        }
        markDirtyAndSync();
        return true;
    }

    public boolean canAcceptEnergyItem(ItemStack stack) {
        List<AbyssEnergyData> entries = readEnergyList(stack);
        if (entries.isEmpty()) {
            return false;
        }
        for (AbyssEnergyData data : entries) {
            if (data == null || data.energyId() == null || !data.hasPositiveAmount()) {
                continue;
            }
            if (findEnergyIndex(data.energyId()) >= 0) {
                return true;
            }
            if (getEnergyTypeCount() < maxEnergyTypes) {
                return true;
            }
        }
        return false;
    }

    public ItemStack chargeFromItem(ItemStack input) {
        if (input.isEmpty()) {
            return input;
        }
        List<AbyssEnergyData> entries = readEnergyList(input);
        if (entries.isEmpty()) {
            return input;
        }
        boolean changed = false;
        for (int i = 0; i < entries.size(); ) {
            AbyssEnergyData data = entries.get(i);
            if (data == null || data.energyId() == null || !data.hasPositiveAmount()) {
                entries.remove(i);
                changed = true;
                continue;
            }
            long inserted = insert(data.energyId(), data.amountOrZero(), false);
            if (inserted > 0) {
                long remaining = data.amountOrZero() - inserted;
                if (remaining <= 0) {
                    entries.remove(i);
                    changed = true;
                    continue;
                }
                entries.set(i, new AbyssEnergyData(data.energyId(), remaining));
                changed = true;
            }
            i++;
        }
        if (changed) {
            writeEnergyList(input, entries);
        }
        return input;
    }

    public boolean canChargeItem(ItemStack stack) {
        return !stack.isEmpty();
    }

    public ItemStack chargeItem(ItemStack stack) {
        if (stack.isEmpty()) {
            return stack;
        }
        EnergyCapacityData capacityData = stack.get(ProjectKDataComponets.ENERGY_CAPACITY_DATA_COMPONENT_TYPE.get());
        if (capacityData == null || capacityData.isInfinite() || capacityData.get() <= 0) {
            if (getMaxEnergy() <= 0) {
                return stack;
            }
            capacityData = EnergyCapacityData.of(getMaxEnergy());
            stack.set(ProjectKDataComponets.ENERGY_CAPACITY_DATA_COMPONENT_TYPE.get(), capacityData);
        }
        ResourceLocation activeId = getActiveEnergyId();
        if (activeId == null) {
            return stack;
        }
        long activeAmount = getActiveEnergyAmount();
        if (activeAmount <= 0) {
            return stack;
        }
        List<AbyssEnergyData> energies = readEnergyList(stack);
        int index = findStackEnergyIndex(energies, activeId);
        long current = index >= 0 ? energies.get(index).amountOrZero() : 0L;
        long cap = capacityData.get();
        if (current >= cap) {
            return stack;
        }
        long maxMove = Math.min(activeAmount, cap - current);
        long extracted = extract(activeId, maxMove, false);
        if (extracted <= 0) {
            return stack;
        }
        long nextAmount = current + extracted;
        if (index >= 0) {
            energies.set(index, new AbyssEnergyData(activeId, nextAmount));
        } else {
            energies.add(new AbyssEnergyData(activeId, nextAmount));
        }
        writeEnergyList(stack, energies);
        return stack;
    }

    private List<AbyssEnergyData> readEnergyList(ItemStack stack) {
        List<AbyssEnergyData> list = new ArrayList<>();
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (tag.contains(IMultiEnergyStorage.ENERGY_LIST_KEY, Tag.TAG_LIST)) {
            ListTag listTag = tag.getList(IMultiEnergyStorage.ENERGY_LIST_KEY, Tag.TAG_COMPOUND);
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
            tag.remove(IMultiEnergyStorage.ENERGY_LIST_KEY);
            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
            return;
        }
        if (list.size() == 1) {
            AbyssEnergyData data = list.getFirst();
            AbyssEnergyData.applyToStack(stack, data.energyId(), data.amount());
            tag.remove(IMultiEnergyStorage.ENERGY_LIST_KEY);
            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
            return;
        }
        stack.remove(ProjectKDataComponets.ABYSS_ENERGY_DATA_COMPONENT_TYPE.get());
        ListTag listTag = new ListTag();
        for (AbyssEnergyData data : list) {
            AbyssEnergyData.CODEC.encodeStart(NbtOps.INSTANCE, data).result().ifPresent(listTag::add);
        }
        tag.put(IMultiEnergyStorage.ENERGY_LIST_KEY, listTag);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    private int findStackEnergyIndex(List<AbyssEnergyData> list, ResourceLocation id) {
        if (id == null) {
            return -1;
        }
        for (int i = 0; i < list.size(); i++) {
            AbyssEnergyData data = list.get(i);
            if (data != null && id.equals(data.energyId())) {
                return i;
            }
        }
        return -1;
    }

    private boolean isActiveEnergy(@Nullable ResourceLocation id) {
        if (id == null) {
            return false;
        }
        AbyssEnergyData data = getActiveEnergy();
        return data != null && id.equals(data.energyId());
    }

    @Nullable
    private ResourceLocation getActiveEnergyId() {
        AbyssEnergyData data = getActiveEnergy();
        return data == null ? null : data.energyId();
    }

    private long getActiveEnergyAmount() {
        AbyssEnergyData data = getActiveEnergy();
        return data == null ? 0L : data.amountOrZero();
    }

    @Nullable
    private AbyssEnergyData getActiveEnergy() {
        if (activeIndex < 0 || activeIndex >= energies.size()) {
            return null;
        }
        return energies.get(activeIndex);
    }

    private void removeEnergyAt(int index) {
        if (index < 0 || index >= energies.size()) {
            return;
        }
        energies.remove(index);
        if (activeIndex == index) {
            activeIndex = -1;
        } else if (activeIndex > index) {
            activeIndex--;
        }
    }

    private void clampEnergyAmounts() {
        if (energies == null)
            return;
        for (int i = 0; i < energies.size(); i++) {
            AbyssEnergyData data = energies.get(i);
            if (data == null || data.energyId() == null) {
                energies.remove(i--);
                continue;
            }
            long nextAmount = Math.min(data.amountOrZero(), getMaxEnergy());
            if (nextAmount <= 0) {
                energies.remove(i--);
                continue;
            }
            energies.set(i, new AbyssEnergyData(data.energyId(), nextAmount));
        }
    }

    public int getDataValue(int index) {
        return switch (index) {
            case 0 -> activeIndex;
            case 1 -> maxEnergyTypes;
            case 2 -> (int) getEnergyAmount1();
            case 3 -> (int) (getEnergyAmount1() >>> 32);
            case 4 -> (int) getEnergyAmount2();
            case 5 -> (int) (getEnergyAmount2() >>> 32);
            case 6 -> (int) getEnergyAmount3();
            case 7 -> (int) (getEnergyAmount3() >>> 32);
            case 8 -> ProjectKEnergies.getModelIndex(getEnergyId1());
            case 9 -> ProjectKEnergies.getModelIndex(getEnergyId2());
            case 10 -> ProjectKEnergies.getModelIndex(getEnergyId3());
            case 11 -> {
                Long cap = getEnergyCapacity();
                yield (int) (cap == null ? -1L : cap);
            }
            case 12 -> {
                Long cap = getEnergyCapacity();
                yield (int) ((cap == null ? -1L : cap) >>> 32);
            }
            default -> 0;
        };
    }

    public void setDataValue(int index, int value) {
        if (index == 0) {
            this.activeIndex = value;
            markDirtyAndSync();
        }
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.projectk.abyss_storage");
    }

    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory inventory, Player player) {
        return new AbyssStorageMenu(syncId, inventory, this);
    }

    @Override
    protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        super.saveAdditional(nbt, registries);
        nbt.putInt("active_index", activeIndex);
    }

    @Override
    protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        super.loadAdditional(nbt, registries);
        activeIndex = nbt.getInt("active_index");
        if (activeIndex >= energies.size()) {
            activeIndex = -1;
        }
    }

    public record ToggleContext(AbyssStorageBlockEntity blockEntity, Player player, int index) {
    }
}
