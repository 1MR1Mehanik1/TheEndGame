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

    private float fitScale = 1.0F;
    private int fitX;
    private int fitY;
    private boolean backgroundDrawn;

    public TableTheoriesScreen(TableTheoriesMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 209;
        this.imageHeight = INVENTORY_Y + INVENTORY_HEIGHT;
    }

    @Override protected void init() {
        super.init();
        float scaleX = (float)(this.width - 8) / this.imageWidth;
        float scaleY = (float)(this.height - 8) / this.imageHeight;
        this.fitScale = Math.min(1.0F, Math.min(scaleX, scaleY));
        this.fitX = Math.round((this.width - this.imageWidth * this.fitScale) / 2.0F);
        this.fitY = Math.round((this.height - this.imageHeight * this.fitScale) / 2.0F);
        this.leftPos = 0;
        this.topPos = 0;
    }

    @Override public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        int guiMouseX = this.toGuiX(mouseX);
        int guiMouseY = this.toGuiY(mouseY);
        this.renderTransparentBackground(graphics);
        this.backgroundDrawn = true;
        graphics.pose().pushPose();
        graphics.pose().translate(this.fitX, this.fitY, 0);
        graphics.pose().scale(this.fitScale, this.fitScale, 1);
        super.render(graphics, mouseX, mouseY, partialTick);
        graphics.pose().popPose();
        this.backgroundDrawn = false;
        this.renderTooltip(graphics, mouseX, mouseY);
        this.renderGhostTooltip(graphics, guiMouseX, guiMouseY, mouseX, mouseY);
    }

    @Override public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (this.backgroundDrawn) {
            this.renderBg(graphics, partialTick, mouseX, mouseY);
            return;
        }
        super.renderBackground(graphics, mouseX, mouseY, partialTick);
    }

    private int toGuiX(double screenX) {
        return (int)Math.floor((screenX - this.fitX) / this.fitScale);
    }

    private int toGuiY(double screenY) {
        return (int)Math.floor((screenY - this.fitY) / this.fitScale);
    }

    @Override protected boolean isHovering(int x, int y, int width, int height, double mouseX, double mouseY) {
        return this.isGuiHovering(x, y, width, height, this.toGuiX(mouseX), this.toGuiY(mouseY));
    }

    private boolean isGuiHovering(int x, int y, int width, int height, int mouseX, int mouseY) {
        return mouseX >= x - 1 && mouseX < x + width + 1 && mouseY >= y - 1 && mouseY < y + height + 1;
    }

    @Override public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return super.mouseClicked(this.toGuiX(mouseX), this.toGuiY(mouseY), button);
    }

    @Override public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return super.mouseReleased(this.toGuiX(mouseX), this.toGuiY(mouseY), button);
    }

    @Override public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        return super.mouseDragged(this.toGuiX(mouseX), this.toGuiY(mouseY), button, dragX / this.fitScale, dragY / this.fitScale);
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

    private void renderGhostTooltip(GuiGraphics graphics, int guiMouseX, int guiMouseY, int mouseX, int mouseY) {
        if (!this.menu.getCarried().isEmpty()) return;
        for (int slot = 0; slot < 3; slot++) {
            if (!this.menu.slots.get(slot).getItem().isEmpty()) continue;
            if (!this.isGuiHovering(this.menu.slots.get(slot).x, this.menu.slots.get(slot).y, 16, 16, guiMouseX, guiMouseY)) continue;
            ItemStack stack = switch (slot) {
                case 0 -> new ItemStack(Items.PAPER);
                case 1 -> new ItemStack(InitItem.INKWELL.get());
                default -> new ItemStack(InitItem.WRITING_FEATHER.get());
            };
            graphics.renderTooltip(this.font, stack, mouseX, mouseY);
            return;
        }
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
