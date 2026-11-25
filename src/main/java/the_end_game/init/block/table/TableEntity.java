package the_end_game.init.block.table;

import gcapi_model_animation.init.basic.GCmaBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TableEntity extends GCmaBlockEntity {
    public static final int SLOTS = 6;
    public static final float[] POS_ITEM_X = {-0.28F, 0.0F, 0.28F, -0.28F, 0.0F, 0.28F};
    public static final float[] POS_ITEM_Z = {-0.2F, -0.2F, -0.2F, 0.2F, 0.2F, 0.2F};
    public static final double HIT_RADIUS_SQ = 0.0225D;

    private static final int KIND_NORMAL = 0;
    private static final int KIND_TOOL = 1;
    private static final int KIND_ARMOR = 2;
    private static final int KIND_BED = 3;
    private static final int KIND_SHIELD = 4;

    private final List<Entry> entries = new ArrayList<>();
    private float layoutYaw;
    @Nullable private DyeColor clothColor;

    public TableEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) { super(type, pos, state); }

    public int getEntryCount() { return this.entries.size(); }
    public ItemStack getEntryStack(int index) { return this.entries.get(index).stack; }
    public boolean isEntryLying(int index) { return this.entries.get(index).lying; }
    public boolean isEntryTyped(int index) { return this.entries.get(index).kind != KIND_NORMAL; }
    public boolean isEntryBed(int index) { return this.entries.get(index).kind == KIND_BED; }
    public float getEntryYaw(int index) { return this.entries.get(index).yaw; }
    public float getLayoutYaw() {
        BlockState state = getBlockState();
        return state.hasProperty(Table.ROTATION) ? state.getValue(Table.ROTATION) * 45.0F : 0.0F;
    }
    public float getEntryScale(int index) { return this.entries.get(index).scale(); }
    public float getVisualX(int index) { return 0.5F + rotatedX(visualOffsetX(index), visualOffsetZ(index)); }
    public float getVisualZ(int index) { return 0.5F + rotatedZ(visualOffsetX(index), visualOffsetZ(index)); }

    public boolean hasItems() { return !this.entries.isEmpty(); }
    public boolean hasCloth() { return this.clothColor != null; }
    @Nullable public DyeColor getClothColor() { return this.clothColor; }

    public ItemStack setCloth(DyeColor color) {
        ItemStack old = this.clothColor == null ? ItemStack.EMPTY : new ItemStack(carpetItem(this.clothColor));
        this.clothColor = color;
        sync();
        return old;
    }

    public ItemStack removeCloth() {
        if (this.clothColor == null) return ItemStack.EMPTY;
        ItemStack old = new ItemStack(carpetItem(this.clothColor));
        this.clothColor = null;
        sync();
        return old;
    }

    public boolean placeStack(ItemStack stack, int amount, boolean consume, Direction side, float viewYaw) {
        if (stack.isEmpty() || amount <= 0) return false;
        int kind = kindOf(stack);
        if (kind == KIND_BED) return placeBeds(stack, amount, consume, viewYaw);
        if (kind == KIND_SHIELD) return placeShields(stack, amount, consume, viewYaw);
        if (kind == KIND_TOOL) return placeTools(stack, amount, consume, side, viewYaw);
        if (kind == KIND_ARMOR) return placeArmor(stack, amount, consume, side, viewYaw);
        return placeNormal(stack, amount, consume, side, viewYaw);
    }

    public List<ItemStack> takeEntry(int index, int amount) {
        if (index < 0 || index >= this.entries.size()) return List.of();
        Entry entry = this.entries.get(index);
        if (entry.kind != KIND_NORMAL) {
            this.entries.remove(index);
            sync();
            return List.of(entry.stack.copyWithCount(1));
        }
        int moved = Math.min(amount, Math.min(entry.count, entry.stack.getMaxStackSize()));
        if (moved <= 0) return List.of();
        List<ItemStack> result = split(entry.stack, moved);
        entry.count -= moved;
        if (entry.count <= 0) {
            this.entries.remove(index);
        } else {
            entry.slots = Math.max(1, Math.min(entry.slots, ceilDiv(entry.count, entry.stack.getMaxStackSize())));
        }
        sync();
        return result;
    }

    public int findItemAt(BlockHitResult hit) {
        if (hit.getDirection() != Direction.UP || this.entries.isEmpty()) return -1;
        double x = hit.getLocation().x - this.worldPosition.getX();
        double z = hit.getLocation().z - this.worldPosition.getZ();
        int best = -1;
        double bestDistance = Double.MAX_VALUE;
        for (int i = 0; i < this.entries.size(); i++) {
            double dx = x - getVisualX(i);
            double dz = z - getVisualZ(i);
            double distance = dx * dx + dz * dz;
            double radius = this.entries.get(i).scale() * 0.5D + 0.05D;
            if (distance <= radius * radius && distance < bestDistance) {
                bestDistance = distance;
                best = i;
            }
        }
        return best;
    }

    public List<ItemStack> getDrops() {
        List<ItemStack> drops = new ArrayList<>();
        if (this.clothColor != null) drops.add(new ItemStack(carpetItem(this.clothColor)));
        for (Entry entry : this.entries) {
            if (entry.kind == KIND_NORMAL) drops.addAll(split(entry.stack, entry.count));
            else drops.add(entry.stack.copyWithCount(1));
        }
        return drops;
    }

    private boolean placeBeds(ItemStack stack, int amount, boolean consume, float viewYaw) {
        int wanted = Math.min(amount, stack.getCount());
        int canPlace = Math.min(wanted, freeSlots() / 3);
        if (canPlace <= 0) return false;
        float yaw = 180.0F - snapYaw(viewYaw);
        for (int i = 0; i < canPlace; i++) {
            this.entries.add(new Entry(stack.copyWithCount(1), 1, 3, true, yaw, KIND_BED));
        }
        if (consume) stack.shrink(canPlace);
        sync();
        return true;
    }

    private boolean placeShields(ItemStack stack, int amount, boolean consume, float viewYaw) {
        int wanted = Math.min(amount, stack.getCount());
        int canPlace = Math.min(wanted, freeSlots() / 3);
        if (canPlace <= 0) return false;
        float yaw = 180.0F - snapYaw(viewYaw);
        for (int i = 0; i < canPlace; i++) {
            this.entries.add(new Entry(stack.copyWithCount(1), 1, 3, true, yaw, KIND_SHIELD));
        }
        if (consume) stack.shrink(canPlace);
        sync();
        return true;
    }

    private boolean placeTools(ItemStack stack, int amount, boolean consume, Direction side, float viewYaw) {
        int wanted = Math.min(amount, stack.getCount());
        int canPlace = Math.min(wanted, freeSlots() / 2);
        if (canPlace <= 0) return false;
        float yaw = 180.0F - snapYaw(viewYaw);
        for (int i = 0; i < canPlace; i++) {
            this.entries.add(new Entry(stack.copyWithCount(1), 1, 2, true, yaw, KIND_TOOL));
        }
        if (consume) stack.shrink(canPlace);
        sync();
        return true;
    }

    private boolean placeArmor(ItemStack stack, int amount, boolean consume, Direction side, float viewYaw) {
        if (amount <= 0 || freeSlots() < 4) return false;
        this.entries.add(new Entry(stack.copyWithCount(1), 1, 4, true, 180.0F - snapYaw(viewYaw), KIND_ARMOR));
        if (consume) stack.shrink(1);
        sync();
        return true;
    }

    private boolean placeNormal(ItemStack stack, int amount, boolean consume, Direction side, float viewYaw) {
        int max = stack.getMaxStackSize();
        int requested = Math.min(amount, stack.getCount());
        if (requested <= 0) return false;

        int same = findSameNormal(stack);
        if (same >= 0) {
            Entry entry = this.entries.get(same);
            int free = freeSlots();
            int capacity = (entry.slots + free) * max;
            int moved = Math.min(requested, Math.max(0, capacity - entry.count));
            if (moved <= 0) return false;
            entry.count += moved;
            entry.slots = Math.max(1, Math.min(SLOTS, ceilDiv(entry.count, max)));
            if (consume) stack.shrink(moved);
            sync();
            return true;
        }

        int free = freeSlots();
        if (free <= 0) return false;
        int slots = Math.min(free, ceilDiv(requested, max));
        int count = Math.min(requested, slots * max);
        if (count <= 0) return false;
        this.entries.add(new Entry(stack.copyWithCount(1), count, Math.max(1, ceilDiv(count, max)), true, 180.0F - snapYaw(viewYaw), KIND_NORMAL));
        if (consume) stack.shrink(count);
        sync();
        return true;
    }

    private int findSameNormal(ItemStack stack) {
        for (int i = 0; i < this.entries.size(); i++) {
            Entry entry = this.entries.get(i);
            if (entry.kind == KIND_NORMAL && ItemStack.isSameItemSameComponents(entry.stack, stack)) return i;
        }
        return -1;
    }

    private int freeSlots() {
        int used = 0;
        for (Entry entry : this.entries) used += entry.slots;
        return SLOTS - used;
    }

    private static int ceilDiv(int value, int divisor) { return (value + divisor - 1) / divisor; }

    private static List<ItemStack> split(ItemStack prototype, int amount) {
        List<ItemStack> result = new ArrayList<>();
        int max = prototype.getMaxStackSize();
        if (max <= 0) return result;
        int remaining = amount;
        while (remaining > 0) {
            int count = Math.min(max, remaining);
            result.add(prototype.copyWithCount(count));
            remaining -= count;
        }
        return result;
    }

    private static int kindOf(ItemStack stack) {
        if (stack.getItem() instanceof ShieldItem) return KIND_SHIELD;
        if (stack.getItem() instanceof BedItem) return KIND_BED;
        if (isArmor(stack)) return KIND_ARMOR;
        if (isToolOrWeapon(stack)) return KIND_TOOL;
        return KIND_NORMAL;
    }

    private static boolean isArmor(ItemStack stack) {
        return stack.is(ItemTags.HEAD_ARMOR) || stack.is(ItemTags.CHEST_ARMOR) || stack.is(ItemTags.LEG_ARMOR) || stack.is(ItemTags.FOOT_ARMOR)
                || stack.is(Items.ELYTRA) || stack.getItem() instanceof ArmorItem || stack.getItem() instanceof ElytraItem;
    }

    private static boolean isToolOrWeapon(ItemStack stack) {
        return stack.is(ItemTags.SWORDS) || stack.is(ItemTags.AXES) || stack.is(ItemTags.PICKAXES) || stack.is(ItemTags.SHOVELS) || stack.is(ItemTags.HOES)
                || stack.is(ItemTags.BOW_ENCHANTABLE) || stack.is(ItemTags.CROSSBOW_ENCHANTABLE) || stack.is(ItemTags.TRIDENT_ENCHANTABLE)
                || stack.is(Items.SHEARS) || stack.is(Items.MACE) || stack.is(Items.BRUSH) || stack.is(Items.FLINT_AND_STEEL) || stack.is(Items.SPYGLASS)
                || stack.is(Items.FISHING_ROD) || stack.is(Items.CARROT_ON_A_STICK) || stack.is(Items.WARPED_FUNGUS_ON_A_STICK)
                || stack.getItem() instanceof BowItem || stack.getItem() instanceof CrossbowItem || stack.getItem() instanceof TridentItem
                || stack.getItem() instanceof ShieldItem || stack.getItem() instanceof MaceItem || stack.getItem() instanceof BrushItem
                || stack.getItem() instanceof FlintAndSteelItem || stack.getItem() instanceof SpyglassItem
                || stack.getItem() instanceof FishingRodItem || stack.getItem() instanceof FoodOnAStickItem;

    }

    private static float yawForSide(Direction side) { return (float) Math.toDegrees(Math.atan2(side.getStepX(), side.getStepZ())); }

    private static float snapYaw(float yaw) {
        float snapped = Math.round(yaw / 45.0F) * 45.0F;
        snapped = ((snapped % 360.0F) + 360.0F) % 360.0F;
        if (snapped >= 180.0F) snapped -= 360.0F;
        return snapped;
    }

    private float visualOffsetX(int index) {
        int count = this.entries.size();
        if (count <= 1) return 0.0F;
        if (count == 2) return index == 0 ? -0.25F : 0.25F;
        if (count == 3) {
            if (allTridents()) return (index - 1) * 0.3F;
            if (hasTyped()) return index == 0 ? 0.0F : (index == 1 ? -0.25F : 0.25F);
            return (index - 1) * 0.3F;
        }
        if (count == 4) return index % 2 == 0 ? -0.25F : 0.25F;
        if (count == 5) {
            if (hasTyped()) {
                int typed = typedIndex();
                if (index == typed) return 0.0F;
                int slot = index < typed ? index : index - 1;
                return slot == 0 || slot == 2 ? -0.4F : 0.4F;
            }
            return index < 3 ? (index - 1) * 0.3F : (index == 3 ? -0.15F : 0.15F);
        }
        return (index % 3 - 1) * 0.3F;
    }

    private float visualOffsetZ(int index) {
        int count = this.entries.size();
        if (count <= 2) return 0.0F;
        if (count == 3) {
            if (allTridents()) return 0.0F;
            if (hasTyped()) return index == 0 ? -0.25F : 0.25F;
            return 0.0F;
        }
        if (count == 4) return index < 2 ? -0.25F : 0.25F;
        if (count == 5) {
            if (hasTyped()) {
                int typed = typedIndex();
                if (index == typed) return 0.0F;
                int slot = index < typed ? index : index - 1;
                return slot < 2 ? -0.4F : 0.4F;
            }
            return index < 3 ? -0.18F : 0.18F;
        }
        return index < 3 ? -0.2F : 0.2F;
    }

    private boolean hasTyped() {
        for (Entry entry : this.entries) {
            if (entry.kind != KIND_NORMAL) return true;
        }
        return false;
    }

    private boolean allTridents() {
        if (this.entries.size() != 3) return false;
        for (Entry entry : this.entries) {
            if (!(entry.stack.getItem() instanceof TridentItem)) return false;
        }
        return true;
    }

    private int typedIndex() {
        for (int i = 0; i < this.entries.size(); i++) {
            if (this.entries.get(i).kind != KIND_NORMAL) return i;
        }
        return -1;
    }

    private float rotatedX(float x, float z) {
        float radians = (float) Math.toRadians(getLayoutYaw());
        return x * (float) Math.cos(radians) + z * (float) Math.sin(radians);
    }

    private float rotatedZ(float x, float z) {
        float radians = (float) Math.toRadians(getLayoutYaw());
        return -x * (float) Math.sin(radians) + z * (float) Math.cos(radians);
    }

    private static Item carpetItem(DyeColor color) { return BuiltInRegistries.ITEM.get(ResourceLocation.withDefaultNamespace(color.getName() + "_carpet")); }

    private void sync() {
        setChanged();
        if (this.level != null && !this.level.isClientSide) this.level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
    }

    @Override protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("Cloth", this.clothColor == null ? -1 : this.clothColor.getId());
        tag.putFloat("LayoutYaw", this.layoutYaw);
        ListTag list = new ListTag();
        for (Entry entry : this.entries) {
            CompoundTag data = new CompoundTag();
            data.put("Item", entry.stack.save(registries, new CompoundTag()));
            data.putInt("Count", entry.count);
            data.putFloat("Yaw", entry.yaw);
            data.putInt("Kind", entry.kind);
            list.add(data);
        }
        tag.put("Entries", list);
    }

    @Override protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        int cloth = tag.getInt("Cloth");
        this.clothColor = tag.contains("Cloth") && cloth >= 0 && cloth < 16 ? DyeColor.byId(cloth) : null;
        this.layoutYaw = tag.getFloat("LayoutYaw");
        this.entries.clear();
        if (tag.contains("Entries", Tag.TAG_LIST)) {
            loadEntries(tag.getList("Entries", Tag.TAG_COMPOUND), registries);
        } else if (tag.contains("Items", Tag.TAG_LIST)) {
            loadLegacyItems(tag, registries);
        }
    }

    private void loadEntries(ListTag list, HolderLookup.Provider registries) {
        for (int i = 0; i < list.size(); i++) {
            CompoundTag data = list.getCompound(i);
            if (!data.contains("Item")) continue;
            ItemStack stack = ItemStack.parse(registries, data.get("Item")).orElse(ItemStack.EMPTY);
            if (stack.isEmpty()) continue;
            stack = stack.copyWithCount(1);
            int kind = data.getInt("Kind");
            if (kind != KIND_TOOL && kind != KIND_ARMOR && kind != KIND_BED && kind != KIND_SHIELD) kind = kindOf(stack);
            int count = data.getInt("Count");
            int slots;
            float yaw = data.getFloat("Yaw");
            if (kind == KIND_NORMAL) {
                int max = stack.getMaxStackSize();
                count = Math.max(1, count);
                slots = Math.max(1, Math.min(SLOTS, ceilDiv(count, max)));
                count = Math.min(count, slots * max);
            } else {
                slots = kind == KIND_ARMOR ? 4 : (kind == KIND_BED || kind == KIND_SHIELD) ? 3 : 2;
                count = 1;
            }
            if (freeSlots() < slots) continue;
            this.entries.add(new Entry(stack, count, slots, true, yaw, kind));
        }
    }

    private void loadLegacyItems(CompoundTag tag, HolderLookup.Provider registries) {
        NonNullList<ItemStack> old = NonNullList.withSize(SLOTS, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, old, registries);
        for (ItemStack stack : old) {
            if (stack.isEmpty()) continue;
            int count = stack.getCount();
            int slots = Math.min(freeSlots(), Math.max(1, ceilDiv(count, stack.getMaxStackSize())));
            if (slots <= 0) continue;
            count = Math.min(count, slots * stack.getMaxStackSize());
            this.entries.add(new Entry(stack.copyWithCount(1), count, slots, true, 0.0F, KIND_NORMAL));
        }
    }

    @Override public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, registries);
        return tag;
    }

    @Nullable @Override public Packet<ClientGamePacketListener> getUpdatePacket() { return ClientboundBlockEntityDataPacket.create(this); }

    private static final class Entry {
        private final ItemStack stack;
        private int count;
        private int slots;
        private final boolean lying;
        private final float yaw;
        private final int kind;

        private Entry(ItemStack stack, int count, int slots, boolean lying, float yaw, int kind) {
            this.stack = stack;
            this.count = count;
            this.slots = slots;
            this.lying = lying;
            this.yaw = yaw;
            this.kind = kind;
        }

        private float scale() {
            if (this.kind == KIND_BED) return 0.3F;
            return this.kind == KIND_NORMAL ? 0.3F * (1.0F + Math.min(0.15F, 0.05F * (this.slots - 1))) : 0.5F;
        }
    }
}
