package the_end_game.init.block.table_theories;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import the_end_game.Main;
import the_end_game.init.InitItem;

public class TableTheoriesScreen extends AbstractContainerScreen<TableTheoriesMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Main.MOD_ID, "textures/gui/table_theories_gui.png");
    private static final ResourceLocation INVENTORY = ResourceLocation.withDefaultNamespace("textures/gui/container/inventory.png");

    private static final int TEXTURE_WIDTH = 209;
    private static final int TEXTURE_HEIGHT = 191;

    private static final int PANEL_U = 0;
    private static final int PANEL_V = 0;
    private static final int PANEL_WIDTH = 209;
    private static final int PANEL_HEIGHT = 191;

    private static final int INVENTORY_X = 17;
    private static final int INVENTORY_Y = 191;
    private static final int INVENTORY_U = 0;
    private static final int INVENTORY_V = 83;
    private static final int INVENTORY_WIDTH = 176;
    private static final int INVENTORY_HEIGHT = 83;
    private static final int INVENTORY_TEXTURE_WIDTH = 256;
    private static final int INVENTORY_TEXTURE_HEIGHT = 256;

    public TableTheoriesScreen(TableTheoriesMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 209;
        this.imageHeight = INVENTORY_Y + INVENTORY_HEIGHT;
    }

    @Override protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
    }

    @Override protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.blit(
                TEXTURE,
                this.leftPos,
                this.topPos,
                PANEL_WIDTH,
                PANEL_HEIGHT,
                PANEL_U,
                PANEL_V,
                PANEL_WIDTH,
                PANEL_HEIGHT,
                TEXTURE_WIDTH,
                TEXTURE_HEIGHT
        );
        graphics.blit(
                INVENTORY,
                this.leftPos + INVENTORY_X,
                this.topPos + INVENTORY_Y,
                INVENTORY_WIDTH,
                INVENTORY_HEIGHT,
                INVENTORY_U,
                INVENTORY_V,
                INVENTORY_WIDTH,
                INVENTORY_HEIGHT,
                INVENTORY_TEXTURE_WIDTH,
                INVENTORY_TEXTURE_HEIGHT
        );
        renderGhost(graphics, 0, new ItemStack(Items.PAPER));
        renderGhost(graphics, 1, new ItemStack(InitItem.INKWELL.get()));
        renderGhost(graphics, 2, new ItemStack(InitItem.WRITING_FEATHER.get()));
    }

    private void renderGhost(GuiGraphics graphics, int slot, ItemStack stack) {
        if (!this.menu.slots.get(slot).getItem().isEmpty()) return;
        int x = this.leftPos + this.menu.slots.get(slot).x;
        int y = this.topPos + this.menu.slots.get(slot).y;
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 0.35F);
        graphics.renderItem(stack, x, y);
        graphics.flush();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
