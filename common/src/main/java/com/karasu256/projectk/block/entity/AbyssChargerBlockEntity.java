package com.karasu256.projectk.block.entity;

import com.karasu256.projectk.block.custom.AbyssCharger;
import com.karasu256.projectk.block.entity.impl.AbstractPKEnergyBlockEntity;
import com.karasu256.projectk.data.AbyssEnergyData;
import com.karasu256.projectk.data.EnergyCapacityData;
import com.karasu256.projectk.data.ProjectKDataComponets;
import com.karasu256.projectk.energy.AbyssEnergy;
import com.karasu256.projectk.energy.IEnergyListHolder;
import com.karasu256.projectk.energy.ProjectKEnergies;
import com.karasu256.projectk.menu.AbyssChargerMenu;
import com.karasu256.projectk.utils.Id;
import net.karasuniki.karasunikilib.api.block.ICableInputable;
import net.karasuniki.karasunikilib.api.data.impl.HeldItem;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public class AbyssChargerBlockEntity extends AbstractPKEnergyBlockEntity<AbyssEnergy> implements MenuProvider, IEnergyListHolder, ICableInputable {
    private static final String ENERGY_LIST_KEY = "projectk:abyss_energy_list";
    private final HeldItem inputItem = new HeldItem(Id.id("abyss_charger_input"));
    private final HeldItem outputItem = new HeldItem(Id.id("abyss_charger_output"));

    private long capacity;
    private long transferRate;

    public AbyssChargerBlockEntity(BlockPos pos, BlockState state) {
        super(ProjectKBlockEntities.ABYSS_CHARGER.get(), pos, state, resolveCapacity(state));
        this.capacity = resolveCapacity(state);
        this.transferRate = resolveTransferRate(state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, AbyssChargerBlockEntity be) {
        if (level.isClientSide) {
            return;
        }
        be.serverTick();
    }

    private static long resolveCapacity(BlockState state) {
        if (state.getBlock() instanceof AbyssCharger charger) {
            return charger.getCapacity();
        }
        return 0L;
    }

    private static long resolveTransferRate(BlockState state) {
        if (state.getBlock() instanceof AbyssCharger charger) {
            return charger.getTransferRate();
        }
        return 0L;
    }

    @Override
    protected AbyssEnergy createEnergy() {
        return new AbyssEnergy(0L);
    }

    @Override
    public List<EnergyEntry> getEnergyEntries() {
        ResourceLocation id = getAbyssEnergyId();
        if (id == null || getAmount() <= 0) {
            return List.of();
        }
        return List.of(new EnergyEntry(id, getAmount(), getCapacity(), false));
    }

    @Override
    protected boolean canOutputEnergy() {
        return false;
    }

    private void serverTick() {
        chargeFromItem();
        chargeItem();
    }

    private void chargeFromItem() {
        ItemStack input = getInputItem();
        if (input.isEmpty()) {
            return;
        }
        List<AbyssEnergyData> energies = readEnergyList(input);
        if (energies.isEmpty()) {
            return;
        }
        int selectedIndex = findPriorityIndex(energies);
        if (selectedIndex < 0) {
            return;
        }
        AbyssEnergyData data = energies.get(selectedIndex);
        if (!canAcceptEnergy(data.energyId())) {
            return;
        }
        long room = capacity - getAmount();
        if (room <= 0) {
            return;
        }
        long maxMove = transferRate > 0 ? Math.min(transferRate, room) : room;
        long toMove = Math.min(maxMove, data.amount());
        if (toMove <= 0) {
            return;
        }
        long inserted = insert(data.energyId(), toMove, false);
        if (inserted <= 0) {
            return;
        }
        long remaining = data.amount() - inserted;
        if (remaining <= 0) {
            energies.remove(selectedIndex);
        } else {
            energies.set(selectedIndex, new AbyssEnergyData(data.energyId(), remaining));
        }
        writeEnergyList(input, energies);
        setInputItem(input);
    }

    private void chargeItem() {
        ItemStack output = getOutputItem();
        if (output.isEmpty()) {
            return;
        }
        EnergyCapacityData capacityData = output.get(ProjectKDataComponets.ENERGY_CAPACITY_DATA_COMPONENT_TYPE.get());
        if (capacityData == null || capacityData.capacity() <= 0) {
            if (capacity <= 0) {
                return;
            }
            capacityData = new EnergyCapacityData(capacity);
            output.set(ProjectKDataComponets.ENERGY_CAPACITY_DATA_COMPONENT_TYPE.get(), capacityData);
            setOutputItem(output);
        }
        ResourceLocation energyId = getAbyssEnergyId();
        if (energyId == null || getAmount() <= 0) {
            return;
        }
        List<AbyssEnergyData> energies = readEnergyList(output);
        int existingIndex = findEnergyIndex(energies, energyId);
        long current = existingIndex >= 0 ? energies.get(existingIndex).amount() : 0L;
        long cap = capacityData.capacity();
        if (current >= cap) {
            return;
        }
        long maxMove = transferRate > 0 ? Math.min(transferRate, cap - current) : (cap - current);
        long toMove = Math.min(maxMove, getAmount());
        if (toMove <= 0) {
            return;
        }
        long extracted = extract(energyId, toMove, false);
        if (extracted <= 0) {
            return;
        }
        long nextAmount = current + extracted;
        if (existingIndex >= 0) {
            energies.set(existingIndex, new AbyssEnergyData(energyId, nextAmount));
        } else {
            energies.add(new AbyssEnergyData(energyId, nextAmount));
        }
        writeEnergyList(output, energies);
        setOutputItem(output);
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
        return outputItem.getHeldItem();
    }

    public void setOutputItem(ItemStack stack) {
        outputItem.setHeldItem(stack);
        setChanged();
        sync();
    }

    private int findPriorityIndex(List<AbyssEnergyData> list) {
        long maxAmount = -1L;
        int bestIndex = -1;
        for (int i = 0; i < list.size(); i++) {
            AbyssEnergyData data = list.get(i);
            if (data == null || data.energyId() == null || data.amount() <= 0) {
                continue;
            }
            long amount = data.amount();
            if (amount > maxAmount) {
                maxAmount = amount;
                bestIndex = i;
            }
        }
        return bestIndex;
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
            AbyssEnergyData.CODEC.encodeStart(NbtOps.INSTANCE, data).result().ifPresent(listTag::add);
        }
        tag.put(ENERGY_LIST_KEY, listTag);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    public boolean canAcceptInput(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        AbyssEnergyData data = stack.get(ProjectKDataComponets.ABYSS_ENERGY_DATA_COMPONENT_TYPE.get());
        return data != null && data.amount() > 0 && data.energyId() != null;
    }

    public boolean canAcceptOutput(ItemStack stack) {
        return !stack.isEmpty();
    }

    public int getDataValue(int index) {
        return switch (index) {
            case 0 -> (int) getAmount();
            case 1 -> (int) (getAmount() >>> 32);
            case 2 -> (int) getCapacity();
            case 3 -> (int) (getCapacity() >>> 32);
            case 4 -> getAbyssEnergyId() == null ? 0 : ProjectKEnergies.getModelIndex(getAbyssEnergyId());
            default -> 0;
        };
    }

    public void setDataValue(int index, int value) {
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.projectk.abyss_charger");
    }

    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory inventory, Player player) {
        return new AbyssChargerMenu(syncId, inventory, this);
    }

    @Override
    protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        super.saveAdditional(nbt, registries);
        inputItem.writeNbt(nbt, registries);
        outputItem.writeNbt(nbt, registries);
        nbt.putLong("capacity", capacity);
        nbt.putLong("transfer_rate", transferRate);
    }

    @Override
    protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        super.loadAdditional(nbt, registries);
        inputItem.readNbt(nbt, registries);
        outputItem.readNbt(nbt, registries);
        if (nbt.contains("capacity")) {
            capacity = nbt.getLong("capacity");
        }
        if (nbt.contains("transfer_rate")) {
            transferRate = nbt.getLong("transfer_rate");
        }
    }
}
