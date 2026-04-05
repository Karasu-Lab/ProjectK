package com.karasu256.projectk.client.screen;

import com.karasu256.projectk.block.entity.AbyssSynthesizerBlockEntity;
import com.karasu256.projectk.data.AbyssEnergyData;
import com.karasu256.projectk.menu.AbyssSynthesizerMenu;
import com.karasu256.projectk.utils.Id;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;

import java.util.*;
import java.util.stream.Collectors;

public class AbyssSynthesizerScreen extends AbstractContainerScreen<AbyssSynthesizerMenu> {
    private static final ResourceLocation TEXTURE = Id.id("textures/gui/abyss_synthesizer.png");
    private static final int DONUT_CENTER_X = 88;
    private static final int DONUT_CENTER_Y = 78;
    private static final float DONUT_CENTER_RADIUS = 56.0f;
    private static final float DONUT_THICKNESS = 12.0f;
    private static final float ARC_STEP = 2.0f;

    private static final int SAMPLING_INTERVAL = 20;
    private AbyssSynthesizerBlockEntity clientBe;
    private Button dumpButton;

    private List<AbyssEnergyData> targetEnergies = new ArrayList<>();
    private List<AbyssEnergyData> startEnergies = new ArrayList<>();
    private List<AbyssEnergyData> animatedEnergies = new ArrayList<>();
    private int samplingTickTimer = 0;
    private boolean initialSampleDone = false;
    private boolean introAnimationDone = false;

    public AbyssSynthesizerScreen(AbyssSynthesizerMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 240;
        this.inventoryLabelY = 146;
        this.titleLabelY = 6;
    }

    @Override
    protected void init() {
        super.init();

        if (this.menu.getSynthesizer() != null) {
            this.clientBe = this.menu.getSynthesizer();
        } else if (this.minecraft != null && this.minecraft.hitResult instanceof BlockHitResult blockHit && this.minecraft.level != null) {
            BlockEntity be = this.minecraft.level.getBlockEntity(blockHit.getBlockPos());
            if (be instanceof AbyssSynthesizerBlockEntity synthBe) {
                this.clientBe = synthBe;
            }
        }

        int dumpX = this.leftPos + 8;
        int dumpY = this.topPos + 125;
        this.dumpButton = this.addRenderableWidget(
                Button.builder(Component.translatable("gui.projectk.dump"), button -> {
                    if (this.minecraft != null && this.minecraft.gameMode != null) {
                        this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, 0);
                    }
                }).bounds(dumpX, dumpY, 40, 16).build());
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        if (dumpButton != null && clientBe != null) {
            dumpButton.active = !clientBe.getEnergyList().isEmpty();
        }

        samplingTickTimer++;
        if (!initialSampleDone) {
            if (samplingTickTimer >= 5) {
                initialSampleDone = true;
                samplingTickTimer = 0;
                startEnergies = new ArrayList<>();
                targetEnergies = clientBe != null ? new ArrayList<>(clientBe.getEnergyList()) : new ArrayList<>();
            }
        } else if (samplingTickTimer >= SAMPLING_INTERVAL) {
            samplingTickTimer = 0;
            introAnimationDone = true;
            startEnergies = new ArrayList<>(animatedEnergies);
            targetEnergies = clientBe != null ? new ArrayList<>(clientBe.getEnergyList()) : new ArrayList<>();
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        updateAnimation(partialTick);
        renderBackground(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
        renderEnergyTooltip(graphics, mouseX, mouseY);
    }

    private void updateAnimation(float partialTick) {
        float progress = Math.min(1.0f, (samplingTickTimer + partialTick) / (float) SAMPLING_INTERVAL);
        float easedProgress = (float) (Math.sin(Math.PI * progress - Math.PI / 2) * 0.5 + 0.5);

        Map<ResourceLocation, Long> startMap = startEnergies.stream()
                .collect(Collectors.toMap(AbyssEnergyData::energyId, AbyssEnergyData::amount, (a, b) -> a));
        Map<ResourceLocation, Long> targetMap = targetEnergies.stream()
                .collect(Collectors.toMap(AbyssEnergyData::energyId, AbyssEnergyData::amount, (a, b) -> a));

        Set<ResourceLocation> allIds = new HashSet<>(startMap.keySet());
        allIds.addAll(targetMap.keySet());

        animatedEnergies = new ArrayList<>();
        for (ResourceLocation id : allIds) {
            long sVal = startMap.getOrDefault(id, 0L);
            long tVal = targetMap.getOrDefault(id, 0L);
            long animated = (long) (sVal + (tVal - sVal) * easedProgress);
            if (animated > 0) {
                animatedEnergies.add(new AbyssEnergyData(id, animated));
            }
        }
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight, 176, 240);
        renderEnergyDonut(graphics);
    }

    private void renderEnergyDonut(GuiGraphics graphics) {
        if (clientBe == null)
            return;

        long capacity = clientBe.getEnergyCapacity();
        if (capacity <= 0)
            return;

        List<AbyssEnergyData> energies = animatedEnergies;
        long currentTotal = energies.stream().mapToLong(AbyssEnergyData::amount).sum();
        if (currentTotal <= 0)
            return;

        float progress = Math.min(1.0f, (samplingTickTimer + Minecraft.getInstance().getTimer()
                .getGameTimeDeltaPartialTick(true)) / (float) SAMPLING_INTERVAL);
        float easedProgress = (float) (Math.sin(Math.PI * progress - Math.PI / 2) * 0.5 + 0.5);

        long visualTotal = introAnimationDone ? currentTotal : (long) (currentTotal / Math.max(0.0001f, easedProgress));

        float cx = leftPos + DONUT_CENTER_X;
        float cy = topPos + DONUT_CENTER_Y;

        graphics.pose().pushPose();
        graphics.pose().translate(0, 0, 100);
        EnergyBarRenderer.renderDonut(graphics, cx, cy, energies, visualTotal, DONUT_CENTER_RADIUS, DONUT_THICKNESS,
                -90.0f);
        graphics.pose().popPose();
    }

    private void renderEnergyTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
        if (clientBe == null)
            return;

        long capacity = clientBe.getEnergyCapacity();
        if (capacity <= 0)
            return;

        List<AbyssEnergyData> energies = clientBe.getEnergyList();
        long currentTotal = energies.stream().mapToLong(AbyssEnergyData::amount).sum();
        if (currentTotal <= 0)
            return;

        float cx = leftPos + DONUT_CENTER_X;
        float cy = topPos + DONUT_CENTER_Y;

        EnergyBarRenderer.renderDonutTooltip(graphics, font, mouseX, mouseY, cx, cy, energies, currentTotal, capacity,
                DONUT_CENTER_RADIUS, DONUT_THICKNESS, -90.0f);
    }
}
