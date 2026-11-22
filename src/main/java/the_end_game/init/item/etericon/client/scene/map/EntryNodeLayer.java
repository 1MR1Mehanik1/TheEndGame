package the_end_game.init.item.etericon.client.scene.map;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import the_end_game.init.item.etericon.book.BookEntry;
import the_end_game.init.item.etericon.book.BookProgress;
import the_end_game.init.item.etericon.book.EntryState;
import the_end_game.init.item.etericon.client.EtericonAtlas;
import the_end_game.init.item.etericon.client.EtericonLayout;
import the_end_game.init.item.etericon.client.EtericonScreen;

import java.util.List;
import java.util.function.Predicate;

public final class EntryNodeLayer {
    private final EtericonScreen screen;
    private final MapScene map;

    public EntryNodeLayer(EtericonScreen screen, MapScene map) {
        this.screen = screen;
        this.map = map;
    }

    public void draw(GuiGraphics graphics, List<BookEntry> entries, BookProgress progress, boolean searchActive, Predicate<BookEntry> searchMatch) {
        int viewX = this.map.mapViewX();
        int viewY = this.map.mapViewY();
        int viewWidth = this.map.mapViewWidth();
        int viewHeight = this.map.mapViewHeight();
        int contentBottom = viewY + viewHeight;
        boolean mouseInMap = !this.screen.overlayBase() && !this.map.overlayHovered()
                && this.screen.isInside(this.screen.sceneMouseX(), this.screen.sceneMouseY(), viewX, viewY, viewWidth, viewHeight);
        for (BookEntry entry : entries) {
            int centerX = (int) this.map.mapX(entry.x());
            int centerY = (int) this.map.mapY(entry.y());
            int left = centerX - 12;
            int top = centerY - 12;
            int iconWidth = Math.min(entry.icon().width(), EtericonLayout.NODE_ICON_MAX);
            int iconHeight = Math.min(entry.icon().height(), EtericonLayout.NODE_ICON_MAX);
            boolean mouseInNode = mouseInMap && this.screen.isInside(this.screen.sceneMouseX(), this.screen.sceneMouseY(), left, top, 24, 24);
            EntryState state = progress.stateOf(entry);
            boolean grow = mouseInNode && (state == EntryState.AVAILABLE || state == EntryState.OPENED);
            boolean matches = !searchActive || searchMatch.test(entry);
            if (mouseInNode) { this.screen.setHoveredEntry(entry); }
            int drawLeft = left;
            int drawTop = top;
            int drawSize = 24;
            if (grow) {
                drawLeft = left - 1;
                drawTop = top - 1;
                drawSize = 26;
            }
            int iconLeft = drawLeft + (drawSize - iconWidth) / 2;
            int iconTop = drawTop + (drawSize - iconHeight) / 2;
            boolean dimNode = state == EntryState.LOCKED || !matches;
            if (dimNode) { RenderSystem.setShaderColor(0.35F, 0.35F, 0.35F, 1.0F); }
            EtericonAtlas.drawNode(graphics, entry.shape(), state, drawLeft, drawTop, drawSize, drawSize);
            EtericonAtlas.drawIcon(graphics, entry.icon(), iconLeft, iconTop, iconWidth, iconHeight);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            if (!this.screen.overlayBase() && centerY <= contentBottom && this.screen.isInside(centerX, centerY, viewX, viewY, viewWidth, viewHeight)) {
                this.screen.addClickable(left, top, 24, 24, () -> this.screen.requestEntry(entry.id()));
            }
        }
    }
}
