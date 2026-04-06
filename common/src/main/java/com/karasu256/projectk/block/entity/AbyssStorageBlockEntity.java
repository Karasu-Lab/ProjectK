package com.karasu256.projectk.block.entity;

import com.karasu256.projectk.block.custom.AbyssStorage;
import com.karasu256.projectk.data.AbyssEnergyData;
import com.karasu256.projectk.data.EnergyCapacityData;
import com.karasu256.projectk.data.ProjectKDataComponets;
import com.karasu256.projectk.energy.*;
import com.karasu256.projectk.menu.AbyssStorageMenu;
import com.karasu256.projectk.utils.Id;
import net.karasuniki.karasunikilib.api.block.ICableInputable;
import net.karasuniki.karasunikilib.api.block.ICableOutputable;
import net.karasuniki.karasunikilib.api.block.entity.impl.KarasuCoreBlockEntity;
import net.karasuniki.karasunikilib.api.data.ICapacity;
import net.karasuniki.karasunikilib.api.data.impl.HeldItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class AbyssStorageBlockEntity extends KarasuCoreBlockEntity implements ICableInputable, ICableOutputable, ICapacity, MenuProvider, IMultiEnergyStorage, IEnergyItemInput, IEnergyItemOutput, IMaxEnrgyInfo, ITierInfo {
    private static final int SLOT_COUNT = 3;
    private static final int MAX_TIER = 3;
    private static final int DEFAULT_TIER = 1;
    private static Predicate<ToggleContext> TOGGLE_CONDITION = context -> true;
    private final List<AbyssEnergyData> energies = new ArrayList<>();
    private final HeldItem inputItem = new HeldItem(Id.id("storage_input"));
    private final HeldItem chargeItem = new HeldItem(Id.id("storage_charge"));
    private final long baseMaxEnergy;
    private long capacity;
    private long maxEnergy;
    private int maxTypes;
    private int activeIndex = -1;
    private int tier;

    public AbyssStorageBlockEntity(BlockPos pos, BlockState state) {
        super(ProjectKBlockEntities.ABYSS_STORAGE.get(), pos, state);
        this.baseMaxEnergy = resolveCapacity(state);
        this.capacity = baseMaxEnergy;
        this.tier = DEFAULT_TIER;
        refreshMaxEnergy();
        this.maxTypes = resolveMaxTypes(state);
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

    private void serverTick() {
        setInputItem(chargeFromItem(getInputItem()));
        setChargeItem(chargeItem(getChargeItem()));
    }

    @Override
    public long insert(ResourceLocation id, long maxAmount, boolean simulate) {
        if (id == null || maxAmount <= 0) {
            return 0;
        }
        int index = findEnergyIndex(id);
        if (index < 0 && getEnergyTypeCount() >= maxTypes) {
            return 0;
        }
        long current = index >= 0 ? energies.get(index).amountOrZero() : 0L;
        long received = Math.min(capacity - current, maxAmount);
        if (received <= 0) {
            return 0;
        }
        if (!simulate) {
            long nextAmount = current + received;
            if (index >= 0) {
                energies.set(index, new AbyssEnergyData(id, nextAmount));
            } else {
                energies.add(new AbyssEnergyData(id, nextAmount));
                if (activeIndex < 0) {
                    activeIndex = energies.size() - 1;
                }
            }
            setChanged();
            sync();
        }
        return received;
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
            setChanged();
            sync();
        }
        return extracted;
    }

    @Override
    public long extract(ResourceLocation id, long maxAmount, boolean simulate, @Nullable Direction side) {
        return extract(id, maxAmount, simulate);
    }

    @Override
    public long getCapacity() {
        return capacity;
    }

    public int getMaxTypes() {
        return maxTypes;
    }

    public ItemStack getInputItem() {
        return inputItem.getHeldItem();
    }

    public void setInputItem(ItemStack stack) {
        inputItem.setHeldItem(stack);
        setChanged();
        sync();
    }

    public ItemStack getChargeItem() {
        return chargeItem.getHeldItem();
    }

    public void setChargeItem(ItemStack stack) {
        chargeItem.setHeldItem(stack);
        setChanged();
        sync();
    }

    private void refreshMaxEnergy() {
        setMaxEnergy(getTieredMaxEnergy(getTier()));
        capacity = getMaxEnergy();
        clampEnergyAmounts();
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
        return maxTypes;
    }

    @Override
    public int getActiveEnergyIndex() {
        return activeIndex;
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

    @Override
    public List<EnergyEntry> getEnergyEntries() {
        return IMultiEnergyStorage.super.getEnergyEntries();
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
        setChanged();
        sync();
        return true;
    }

    @Override
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
            if (getEnergyTypeCount() < maxTypes) {
                return true;
            }
        }
        return false;
    }

    @Override
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

    @Override
    public boolean canChargeItem(ItemStack stack) {
        return !stack.isEmpty();
    }

    @Override
    public ItemStack chargeItem(ItemStack stack) {
        if (stack.isEmpty()) {
            return stack;
        }
        EnergyCapacityData capacityData = stack.get(ProjectKDataComponets.ENERGY_CAPACITY_DATA_COMPONENT_TYPE.get());
        if (capacityData == null || capacityData.capacity() <= 0) {
            if (capacity <= 0) {
                return stack;
            }
            capacityData = new EnergyCapacityData(capacity);
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
        int index = findEnergyIndex(energies, activeId);
        long current = index >= 0 ? energies.get(index).amountOrZero() : 0L;
        long cap = capacityData.capacity();
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
            AbyssEnergyData data = list.get(0);
            AbyssEnergyData.applyToStack(stack, data.energyId(), data.amount());
            tag.remove(IMultiEnergyStorage.ENERGY_LIST_KEY);
            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
            return;
        }
        stack.remove(ProjectKDataComponets.ABYSS_ENERGY_DATA_COMPONENT_TYPE.get());
        ListTag listTag = new ListTag();
        for (AbyssEnergyData data : list) {
            AbyssEnergyData.CODEC.encodeStart(NbtOps.INSTANCE, data).result()
                    .ifPresent(element -> listTag.add(element));
        }
        tag.put(IMultiEnergyStorage.ENERGY_LIST_KEY, listTag);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    private int findEnergyIndex(List<AbyssEnergyData> list, ResourceLocation id) {
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
        for (int i = 0; i < energies.size(); i++) {
            AbyssEnergyData data = energies.get(i);
            if (data == null || data.energyId() == null) {
                energies.remove(i--);
                continue;
            }
            long nextAmount = Math.min(data.amountOrZero(), capacity);
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
            case 1 -> maxTypes;
            case 2 -> (int) getEnergyAmount1();
            case 3 -> (int) (getEnergyAmount1() >>> 32);
            case 4 -> (int) getEnergyAmount2();
            case 5 -> (int) (getEnergyAmount2() >>> 32);
            case 6 -> (int) getEnergyAmount3();
            case 7 -> (int) (getEnergyAmount3() >>> 32);
            case 8 -> ProjectKEnergies.getModelIndex(getEnergyId1());
            case 9 -> ProjectKEnergies.getModelIndex(getEnergyId2());
            case 10 -> ProjectKEnergies.getModelIndex(getEnergyId3());
            case 11 -> (int) capacity;
            case 12 -> (int) (capacity >>> 32);
            default -> 0;
        };
    }

    public void setDataValue(int index, int value) {
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
        saveTier(nbt);
        saveMaxEnergy(nbt);
        writeEnergyListNbt(nbt);
        nbt.putInt("active_index", activeIndex);
        nbt.putInt("max_types", maxTypes);
        inputItem.writeNbt(nbt, registries);
        chargeItem.writeNbt(nbt, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        super.loadAdditional(nbt, registries);
        loadTier(nbt);
        loadMaxEnergy(nbt);
        refreshMaxEnergy();
        readEnergyListNbt(nbt);
        activeIndex = nbt.getInt("active_index");
        if (nbt.contains("max_types")) {
            maxTypes = nbt.getInt("max_types");
        }
        if (activeIndex >= energies.size()) {
            activeIndex = -1;
        }
        inputItem.readNbt(nbt, registries);
        chargeItem.readNbt(nbt, registries);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag nbt = super.getUpdateTag(registries);
        saveAdditional(nbt, registries);
        return nbt;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public record ToggleContext(AbyssStorageBlockEntity blockEntity, Player player, int index) {
    }
}
