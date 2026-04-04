package com.karasu256.projectk.block.entity;

import com.karasu256.projectk.block.custom.AbyssStorage;
import com.karasu256.projectk.data.AbyssEnergyData;
import com.karasu256.projectk.data.ProjectKDataComponets;
import com.karasu256.projectk.data.EnergyCapacityData;
import com.karasu256.projectk.energy.IEnergyItemInput;
import com.karasu256.projectk.energy.IEnergyItemOutput;
import com.karasu256.projectk.energy.IEnergyListHolder;
import com.karasu256.projectk.energy.ProjectKEnergies;
import com.karasu256.projectk.menu.AbyssStorageMenu;
import com.karasu256.projectk.utils.Id;
import net.karasuniki.karasunikilib.api.block.ICableInputable;
import net.karasuniki.karasunikilib.api.block.ICableOutputable;
import net.karasuniki.karasunikilib.api.block.entity.impl.KarasuCoreBlockEntity;
import net.karasuniki.karasunikilib.api.data.ICapacity;
import net.karasuniki.karasunikilib.api.data.impl.EnergyValue;
import net.karasuniki.karasunikilib.api.data.impl.HeldItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.network.chat.Component;
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

public class AbyssStorageBlockEntity extends KarasuCoreBlockEntity implements ICableInputable, ICableOutputable, ICapacity, MenuProvider, IEnergyListHolder, IEnergyItemInput, IEnergyItemOutput {
    private static final int SLOT_COUNT = 3;
    private static final String ENERGY_LIST_KEY = "projectk:abyss_energy_list";
    private static Predicate<ToggleContext> TOGGLE_CONDITION = context -> true;
    private final EnergyValue energyOne = new EnergyValue();
    private final EnergyValue energyTwo = new EnergyValue();
    private final EnergyValue energyThree = new EnergyValue();
    private final HeldItem inputItem = new HeldItem(Id.id("storage_input"));
    private final HeldItem chargeItem = new HeldItem(Id.id("storage_charge"));

    private long capacity;
    private int maxTypes;
    private int activeIndex = -1;

