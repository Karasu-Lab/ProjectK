package com.karasu256.projectk.block.custom;

import com.karasu256.projectk.block.entity.AbyssAlchemyBlendMachineBlockEntity;
import com.karasu256.projectk.block.entity.ProjectKBlockEntities;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AbyssAlchemyBlendMachine extends BaseEntityBlock {
    public static final MapCodec<AbyssAlchemyBlendMachine> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    ProjectKBlock.CustomProperties.CODEC.fieldOf("properties")
                            .forGetter(AbyssAlchemyBlendMachine::getCustomProperties)
            ).apply(instance, properties -> new AbyssAlchemyBlendMachine(
                    BlockBehaviour.Properties.ofFullCopy(Blocks.ENCHANTING_TABLE), properties)));

    private static final VoxelShape SHAPE = box(0.0, 0.0, 0.0, 16.0, 12.0, 16.0);

    private final ProjectKBlock.CustomProperties customProperties;

    public AbyssAlchemyBlendMachine(BlockBehaviour.Properties properties, ProjectKBlock.CustomProperties customProperties) {
        super(properties);
        this.customProperties = customProperties;
    }

    @Override
    @NotNull
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AbyssAlchemyBlendMachineBlockEntity(pos, state);
    }

    @Override
    @NotNull
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected boolean useShapeForLightOcclusion(BlockState state) {
        return true;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    @NotNull
    protected InteractionResult useWithoutItem(BlockState state, @NotNull Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.isClientSide)
            return InteractionResult.SUCCESS;

        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof AbyssAlchemyBlendMachineBlockEntity machine) {
            player.openMenu(machine);
            return InteractionResult.SUCCESS;
        }
        return super.useWithoutItem(state, level, pos, player, hitResult);
    }

    @Override
    public void onRemove(@NotNull BlockState state, Level level, BlockPos pos, @NotNull BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock())) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof AbyssAlchemyBlendMachineBlockEntity machine) {
                Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), machine.getInputItem());
                Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), machine.getOutputItem());
                machine.dropTierUpgrades(level, pos);
            }
            super.onRemove(state, level, pos, newState, movedByPiston);
        }
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null : createTickerHelper(type,
                ProjectKBlockEntities.ABYSS_ALCHEMY_BLEND_MACHINE.get(), AbyssAlchemyBlendMachineBlockEntity::tick);
    }

    public long getCapacity() {
        return customProperties.capacity();
    }

    private ProjectKBlock.CustomProperties getCustomProperties() {
        return customProperties;
    }
}
