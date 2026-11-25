package the_end_game.init.item.etericon.client.scene.extra;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import the_end_game.init.item.etericon.book.BookElement;
import the_end_game.init.item.etericon.book.BookPage;
import the_end_game.init.item.etericon.client.ClientEtericonData;
import the_end_game.init.item.etericon.client.EtericonAtlas;
import the_end_game.init.item.etericon.client.EtericonLayout;
import the_end_game.init.item.etericon.client.EtericonScreen;

import java.util.List;

public final class ExtraScene {
    private final EtericonScreen screen;
    private EtericonScreen.Mode returnMode = EtericonScreen.Mode.BOOK;
    private ResourceLocation pageId;

    public ExtraScene(EtericonScreen screen) {
        this.screen = screen;
    }

    public void open(ResourceLocation id, EtericonScreen.Mode currentMode) {
        if (currentMode != EtericonScreen.Mode.EXTRA) {
            this.returnMode = currentMode == EtericonScreen.Mode.MAP ? EtericonScreen.Mode.MAP : EtericonScreen.Mode.BOOK;
        }
        this.pageId = id;
    }

    public void setPage(ResourceLocation id) { this.pageId = id; }
    public ResourceLocation pageId() { return this.pageId; }
    public EtericonScreen.Mode returnMode() { return this.returnMode; }

    public void clear() {
        this.pageId = null;
        this.returnMode = EtericonScreen.Mode.BOOK;
    }

    public void drawBase(GuiGraphics graphics) {
        this.screen.setOverlayBase(true);
        if (this.returnMode == EtericonScreen.Mode.BOOK) { this.screen.drawBook(graphics); } else { this.screen.drawMap(graphics); }
        this.screen.setOverlayBase(false);
        this.screen.clearInteraction();
    }

    public void drawPage(GuiGraphics graphics) {
        graphics.pose().pushPose();
        graphics.pose().translate(0.0F, 0.0F, 500.0F);
        int bookX = /*this.returnMode == EtericonScreen.Mode.BOOK ? 0 : this.screen.mapPaperX()*/ 100;
        int pageWidth = EtericonLayout.SPECIAL_PAGE_WIDTH;
        int pageHeight = EtericonLayout.SPECIAL_PAGE_HEIGHT;
        int pageX = bookX + (EtericonScreen.MAP_PAPER_WIDTH - pageWidth) / 2;
        int pageY = EtericonLayout.SPECIAL_PAGE_Y;
        EtericonAtlas.drawSpecialPage(graphics, pageX, pageY, pageWidth, pageHeight);
        this.drawContent(graphics, pageX, pageY, pageWidth, pageHeight);
        this.screen.drawBackButton(graphics, pageX + EtericonLayout.SPECIAL_PAGE_BACK_INSET + 2, pageY + EtericonLayout.SPECIAL_PAGE_BACK_INSET + 1, this.screen::closeExtra);
        graphics.pose().popPose();
    }

    private void drawContent(GuiGraphics graphics, int pageX, int pageY, int pageWidth, int pageHeight) {
        this.screen.drawScaledText(graphics, Component.translatable("gui.the_end_game.etericon.title"), pageX + 10, pageY + 18, 0.82F, 0xFF2A1A0E, false);
        int points = ClientEtericonData.research();
        this.screen.drawScaledText(graphics, Component.translatable("gui.the_end_game.etericon.points", points), pageX + 10, pageY + 32, 0.5F, 0xFF5A3A1A, false);
        this.screen.drawScaledText(graphics, Component.translatable("gui.the_end_game.etericon.special.hint1"), pageX + 10, pageY + 42, 0.46F, 0xFF6A4A2A, false);
        this.screen.drawScaledText(graphics, Component.translatable("gui.the_end_game.etericon.special.hint2"), pageX + 10, pageY + 50, 0.46F, 0xFF6A4A2A, false);
        this.screen.drawStack(graphics, new ItemStack(the_end_game.init.InitItem.ETERICON.get()), pageX + 10, pageY + 60, 18);
        this.screen.drawScaledText(graphics, Component.translatable("gui.the_end_game.etericon.special.search"), pageX + 34, pageY + 62, 0.46F, 0xFF6A4A2A, false);
        BookPage page = this.pageId == null ? null : ClientEtericonData.catalog().page(this.pageId);
        if (page == null) {
            this.screen.drawScaledText(graphics, Component.translatable("gui.the_end_game.etericon.special.text"), pageX + 10, pageY + 82, 0.5F, 0xFF3B2A1A, false);
            return;
        }
        int contentY = pageY + 78;
        int contentHeight = pageHeight - 88;
        boolean twoColumns = !page.left().isEmpty() && !page.right().isEmpty();
        if (twoColumns) {
            int columnWidth = (pageWidth - 26) / 2;
            this.drawColumn(graphics, page.left(), pageX + 10, contentY, columnWidth, contentHeight);
            this.drawColumn(graphics, page.right(), pageX + 16 + columnWidth, contentY, columnWidth, contentHeight);
        } else if (!page.left().isEmpty()) {
            this.drawColumn(graphics, page.left(), pageX + 10, contentY, pageWidth - 20, contentHeight);
        } else {
            this.drawColumn(graphics, page.right(), pageX + 10, contentY, pageWidth - 20, contentHeight);
        }
    }

    private void drawColumn(GuiGraphics graphics, List<BookElement> elements, int x, int y, int width, int height) {
        if (elements.isEmpty()) { return; }
        this.screen.enableScissor(graphics, x, y, width, height);
        int cursor = 0;
        for (BookElement element : elements) {
            int elementX = element.x() == null ? 0 : element.x();
            int elementY = element.y() == null ? cursor : element.y();
            int availableWidth = Math.max(1, width - elementX);
            int elementHeight = this.screen.drawContent(graphics, element.content(), x + elementX, y + elementY, availableWidth, element.width(), element.height());
            cursor = Math.max(cursor, elementY + elementHeight + element.gap());
        }
        this.screen.disableScissor(graphics);
    }
}