    public AbyssStorageBlockEntity(BlockPos pos, BlockState state) {
        super(ProjectKBlockEntities.ABYSS_STORAGE.get(), pos, state);
        this.capacity = resolveCapacity(state);
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
        EnergyValue slot = findInsertSlot(id);
        if (slot == null) {
            return 0;
        }
        long received = Math.min(capacity - slot.getValue(), maxAmount);
        if (received <= 0) {
            return 0;
        }
        if (!simulate) {
            if (slot.getValue() == 0) {
                slot.setId(id);
            }
            slot.setValue(slot.getValue() + received);
            if (activeIndex < 0) {
                activeIndex = resolveSlotIndex(slot);
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
        EnergyValue slot = getActiveSlot();
        if (slot == null) {
            return 0;
        }
        long extracted = Math.min(slot.getValue(), maxAmount);
        if (extracted <= 0) {
            return 0;
        }
        if (!simulate) {
            slot.setValue(slot.getValue() - extracted);
            if (slot.getValue() <= 0) {
                clearSlot(slot);
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

    public ResourceLocation getEnergyId1() {
        return energyOne.getValue() == 0 ? null : energyOne.getId();
    }

    public ResourceLocation getEnergyId2() {
        return energyTwo.getValue() == 0 ? null : energyTwo.getId();
    }

    public ResourceLocation getEnergyId3() {
        return energyThree.getValue() == 0 ? null : energyThree.getId();
    }

    public long getEnergyAmount1() {
        return energyOne.getValue();
    }

    public long getEnergyAmount2() {
        return energyTwo.getValue();
    }

    public long getEnergyAmount3() {
        return energyThree.getValue();
    }

    public int getActiveIndex() {
        return activeIndex;
    }

    @Override
    public List<EnergyEntry> getEnergyEntries() {
        List<EnergyEntry> entries = new ArrayList<>();
        if (getEnergyId1() != null && getEnergyAmount1() > 0) {
            entries.add(new EnergyEntry(getEnergyId1(), getEnergyAmount1(), capacity, activeIndex == 0));
        }
        if (getEnergyId2() != null && getEnergyAmount2() > 0) {
            entries.add(new EnergyEntry(getEnergyId2(), getEnergyAmount2(), capacity, activeIndex == 1));
        }
        if (getEnergyId3() != null && getEnergyAmount3() > 0) {
            entries.add(new EnergyEntry(getEnergyId3(), getEnergyAmount3(), capacity, activeIndex == 2));
        }
        return entries;
    }

    public boolean toggleActive(int index, Player player) {
        if (index < 0 || index >= SLOT_COUNT) {
            return false;
        }
        EnergyValue slot = getSlot(index);
        if (slot == null || slot.getValue() <= 0) {
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
            if (data == null || data.energyId() == null || data.amount() <= 0) {
                continue;
            }
            if (findExactSlot(data.energyId()) != null) {
                return true;
            }
            if (countEnergyTypes() < maxTypes) {
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
            if (data == null || data.energyId() == null || data.amount() <= 0) {
                entries.remove(i);
                changed = true;
                continue;
            }
            long inserted = insert(data.energyId(), data.amount(), false);
            if (inserted > 0) {
                long remaining = data.amount() - inserted;
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
        long current = index >= 0 ? energies.get(index).amount() : 0L;
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
        if (tag.contains(ENERGY_LIST_KEY, Tag.TAG_LIST)) {
            ListTag listTag = tag.getList(ENERGY_LIST_KEY, Tag.TAG_COMPOUND);
            for (int i = 0; i < listTag.size(); i++) {
                AbyssEnergyData.CODEC.parse(NbtOps.INSTANCE, listTag.getCompound(i)).result().ifPresent(list::add);
            }
        }
        if (list.isEmpty()) {
            AbyssEnergyData data = stack.get(ProjectKDataComponets.ABYSS_ENERGY_DATA_COMPONENT_TYPE.get());
            if (data != null && data.amount() > 0 && data.energyId() != null) {
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
            AbyssEnergyData.CODEC.encodeStart(NbtOps.INSTANCE, data).result().ifPresent(element -> listTag.add(element));
        }
        tag.put(ENERGY_LIST_KEY, listTag);
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
        EnergyValue slot = getActiveSlot();
        return slot != null && id.equals(slot.getId());
    }

    @Nullable
    private ResourceLocation getActiveEnergyId() {
        EnergyValue slot = getActiveSlot();
        return slot == null ? null : slot.getId();
    }

    private long getActiveEnergyAmount() {
        EnergyValue slot = getActiveSlot();
        return slot == null ? 0L : slot.getValue();
    }

    @Nullable
    private EnergyValue getActiveSlot() {
        return getSlot(activeIndex);
    }

    @Nullable
    private EnergyValue getSlot(int index) {
        return switch (index) {
            case 0 -> energyOne;
            case 1 -> energyTwo;
            case 2 -> energyThree;
            default -> null;
        };
    }

    @Nullable
    private EnergyValue findExactSlot(ResourceLocation id) {
        if (energyOne.getValue() > 0 && id.equals(energyOne.getId())) {
            return energyOne;
        }
        if (energyTwo.getValue() > 0 && id.equals(energyTwo.getId())) {
            return energyTwo;
        }
        if (energyThree.getValue() > 0 && id.equals(energyThree.getId())) {
            return energyThree;
        }
        return null;
    }

    @Nullable
    private EnergyValue findInsertSlot(ResourceLocation id) {
        EnergyValue exact = findExactSlot(id);
        if (exact != null) {
            return exact;
        }
        if (countEnergyTypes() >= maxTypes) {
            return null;
        }
        if (energyOne.getValue() == 0) {
            return energyOne;
        }
        if (energyTwo.getValue() == 0) {
            return energyTwo;
        }
        if (energyThree.getValue() == 0) {
            return energyThree;
        }
        return null;
    }

    private void clearSlot(EnergyValue slot) {
        slot.setValue(0);
        slot.setId(null);
        if (resolveSlotIndex(slot) == activeIndex) {
            activeIndex = -1;
        }
    }

    private int resolveSlotIndex(EnergyValue slot) {
        if (slot == energyOne) {
            return 0;
        }
        if (slot == energyTwo) {
            return 1;
        }
        if (slot == energyThree) {
            return 2;
        }
        return -1;
    }

    private int countEnergyTypes() {
        int count = 0;
        if (energyOne.getValue() > 0) {
            count++;
        }
        if (energyTwo.getValue() > 0) {
            count++;
        }
        if (energyThree.getValue() > 0) {
            count++;
        }
        return count;
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
        energyOne.setCapacity(capacity);
        energyTwo.setCapacity(capacity);
        energyThree.setCapacity(capacity);
        CompoundTag energyOneTag = new CompoundTag();
        CompoundTag energyTwoTag = new CompoundTag();
        CompoundTag energyThreeTag = new CompoundTag();
        energyOne.writeNbt(energyOneTag, registries);
        energyTwo.writeNbt(energyTwoTag, registries);
        energyThree.writeNbt(energyThreeTag, registries);
        nbt.put("energy_one", energyOneTag);
        nbt.put("energy_two", energyTwoTag);
        nbt.put("energy_three", energyThreeTag);
        nbt.putInt("active_index", activeIndex);
        nbt.putLong("capacity", capacity);
        nbt.putInt("max_types", maxTypes);
        inputItem.writeNbt(nbt, registries);
        chargeItem.writeNbt(nbt, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        super.loadAdditional(nbt, registries);
        if (nbt.contains("energy_one")) {
            energyOne.readNbt(nbt.getCompound("energy_one"), registries);
        }
        if (nbt.contains("energy_two")) {
            energyTwo.readNbt(nbt.getCompound("energy_two"), registries);
        }
        if (nbt.contains("energy_three")) {
            energyThree.readNbt(nbt.getCompound("energy_three"), registries);
        }
        activeIndex = nbt.getInt("active_index");
        if (nbt.contains("capacity")) {
            capacity = nbt.getLong("capacity");
        }
        if (nbt.contains("max_types")) {
            maxTypes = nbt.getInt("max_types");
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
