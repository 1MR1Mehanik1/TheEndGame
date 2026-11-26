package the_end_game.init.block.table_theories;

import gcapi_model_animation.init.basic.GCmaBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class TableTheoriesEntity extends GCmaBlockEntity implements Container, MenuProvider {
    public static final int SLOTS = 12;

    private final NonNullList<ItemStack> items = NonNullList.withSize(SLOTS, ItemStack.EMPTY);
    private float inkwellX = 9.0F;
    private float inkwellZ = 3.0F;
    private float inkwellYaw;
    private float featherX = 8.5F;
    private float featherZ = 3.25F;
    private float featherYaw;
    private float featherInkwellYaw;

    public TableTheoriesEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) { super(type, pos, state); }

    public float inkwellX() { return this.inkwellX; }
    public float inkwellZ() { return this.inkwellZ; }
    public float inkwellYaw() { return this.inkwellYaw; }
    public float featherX() { return this.featherX; }
    public float featherZ() { return this.featherZ; }
    public float featherYaw() { return this.featherYaw; }
    public float featherInkwellYaw() { return this.featherInkwellYaw; }

    @Override public void onLoad() {
        super.onLoad();
        Level level = this.getLevel();
        if (level == null || level.isClientSide) return;
        TableTheories.ensureExtension(level, this.worldPosition, this.getBlockState());
    }

    @Override public void tick(Level level) {
        super.tick(level);
        if (level.isClientSide || level.getGameTime() % 20L != 0L) return;
        TableTheories.ensureExtension(level, this.worldPosition, this.getBlockState());
    }

    public NonNullList<ItemStack> getItems() { return this.items; }

    @Override public int getContainerSize() { return SLOTS; }

    @Override public boolean isEmpty() {
        for (ItemStack stack : this.items) if (!stack.isEmpty()) return false;
        return true;
    }

    @Override public ItemStack getItem(int slot) { return this.items.get(slot); }

    @Override public ItemStack removeItem(int slot, int amount) {
        ItemStack stack = this.items.get(slot);
        if (stack.isEmpty() || amount <= 0) return ItemStack.EMPTY;
        ItemStack removed = stack.split(amount);
        if (stack.isEmpty()) this.items.set(slot, ItemStack.EMPTY);
        this.setChanged();
        this.syncContents();
        return removed;
    }

    @Override public ItemStack removeItemNoUpdate(int slot) {
        ItemStack stack = this.items.get(slot);
        this.items.set(slot, ItemStack.EMPTY);
        this.setChanged();
        this.syncContents();
        return stack;
    }

    @Override public void setItem(int slot, ItemStack stack) {
        ItemStack previous = this.items.get(slot);
        this.items.set(slot, stack);
        if (previous.isEmpty() && !stack.isEmpty() && this.level != null && !this.level.isClientSide) {
            if (slot == 1) this.rollInkwell(this.level.random);
            if (slot == 2) this.rollFeather(this.level.random);
        }
        this.setChanged();
        this.syncContents();
    }

    private void rollInkwell(RandomSource random) {
        this.inkwellX = 8.5F + random.nextFloat() * 3.0F;
        this.inkwellZ = 3.0F - random.nextFloat() * 9.0F;
        this.inkwellYaw = random.nextFloat() * 360.0F;
    }

    private void rollFeather(RandomSource random) {
        this.featherX = 8.5F + random.nextFloat() * 3.0F;
        this.featherZ = 3.0F - random.nextFloat() * 9.0F;
        this.featherYaw = random.nextFloat() * 360.0F;
        this.featherInkwellYaw = random.nextFloat() * 360.0F;
    }

    @Override public boolean stillValid(Player player) {
        if (this.level == null || this.level.getBlockEntity(this.worldPosition) != this) return false;
        return player.distanceToSqr(this.worldPosition.getX() + 0.5D, this.worldPosition.getY() + 0.5D, this.worldPosition.getZ() + 0.5D) <= 64.0D;
    }

    @Override public void clearContent() {
        for (int i = 0; i < SLOTS; i++) this.items.set(i, ItemStack.EMPTY);
        this.setChanged();
        this.syncContents();
    }

    @Override public Component getDisplayName() { return Component.translatable("block.the_end_game.table_theories"); }

    @Override public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new TableTheoriesMenu(containerId, inventory, this);
    }

    @Override public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        ContainerHelper.saveAllItems(tag, this.items, registries);
        this.savePoses(tag);
        return tag;
    }

    private void savePoses(CompoundTag tag) {
        tag.putFloat("inkwell_x", this.inkwellX);
        tag.putFloat("inkwell_z", this.inkwellZ);
        tag.putFloat("inkwell_yaw", this.inkwellYaw);
        tag.putFloat("feather_x", this.featherX);
        tag.putFloat("feather_z", this.featherZ);
        tag.putFloat("feather_yaw", this.featherYaw);
        tag.putFloat("feather_inkwell_yaw", this.featherInkwellYaw);
    }

    private void loadPoses(CompoundTag tag) {
        if (!tag.contains("inkwell_x")) return;
        this.inkwellX = tag.getFloat("inkwell_x");
        this.inkwellZ = tag.getFloat("inkwell_z");
        this.inkwellYaw = tag.getFloat("inkwell_yaw");
        this.featherX = tag.getFloat("feather_x");
        this.featherZ = tag.getFloat("feather_z");
        this.featherYaw = tag.getFloat("feather_yaw");
        this.featherInkwellYaw = tag.getFloat("feather_inkwell_yaw");
        this.featherX = Mth.clamp(Math.abs(this.featherX), 8.5F, 11.5F);
        this.featherZ = Mth.clamp(this.featherZ, -6.0F, 3.0F);
    }

    private void syncContents() {
        if (this.level != null && !this.level.isClientSide) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), Block.UPDATE_CLIENTS);
        }
    }

    @Override protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ContainerHelper.saveAllItems(tag, this.items, registries);
        this.savePoses(tag);
    }

    @Override protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        for (int i = 0; i < SLOTS; i++) this.items.set(i, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, this.items, registries);
        this.loadPoses(tag);
    }
}
