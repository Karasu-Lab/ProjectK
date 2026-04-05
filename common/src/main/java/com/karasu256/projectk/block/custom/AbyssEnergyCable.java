package com.karasu256.projectk.block.custom;

import com.karasu256.projectk.block.entity.AbyssEnergyCableBlockEntity;
import com.karasu256.projectk.block.entity.ProjectKBlockEntities;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.karasuniki.karasunikilib.api.block.ICableInputable;
import net.karasuniki.karasunikilib.api.block.ICableOutputable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AbyssEnergyCable extends BaseEntityBlock {
    public static final MapCodec<AbyssEnergyCable> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    ProjectKBlock.CustomProperties.CODEC.fieldOf("properties").forGetter(AbyssEnergyCable::getCustomProperties))
            .apply(instance, properties -> new AbyssEnergyCable(
                    BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).noOcclusion(), properties)));

    public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
    public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
    public static final BooleanProperty EAST = BlockStateProperties.EAST;
    public static final BooleanProperty WEST = BlockStateProperties.WEST;
    public static final BooleanProperty UP = BlockStateProperties.UP;
    public static final BooleanProperty DOWN = BlockStateProperties.DOWN;
    public static final EnumProperty<Direction> FACING = BlockStateProperties.FACING;
    public static final EnumProperty<ConnectionMode> MODE = EnumProperty.create("mode", ConnectionMode.class,
            mode -> mode != ConnectionMode.NONE);

    private static final VoxelShape CENTER = box(5, 5, 5, 11, 11, 11);
    private static final VoxelShape[] SIDES = new VoxelShape[Direction.values().length];
    private static final VoxelShape[] SIDES_PULL = new VoxelShape[Direction.values().length];
    private static final VoxelShape[] SIDES_PUSH = new VoxelShape[Direction.values().length];

    static {
        for (Direction dir : Direction.values()) {
            SIDES[dir.ordinal()] = makeSide(dir, 5, 11, 0, 5);
            SIDES_PULL[dir.ordinal()] = Shapes.or(makeSide(dir, 5, 11, 4, 5), makeSide(dir, 6, 10, 2, 4),
                    makeSide(dir, 4, 12, 0, 2));
            SIDES_PUSH[dir.ordinal()] = Shapes.or(makeSide(dir, 5, 11, 3, 5), makeSide(dir, 6, 10, 2, 3),
                    makeSide(dir, 7, 9, 0, 2));
        }
    }

    private final ProjectKBlock.CustomProperties customProperties;

    public AbyssEnergyCable(BlockBehaviour.Properties properties, ProjectKBlock.CustomProperties customProperties) {
        super(properties);
        this.customProperties = customProperties;
        registerDefaultState(defaultBlockState().setValue(NORTH, false).setValue(SOUTH, false).setValue(EAST, false)
                .setValue(WEST, false).setValue(UP, false).setValue(DOWN, false).setValue(FACING, Direction.NORTH)
                .setValue(MODE, ConnectionMode.CONNECTED));
    }

    private static VoxelShape appendIfConnected(VoxelShape base, BlockState state, BooleanProperty prop, Direction dir) {
        if (!state.getValue(prop)) {
            return base;
        }
        return Shapes.or(base, SIDES[dir.ordinal()]);
    }


    private static BooleanProperty getPropertyFor(Direction dir) {
        return switch (dir) {
            case NORTH -> NORTH;
            case SOUTH -> SOUTH;
            case EAST -> EAST;
            case WEST -> WEST;
            case UP -> UP;
            case DOWN -> DOWN;
        };
    }

    public static BooleanProperty getConnectionPropertyFor(Direction dir) {
        return getPropertyFor(dir);
    }

    public static boolean isConnected(BlockState state, Direction dir) {
        return state.getValue(getPropertyFor(dir));
    }

    public static BlockState setConnected(BlockState state, Direction dir, boolean connected) {
        return state.setValue(getPropertyFor(dir), connected);
    }

    private static BlockState updateConnections(LevelAccessor level, BlockPos pos, BlockState state) {
        for (Direction dir : Direction.values()) {
            boolean connect = canConnect(level, pos.relative(dir));
            state = state.setValue(getPropertyFor(dir), connect);
        }
        return state;
    }

    public static boolean canConnect(LevelAccessor level, BlockPos pos) {
        BlockState neighbor = level.getBlockState(pos);
        if (neighbor.getBlock() instanceof AbyssEnergyCable) {
            return true;
        }
        BlockEntity be = level.getBlockEntity(pos);
        return be instanceof ICableInputable || be instanceof ICableOutputable;
    }

    private static VoxelShape makeSide(Direction dir, double innerMin, double innerMax, double extMin, double extMax) {
        return switch (dir) {
            case DOWN -> box(innerMin, extMin, innerMin, innerMax, extMax, innerMax);
            case UP -> box(innerMin, 16 - extMax, innerMin, innerMax, 16 - extMin, innerMax);
            case NORTH -> box(innerMin, innerMin, extMin, innerMax, innerMax, extMax);
            case SOUTH -> box(innerMin, innerMin, 16 - extMax, innerMax, innerMax, 16 - extMin);
            case WEST -> box(extMin, innerMin, innerMin, extMax, innerMax, innerMax);
            case EAST -> box(16 - extMax, innerMin, innerMin, 16 - extMin, innerMax, innerMax);
        };
    }

    @Override
    @NotNull
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AbyssEnergyCableBlockEntity(pos, state);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null : createTickerHelper(type, ProjectKBlockEntities.ABYSS_ENERGY_CABLE.get(),
                AbyssEnergyCableBlockEntity::tick);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite())
                .setValue(MODE, ConnectionMode.CONNECTED);
        return updateConnections(context.getLevel(), context.getClickedPos(), state);
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos pos, BlockPos facingPos) {
        boolean connect = canConnect(level, facingPos);
        return state.setValue(getPropertyFor(facing), connect);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORTH, SOUTH, EAST, WEST, UP, DOWN, FACING, MODE);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return buildShape(state, level, pos);
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return buildShape(state, level, pos);
    }

    private VoxelShape buildShape(BlockState state, BlockGetter level, BlockPos pos) {
        VoxelShape shape = CENTER;
        Direction facing = state.getValue(FACING);
        ConnectionMode mode = state.getValue(MODE);
        for (Direction dir : Direction.values()) {
            if (state.getValue(getPropertyFor(dir))) {
                if (facing == dir) {
                    if (mode == ConnectionMode.INPUT) {
                        shape = Shapes.or(shape, SIDES_PULL[dir.ordinal()]);
                    } else if (mode == ConnectionMode.OUTPUT) {
                        shape = Shapes.or(shape, SIDES_PUSH[dir.ordinal()]);
                    } else {
                        shape = Shapes.or(shape, SIDES[dir.ordinal()]);
                    }
                } else {
                    shape = Shapes.or(shape, SIDES[dir.ordinal()]);
                }
            }
        }
        return shape;
    }

    public long getCapacity() {
        return customProperties.capacity();
    }

    public long getTransferRate() {
        return customProperties.transferRate();
    }

    private ProjectKBlock.CustomProperties getCustomProperties() {
        return customProperties;
    }

    public enum ConnectionMode implements StringRepresentable {
        NONE("none"), CONNECTED("connected"), INPUT("input"), OUTPUT("output");

        private final String id;

        ConnectionMode(String id) {
            this.id = id;
        }

        @Override
        public String getSerializedName() {
            return id;
        }

        public ConnectionMode next() {
            return switch (this) {
                case CONNECTED -> INPUT;
                case INPUT -> OUTPUT;
                case OUTPUT, NONE -> CONNECTED;
            };
        }
    }
}
