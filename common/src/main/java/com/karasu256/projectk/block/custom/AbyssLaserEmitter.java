package com.karasu256.projectk.block.custom;

import com.karasu256.projectk.block.entity.AbyssLaserEmitterBlockEntity;
import com.karasu256.projectk.block.entity.ProjectKBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AbyssLaserEmitter extends AbstractEnergyBlock {
    public static final EnumProperty<Mode> MODE = EnumProperty.create("mode", Mode.class);
    public static final EnumProperty<Direction> FACING = DirectionalBlock.FACING;

    public AbyssLaserEmitter(BlockBehaviour.Properties blockProperties, AbstractEnergyBlock.Properties energyProperties) {
        super(blockProperties, energyProperties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(MODE, Mode.PULSE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, MODE);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite());
    }

    @Override
    @NotNull
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide) {
            level.setBlock(pos, state.cycle(MODE), 3);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.SUCCESS;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AbyssLaserEmitterBlockEntity(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide) return null;
        return type == ProjectKBlockEntities.ABYSS_LASER_EMITTER.get() ? (level1, pos1, state1, blockEntity) -> AbyssLaserEmitterBlockEntity.tick(level1, pos1, state1, (AbyssLaserEmitterBlockEntity) blockEntity) : null;
    }

    public enum Mode implements StringRepresentable {
        PULSE("pulse"),
        DC("dc");

        private final String name;

        Mode(String name) {
            this.name = name;
        }

        @Override
        @NotNull
        public String getSerializedName() {
            return this.name;
        }
    }
}
