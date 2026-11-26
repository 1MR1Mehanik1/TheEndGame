package the_end_game.init.block.table_theories;

import gcapi_model_animation.init.basic.GCmaBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import the_end_game.init.InitBlock;
import the_end_game.init.InitItem;
import the_end_game.init.block.table.Table;
import the_end_game.init.block.table.TableEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class TableTheories extends GCmaBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty EXTENSION = BooleanProperty.create("extension");
    private static final VoxelShape FULL_NORTH = Shapes.or(
            Shapes.box(-1.0D, 0.875D, 0.0D, 1.0D, 1.0D, 1.0D),
            Shapes.box(-0.9375D, 0.8125D, 0.0625D, 0.9375D, 0.875D, 0.9375D),
            Shapes.box(-0.90625D, 0.1875D, 0.09375D, -0.71875D, 0.8125D, 0.28125D),
            Shapes.box(-0.90625D, 0.1875D, 0.71875D, -0.71875D, 0.8125D, 0.90625D),
            Shapes.box(0.71875D, 0.1875D, 0.09375D, 0.90625D, 0.8125D, 0.28125D),
            Shapes.box(0.71875D, 0.1875D, 0.71875D, 0.90625D, 0.8125D, 0.90625D),
            Shapes.box(-0.9375D, 0.0D, 0.0625D, -0.6875D, 0.1875D, 0.3125D),
            Shapes.box(-0.9375D, 0.0D, 0.6875D, -0.6875D, 0.1875D, 0.9375D),
            Shapes.box(0.6875D, 0.0D, 0.0625D, 0.9375D, 0.1875D, 0.3125D),
            Shapes.box(0.6875D, 0.0D, 0.6875D, 0.9375D, 0.1875D, 0.9375D)
    );
    private static final VoxelShape SHAPE_NORTH = FULL_NORTH;
    private static final VoxelShape SHAPE_SOUTH = rotate(FULL_NORTH, Direction.SOUTH);
    private static final VoxelShape SHAPE_EAST = rotate(FULL_NORTH, Direction.EAST);
    private static final VoxelShape SHAPE_WEST = rotate(FULL_NORTH, Direction.WEST);
    private static final VoxelShape EXTENSION_SHAPE_NORTH = buildExtensionShape(Direction.NORTH);
    private static final VoxelShape EXTENSION_SHAPE_SOUTH = buildExtensionShape(Direction.SOUTH);
    private static final VoxelShape EXTENSION_SHAPE_EAST = buildExtensionShape(Direction.EAST);
    private static final VoxelShape EXTENSION_SHAPE_WEST = buildExtensionShape(Direction.WEST);

    public TableTheories(Properties properties, Supplier<? extends BlockEntityType<?>> type, ResourceLocation model, ResourceLocation texture) {
        super(properties, type, model, texture);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(EXTENSION, false));
    }

    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, EXTENSION);
    }

    @Override public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getHorizontalDirection().getOpposite();
        BlockPos extensionPos = context.getClickedPos().relative(facing.getCounterClockWise());
        if (!context.getLevel().getBlockState(extensionPos).canBeReplaced(context)) return null;
        return defaultBlockState().setValue(FACING, facing).setValue(EXTENSION, false);
    }

    public static VoxelShape tableShape(Direction facing) {
        return switch (facing) {
            case SOUTH -> SHAPE_SOUTH;
            case EAST -> SHAPE_EAST;
            case WEST -> SHAPE_WEST;
            default -> SHAPE_NORTH;
        };
    }

    public static VoxelShape extensionShape(Direction facing) {
        return switch (facing) {
            case SOUTH -> EXTENSION_SHAPE_SOUTH;
            case EAST -> EXTENSION_SHAPE_EAST;
            case WEST -> EXTENSION_SHAPE_WEST;
            default -> EXTENSION_SHAPE_NORTH;
        };
    }

    private static VoxelShape buildExtensionShape(Direction facing) {
        Direction offset = facing.getCounterClockWise();
        VoxelShape shifted = translate(tableShape(facing), -offset.getStepX(), -offset.getStepZ());
        return Shapes.join(shifted, Shapes.block(), BooleanOp.AND);
    }

    private static VoxelShape translate(VoxelShape shape, double dx, double dz) {
        List<double[]> boxes = new ArrayList<>();
        shape.forAllBoxes((x1, y1, z1, x2, y2, z2) -> boxes.add(new double[]{x1, y1, z1, x2, y2, z2}));
        VoxelShape result = Shapes.empty();
        for (double[] box : boxes) {
            result = Shapes.or(result, Shapes.box(box[0] + dx, box[1], box[2] + dz, box[3] + dx, box[4], box[5] + dz));
        }
        return result;
    }

    private static VoxelShape rotate(VoxelShape shape, Direction facing) {
        if (facing == Direction.NORTH) return shape;
        List<double[]> boxes = new ArrayList<>();
        shape.forAllBoxes((x1, y1, z1, x2, y2, z2) -> boxes.add(new double[]{x1, y1, z1, x2, y2, z2}));
        VoxelShape result = Shapes.empty();
        for (double[] box : boxes) {
            double x1;
            double z1;
            double x2;
            double z2;
            switch (facing) {
                case SOUTH -> {
                    x1 = 1.0D - box[3];
                    z1 = 1.0D - box[5];
                    x2 = 1.0D - box[0];
                    z2 = 1.0D - box[2];
                }
                case EAST -> {
                    x1 = 1.0D - box[5];
                    z1 = box[0];
                    x2 = 1.0D - box[2];
                    z2 = box[3];
                }
                case WEST -> {
                    x1 = box[2];
                    z1 = 1.0D - box[3];
                    x2 = box[5];
                    z2 = 1.0D - box[0];
                }
                default -> {
                    x1 = box[0];
                    z1 = box[2];
                    x2 = box[3];
                    z2 = box[5];
                }
            }
            result = Shapes.or(result, Shapes.box(x1, box[1], z1, x2, box[4], z2));
        }
        return result;
    }

    public static boolean ensureExtension(Level level, BlockPos pos, BlockState state) {
        if (state.getValue(EXTENSION)) return true;
        BlockPos extensionPos = pos.relative(state.getValue(FACING).getCounterClockWise());
        if (!level.isLoaded(extensionPos) || !level.getBlockState(extensionPos).canBeReplaced()) return false;
        return level.setBlock(extensionPos, state.setValue(EXTENSION, true), Block.UPDATE_ALL);
    }

    @Override public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(EXTENSION) ? extensionShape(state.getValue(FACING)) : tableShape(state.getValue(FACING));
    }

    @Override public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(EXTENSION) ? Shapes.empty() : getShape(state, level, pos, context);
    }

    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return state.getValue(EXTENSION) ? null : new TableTheoriesEntity(getBlockEntityType(), pos, state);
    }

    @Override public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        if (!level.isClientSide) ensureExtension(level, pos, state);
    }

    @Override public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        if (!ensureExtension(level, pos, state)) level.removeBlock(pos, false);
    }

    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock e) {
        if (e.getUseBlock() == TriState.DEFAULT && e.getLevel().getBlockState(e.getPos()).getBlock() instanceof TableTheories) e.setUseBlock(TriState.TRUE);
    }

    @Override public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (state.getValue(EXTENSION)) {
            BlockPos masterPos = pos.relative(state.getValue(FACING).getClockWise());
            BlockState masterState = level.getBlockState(masterPos);
            if (masterState.is(this) && !masterState.getValue(EXTENSION)) return ((TableTheories) masterState.getBlock()).useWithoutItem(masterState, level, masterPos, player, hit);
            return InteractionResult.PASS;
        }
        if (!(level.getBlockEntity(pos) instanceof TableTheoriesEntity table)) return InteractionResult.PASS;
        if (level.isClientSide) return InteractionResult.SUCCESS;
        ensureExtension(level, pos, state);
        if (player instanceof ServerPlayer serverPlayer) serverPlayer.openMenu(table);
        return InteractionResult.CONSUME;
    }

    @Override public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide) {
            Direction facing = state.getValue(FACING);
            BlockPos masterPos = state.getValue(EXTENSION) ? pos.relative(facing.getClockWise()) : pos;
            BlockPos survivor = state.getValue(EXTENSION) ? masterPos : pos.relative(facing.getCounterClockWise());
            if (level.getBlockEntity(masterPos) instanceof TableTheoriesEntity table) {
                BlockState survivorState = level.getBlockState(survivor);
                boolean paired = survivorState.is(this) && survivorState.getValue(EXTENSION) != state.getValue(EXTENSION);
                if (paired) restoreTable(level, survivor, facing, table);
                else dropItems(level, pos, table);
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    private static void dropItems(Level level, BlockPos pos, TableTheoriesEntity source) {
        for (ItemStack stack : source.getItems()) {
            if (!stack.isEmpty()) Block.popResource(level, pos, stack.copy());
        }
    }

    private static void restoreTable(Level level, BlockPos pos, Direction facing, TableTheoriesEntity source) {
        BlockState tableState = InitBlock.TABLE.get().defaultBlockState()
                .setValue(Table.ROTATION, Math.floorMod(Math.round((facing.toYRot() + 180.0F) / 45.0F), 8))
                .setValue(Table.WATERLOGGED, false);
        if (!level.setBlock(pos, tableState, Block.UPDATE_ALL) || !(level.getBlockEntity(pos) instanceof TableEntity table)) {
            dropItems(level, pos, source);
            return;
        }

        int paper = 0;
        ItemStack paperType = ItemStack.EMPTY;
        ItemStack inkwell = ItemStack.EMPTY;
        ItemStack feather = ItemStack.EMPTY;
        List<ItemStack> extras = new ArrayList<>();
        for (int slot = 0; slot < source.getContainerSize(); slot++) {
            ItemStack stack = source.getItem(slot);
            if (stack.isEmpty()) continue;
            if (stack.is(Items.PAPER)) {
                if (paperType.isEmpty()) paperType = stack.copyWithCount(1);
                paper += stack.getCount();
            } else if (slot == 1 && stack.is(InitItem.INKWELL.get())) {
                inkwell = stack.copy();
            } else if (slot == 2 && stack.is(InitItem.WRITING_FEATHER.get())) {
                feather = stack.copy();
            } else {
                extras.add(stack.copy());
            }
        }

        if (paper > 0) {
            ItemStack paperStack = paperType.copyWithCount(paper);
            table.placeStack(paperStack, paper, true, Direction.UP, 0.0F);
            if (!paperStack.isEmpty()) extras.add(paperStack);
        }
        if (!inkwell.isEmpty()) {
            table.placeStack(inkwell, inkwell.getCount(), true, Direction.UP, 0.0F);
            if (!inkwell.isEmpty()) extras.add(inkwell);
        }
        if (!feather.isEmpty()) {
            table.placeStack(feather, feather.getCount(), true, Direction.UP, 0.0F);
            if (!feather.isEmpty()) extras.add(feather);
        }
        for (ItemStack stack : extras) {
            if (!stack.isEmpty()) Block.popResource(level, pos, stack.copy());
        }
    }

    @Override public List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        return List.of(new ItemStack(InitBlock.TABLE.get()));
    }
}
