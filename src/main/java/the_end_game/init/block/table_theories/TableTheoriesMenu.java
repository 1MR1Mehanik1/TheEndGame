package the_end_game.init.block.table_theories;

import java.util.function.Predicate;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import the_end_game.init.InitItem;
import the_end_game.init.InitMenu;
import the_end_game.init.item.TheoryItem;

public class TableTheoriesMenu extends AbstractContainerMenu {
    public static final int BLOCK_SLOTS = 12;

    private static final int[] SLOT_X = {5, 26, 46, 5, 128, 148, 168, 187, 128, 148, 168, 187};
    private static final int[] SLOT_Y = {5, 5, 5, 24, 5, 5, 5, 5, 24, 24, 24, 24};

    private final Container container;

    public TableTheoriesMenu(int containerId, Inventory inventory) {
        this(containerId, inventory, new SimpleContainer(BLOCK_SLOTS));
    }

    public TableTheoriesMenu(int containerId, Inventory inventory, Container container) {
        super(InitMenu.TABLE_THEORIES.get(), containerId);
        checkContainerSize(container, BLOCK_SLOTS);
        this.container = container;

        this.addSlot(new FilteredSlot(container, 0, SLOT_X[0], SLOT_Y[0], stack -> stack.is(Items.PAPER)));
        this.addSlot(new FilteredSlot(container, 1, SLOT_X[1], SLOT_Y[1], stack -> stack.is(InitItem.INKWELL.get())));
        this.addSlot(new FilteredSlot(container, 2, SLOT_X[2], SLOT_Y[2], stack -> stack.is(InitItem.WRITING_FEATHER.get())));
        this.addSlot(new FilteredSlot(container, 3, SLOT_X[3], SLOT_Y[3], stack -> stack.getItem() instanceof TheoryItem && stack.has(InitItem.DATA_THEORY_ID.get())));
        for (int i = 4; i < BLOCK_SLOTS; i++) this.addSlot(new Slot(container, i, SLOT_X[i], SLOT_Y[i]));

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(inventory, col + row * 9 + 9, 25 + col * 18, 192 + row * 18));
            }
        }

        for (int col = 0; col < 9; col++) this.addSlot(new Slot(inventory, col, 25 + col * 18, 250));
    }

    @Override public boolean stillValid(Player player) { return this.container.stillValid(player); }

    @Override public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = this.slots.get(index);
        if (slot == null || !slot.hasItem()) return ItemStack.EMPTY;

        ItemStack stack = slot.getItem();
        ItemStack copy = stack.copy();

        if (index < BLOCK_SLOTS) {
            if (!this.moveItemStackTo(stack, BLOCK_SLOTS, this.slots.size(), true)) return ItemStack.EMPTY;
        } else if (!this.moveItemStackTo(stack, 0, BLOCK_SLOTS, false)) {
            return ItemStack.EMPTY;
        }

        if (stack.isEmpty()) slot.setByPlayer(ItemStack.EMPTY);
        else slot.setChanged();

        return copy;
    }

    private static class FilteredSlot extends Slot {
        private final Predicate<ItemStack> filter;

        FilteredSlot(Container container, int slot, int x, int y, Predicate<ItemStack> filter) {
            super(container, slot, x, y);
            this.filter = filter;
        }

        @Override public boolean mayPlace(ItemStack stack) {
            return this.filter.test(stack);
        }
    }
}
