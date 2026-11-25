package the_end_game.init.block.table;

import gcapi_model_animation.init.basic.GCmaBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.WoolCarpetBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class Table extends GCmaBlock implements SimpleWaterloggedBlock {
    public static final IntegerProperty ROTATION = IntegerProperty.create("rotation", 0, 7);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    private static final VoxelShape TOP = Shapes.or(
            Block.box(0.0D, 12.0D, 0.0D, 16.0D, 16.0D, 16.0D),
            Block.box(1.0D, 11.0D, 1.0D, 15.0D, 12.0D, 15.0D)
    );
    private static final VoxelShape LEG_SE = Shapes.or(Block.box(10.5D, 0.0D, 10.5D, 13.5D, 12.0D, 13.5D));
    private static final VoxelShape LEG_NE = Shapes.or(Block.box(10.5D, 0.0D, 2.5D, 13.5D, 12.0D, 5.5D));
    private static final VoxelShape LEG_SW = Shapes.or(Block.box(2.5D, 0.0D, 10.5D, 5.5D, 12.0D, 13.5D));
    private static final VoxelShape LEG_NW = Shapes.or(Block.box(2.5D, 0.0D, 2.5D, 5.5D, 12.0D, 5.5D));
    private static final VoxelShape[] SHAPES = new VoxelShape[16];

    static {
        for (int mask = 0; mask < 16; mask++) {
            boolean north = (mask & 1) != 0;
            boolean south = (mask & 2) != 0;
            boolean west = (mask & 4) != 0;
            boolean east = (mask & 8) != 0;
            VoxelShape shape = TOP;
            if (!east && !south) shape = Shapes.or(shape, LEG_SE);
            if (!east && !north) shape = Shapes.or(shape, LEG_NE);
            if (!west && !south) shape = Shapes.or(shape, LEG_SW);
            if (!west && !north) shape = Shapes.or(shape, LEG_NW);
            SHAPES[mask] = shape;
        }
    }

    public Table(Properties properties, Supplier<? extends BlockEntityType<?>> type, ResourceLocation model, ResourceLocation texture) {
        super(properties, type, model, texture);
        this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, Boolean.valueOf(false)));
    }

    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(ROTATION).add(WATERLOGGED);
    }

    @Override protected FluidState getFluidState(BlockState state) { return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state); }

    @Override public BlockState getStateForPlacement(BlockPlaceContext context) {
        int rotation = context.getPlayer() == null ? 0 : Math.floorMod(Math.round((context.getPlayer().getYRot() + 180.0F) / 45.0F), 8);
        boolean waterlogged = context.getLevel().getFluidState(context.getClickedPos()).is(Fluids.WATER);
        return this.defaultBlockState().setValue(ROTATION, rotation).setValue(WATERLOGGED, waterlogged);
    }

    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new TableEntity(getBlockEntityType(), pos, state); }
    @Override public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) { return buildShape(level, pos); }
    @Override public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) { return buildShape(level, pos); }

    @Override protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        if (state.getValue(WATERLOGGED)) { level.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level)); }
        return super.updateShape(state, facing, facingState, level, currentPos, facingPos);
    }

    private static VoxelShape buildShape(BlockGetter level, BlockPos pos) {
        int mask = 0;
        if (isTable(level, pos.north())) mask |= 1;
        if (isTable(level, pos.south())) mask |= 2;
        if (isTable(level, pos.west())) mask |= 4;
        if (isTable(level, pos.east())) mask |= 8;
        return SHAPES[mask];
    }

    private static boolean isTable(BlockGetter level, BlockPos pos) { return level.getBlockState(pos).getBlock() instanceof Table; }

    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock e) {
        if (e.getUseBlock() == TriState.DEFAULT && e.getLevel().getBlockState(e.getPos()).getBlock() instanceof Table) { e.setUseBlock(TriState.TRUE); }
    }

    @Override public ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (stack.isEmpty()) {
            if (hit.getDirection() != Direction.UP) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            if (!(level.getBlockEntity(pos) instanceof TableEntity table)) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            int target = table.findItemAt(hit);
            if (target < 0) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            boolean shift = player.isShiftKeyDown();
            return take(level, pos, player, target, shift ? 1 : Integer.MAX_VALUE) ? ItemInteractionResult.sidedSuccess(level.isClientSide) : ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (player.isShiftKeyDown() && hit.getDirection() == Direction.UP && level.getBlockEntity(pos) instanceof TableEntity table) {
            int target = table.findItemAt(hit);
            if (target >= 0) {
                take(level, pos, player, target, 1);
                return ItemInteractionResult.sidedSuccess(level.isClientSide);
            }
        }

        Block heldBlock = Block.byItem(stack.getItem());
        if (heldBlock instanceof WoolCarpetBlock carpet) {
            if (!(level.getBlockEntity(pos) instanceof TableEntity table)) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            DyeColor color = carpet.getColor();
            if (!level.isClientSide && table.getClothColor() != color) {
                ItemStack old = table.setCloth(color);
                if (!old.isEmpty()) giveOrDrop(player, old);
                if (!player.hasInfiniteMaterials()) stack.shrink(1);
                level.playSound(null, pos, SoundEvents.WOOL_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }

        if (stack.is(Items.SHEARS) && level.getBlockEntity(pos) instanceof TableEntity table && table.hasCloth()) {
            if (!level.isClientSide) {
                ItemStack old = table.removeCloth();
                if (!old.isEmpty()) giveOrDrop(player, old);
                stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
                level.playSound(null, pos, SoundEvents.SHEEP_SHEAR, SoundSource.PLAYERS, 1.0F, 1.0F);
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }

        if (hit.getDirection() != Direction.UP) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if (!(level.getBlockEntity(pos) instanceof TableEntity table)) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        Direction side = placementSide(player, pos);
        if (player.isShiftKeyDown()) {
            if (!level.isClientSide) {
                if (table.placeStack(stack, 1, !player.hasInfiniteMaterials(), side, player.getYRot())) {
                    level.playSound(null, pos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 0.8F, 1.0F);
                }
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }

        if (!level.isClientSide) {
            if (table.placeStack(stack, stack.getCount(), !player.hasInfiniteMaterials(), side, player.getYRot())) {
                level.playSound(null, pos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 0.8F, 1.0F);
            }
        }
        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }
    @Override public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (hit.getDirection() != Direction.UP) return InteractionResult.PASS;
        if (!(level.getBlockEntity(pos) instanceof TableEntity table)) return InteractionResult.PASS;
        int target = table.findItemAt(hit);
        if (target < 0) return InteractionResult.PASS;
        boolean shift = player.isShiftKeyDown();
        return take(level, pos, player, target, shift ? 1 : Integer.MAX_VALUE) ? InteractionResult.sidedSuccess(level.isClientSide) : InteractionResult.PASS;
    }

    private static boolean take(Level level, BlockPos pos, Player player, int entry, int amount) {
        if (!(level.getBlockEntity(pos) instanceof TableEntity table)) return false;
        if (entry < 0 || entry >= table.getEntryCount()) return false;
        if (level.isClientSide) return true;
        List<ItemStack> taken = table.takeEntry(entry, amount);
        if (taken.isEmpty()) return false;
        for (ItemStack stack : taken) giveOrDrop(player, stack);
        level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.5F, 1.0F);
        return true;
    }

    private static Direction placementSide(Player player, BlockPos pos) {
        double dx = player.getX() - (pos.getX() + 0.5D);
        double dz = player.getZ() - (pos.getZ() + 0.5D);
        if (Math.abs(dx) < 0.001D && Math.abs(dz) < 0.001D) return player.getDirection().getOpposite();
        return Direction.getNearest(dx, 0.0D, dz);
    }

    private static void giveOrDrop(Player player, ItemStack stack) { if (!player.getInventory().add(stack)) player.drop(stack, false); }

    @Override public List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        List<ItemStack> drops = new ArrayList<>(super.getDrops(state, params));
        BlockEntity blockEntity = params.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (blockEntity instanceof TableEntity table) drops.addAll(table.getDrops());
        return drops;
    }
}
