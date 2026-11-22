package the_end_game.init.item.etericon.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import the_end_game.Main;
import the_end_game.init.item.etericon.book.BookChapter;
import the_end_game.init.item.etericon.book.BookIcon;
import the_end_game.init.item.etericon.book.EntryState;
import the_end_game.init.item.etericon.book.UnlockShape;
import the_end_game.init.item.etericon.client.scene.map.BookmarkLayout;

public final class EtericonAtlas {
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Main.MOD_ID, "textures/other/book/etericon_gui.png");
    public static final int TEXTURE_WIDTH = 542;
    public static final int TEXTURE_HEIGHT = 542;
    public static final int BOOK_WIDTH = 272;
    public static final int BOOK_HEIGHT = 180;

    public static final int MAP_V = 181;
    public static final int BOOK_V = 362;

    private static final int[] NODE_X = {275, 320, 365};
    private static final int[] NODE_Y = {1, 46, 91, 136, 181};
    private static final int NODE_WIDTH = 44;
    private static final int NODE_HEIGHT = 44;

    private static final int[] BOOKMARK_X = {319, 319, 319, 319};
    private static final int[] BOOKMARK_Y = {236, 261, 286, 311};
    private static final int BOOKMARK_WIDTH = 79;
    private static final int BOOKMARK_HEIGHT = 24;

    private static final int[] FRAME_X = {290, 290, 290, 290, 290};
    private static final int[] FRAME_Y = {348, 321, 294, 267, 240};
    private static final int FRAME_WIDTH = 26;
    private static final int FRAME_HEIGHT = 26;

    private static final int[] SIDE_X = {275, 275, 275, 275, 275, 319, 365};
    private static final int[] SIDE_Y = {348, 321, 294, 267, 240, 338, 341};
    private static final int[] SIDE_WIDTH = {14, 14, 14, 14, 14, 17, 13};
    private static final int[] SIDE_HEIGHT = {26, 26, 26, 26, 26, 36, 30};

    private static final int LEFT_ARROW_U = 427;
    private static final int RIGHT_ARROW_U = 408;
    private static final int BACK_ARROW_U = 446;
    private static final int ARROW_DARK_V = 502;
    private static final int ARROW_DARK_HEIGHT = 10;
    private static final int ARROW_LIGHT_V = 513;
    private static final int ARROW_LIGHT_HEIGHT = 10;
    private static final int ARROW_WIDTH = 18;

    private static final int EYE_U = 444;
    private static final int EYE_DARK_V = 460;
    private static final int EYE_LIGHT_V = 472;
    private static final int EYE_WIDTH = 11;
    private static final int EYE_HEIGHT = 7;

    public static void drawCoverFrame(GuiGraphics graphics, int x, int y) {
        graphics.blit(TEXTURE, x, y, BOOK_WIDTH, BOOK_HEIGHT, 0.0F, 0.0F, BOOK_WIDTH, BOOK_HEIGHT, TEXTURE_WIDTH, TEXTURE_HEIGHT);
    }

    public static void drawMap(GuiGraphics graphics, int x, int y) {
        graphics.blit(TEXTURE, x, y, BOOK_WIDTH, BOOK_HEIGHT, 0.0F, (float) MAP_V, BOOK_WIDTH, BOOK_HEIGHT, TEXTURE_WIDTH, TEXTURE_HEIGHT);
    }

    public static void drawBook(GuiGraphics graphics) {
        graphics.blit(TEXTURE, 0, 0, BOOK_WIDTH, BOOK_HEIGHT, 0.0F, (float) BOOK_V, BOOK_WIDTH, BOOK_HEIGHT, TEXTURE_WIDTH, TEXTURE_HEIGHT);
    }

    public static void drawPaperPage(GuiGraphics graphics) {
        graphics.blit(TEXTURE, 0, 0, BOOK_WIDTH, BOOK_HEIGHT, 0.0F, (float) MAP_V, BOOK_WIDTH, BOOK_HEIGHT, TEXTURE_WIDTH, TEXTURE_HEIGHT);
    }

    public static void drawSearchBar(GuiGraphics graphics, int x, int y) {
        graphics.blit(TEXTURE, x, y, 102, 16, 406.0F, 526.0F, 102, 16, TEXTURE_WIDTH, TEXTURE_HEIGHT);
    }

    public static void drawSpecialPage(GuiGraphics graphics, int x, int y, int width, int height) {
        graphics.blit(TEXTURE, x, y, width, height, 273.0F, 377.0F, 132, 165, TEXTURE_WIDTH, TEXTURE_HEIGHT);
    }

    public static void drawBookmarkBackground(GuiGraphics graphics, int type, int x, int y, int width, int height) {
        int row = Math.max(0, Math.min(3, type));
        graphics.blit(TEXTURE, x, y, width, height, (float) BOOKMARK_X[row], (float) BOOKMARK_Y[row], BOOKMARK_WIDTH, BOOKMARK_HEIGHT, TEXTURE_WIDTH, TEXTURE_HEIGHT);
    }

    public static void drawAtlasIcon(GuiGraphics graphics, int u, int v, int uW, int vH, int x, int y, int width, int height) {
        graphics.blit(TEXTURE, x, y, width, height, (float) u, (float) v, uW, vH, TEXTURE_WIDTH, TEXTURE_HEIGHT);
    }

    public static void drawNode(GuiGraphics graphics, UnlockShape shape, EntryState state, int x, int y, int width, int height) {
        int column = Math.max(0, Math.min(2, state.ordinal()));
        int row = Math.max(0, Math.min(4, shape.row()));
        graphics.blit(TEXTURE, x, y, width, height, (float) NODE_X[column], (float) NODE_Y[row], NODE_WIDTH, NODE_HEIGHT, TEXTURE_WIDTH, TEXTURE_HEIGHT);
    }

    public static int sideWidth(int type) { return SIDE_WIDTH[Math.max(0, Math.min(6, type))]; }
    public static int sideHeight(int type) { return SIDE_HEIGHT[Math.max(0, Math.min(6, type))]; }

    public static int leftWidth(BookChapter chapter) {
        int raw = chapter.leftImage() == null ? sideWidth(chapter.left()) : 14;
        return Math.max(1, Math.round(raw * BookmarkLayout.DEFAULT.leftScale()));
    }
    public static int leftHeight(BookChapter chapter) {
        int raw = chapter.leftImage() == null ? sideHeight(chapter.left()) : 26;
        return Math.max(1, Math.round(raw * BookmarkLayout.DEFAULT.leftScale()));
    }

    public static int rightWidth(BookChapter chapter) {
        int raw = chapter.rightImage() == null ? sideWidth(chapter.right()) : 14;
        return Math.max(1, Math.round(raw * BookmarkLayout.DEFAULT.rightScale()));
    }
    public static int rightHeight(BookChapter chapter) {
        int raw = chapter.rightImage() == null ? sideHeight(chapter.right()) : 26;
        return Math.max(1, Math.round(raw * BookmarkLayout.DEFAULT.rightScale()));
    }

    public static int bookmarkContentWidth(BookChapter chapter) { return chapter.bookmarkWidth() > 0 ? chapter.bookmarkWidth() : BookmarkLayout.DEFAULT.bookmarkWidth(); }
    public static int bookmarkContentHeight(BookChapter chapter) { return chapter.bookmarkHeight() > 0 ? chapter.bookmarkHeight() : BookmarkLayout.DEFAULT.bookmarkHeight(); }

    public static int frameContentWidth(BookChapter chapter) { return chapter.frameWidth() > 0 ? chapter.frameWidth() : BookmarkLayout.DEFAULT.frameWidth(); }
    public static int frameContentHeight(BookChapter chapter) { return chapter.frameHeight() > 0 ? chapter.frameHeight() : BookmarkLayout.DEFAULT.frameHeight(); }

    public static int normalBookmarkWidth(BookChapter chapter) {
        int leftWidth = leftWidth(chapter);
        int rightWidth = rightWidth(chapter);
        int frameWidth = frameContentWidth(chapter);
        int leftX = BookmarkLayout.DEFAULT.frameX() - leftWidth - BookmarkLayout.DEFAULT.frameGapLeft();
        int rightX = BookmarkLayout.DEFAULT.frameX() + frameWidth + BookmarkLayout.DEFAULT.frameGapRight();
        return Math.max(
                BookmarkLayout.DEFAULT.bookmarkX() + bookmarkContentWidth(chapter),
                Math.max(BookmarkLayout.DEFAULT.frameX() + frameWidth,
                        Math.max(BookmarkLayout.DEFAULT.imageX() + BookmarkLayout.DEFAULT.imageWidth(),
                                Math.max(leftX + leftWidth, rightX + rightWidth)))
        );
    }

    public static int bookmarkWidth(BookChapter chapter) {
        int leftWidth = leftWidth(chapter);
        int rightWidth = rightWidth(chapter);
        int frameWidth = frameContentWidth(chapter);
        int mirrorWidth = normalBookmarkWidth(chapter);
        int rightEdge = mirrorWidth - BookmarkLayout.DEFAULT.frameX() + BookmarkLayout.DEFAULT.frameGapRight() + rightWidth;
        return Math.max(mirrorWidth, rightEdge);
    }
    public static int bookmarkHeight(BookChapter chapter) {
        int frameHeight = frameContentHeight(chapter);
        return Math.max(
                BookmarkLayout.DEFAULT.bookmarkY() + bookmarkContentHeight(chapter),
                Math.max(BookmarkLayout.DEFAULT.frameY() + frameHeight,
                        Math.max(BookmarkLayout.DEFAULT.imageY() + BookmarkLayout.DEFAULT.imageHeight(),
                                BookmarkLayout.DEFAULT.sideOffsetY() + Math.max(leftHeight(chapter), rightHeight(chapter))))
        );
    }

    public static void drawBookmark(GuiGraphics graphics, BookChapter chapter, boolean hovered, boolean mirrored, int x, int y) {
        int bookmarkRow = Math.max(0, Math.min(3, chapter.bookmark()));
        int frameRow = Math.max(0, Math.min(4, chapter.frame()));
        int leftRow = Math.max(0, Math.min(6, chapter.left()));
        int rightRow = Math.max(0, Math.min(6, chapter.right()));
        int bookmarkWidth = bookmarkContentWidth(chapter);
        int bookmarkHeight = bookmarkContentHeight(chapter);
        int frameWidth = frameContentWidth(chapter);
        int frameHeight = frameContentHeight(chapter);
        int imageWidth = BookmarkLayout.DEFAULT.imageWidth();
        int imageHeight = BookmarkLayout.DEFAULT.imageHeight();
        int leftWidth = leftWidth(chapter);
        int leftHeight = leftHeight(chapter);
        int rightWidth = rightWidth(chapter);
        int rightHeight = rightHeight(chapter);
        int mirrorWidth = normalBookmarkWidth(chapter);
        int bookmarkDrawX = BookmarkLayout.DEFAULT.bookmarkX();
        int bookmarkDrawY = BookmarkLayout.DEFAULT.bookmarkY();
        int frameDrawX = BookmarkLayout.DEFAULT.frameX();
        int frameDrawY = BookmarkLayout.DEFAULT.frameY();
        int imageDrawX = BookmarkLayout.DEFAULT.imageX();
        int imageDrawY = BookmarkLayout.DEFAULT.imageY();
        if (mirrored) {
            bookmarkDrawX = mirrorX(mirrorWidth, bookmarkDrawX, bookmarkWidth);
            frameDrawX = mirrorX(mirrorWidth, frameDrawX, frameWidth);
            imageDrawX = mirrorX(mirrorWidth, imageDrawX, imageWidth);
        }
        int leftDrawX = frameDrawX - leftWidth - BookmarkLayout.DEFAULT.frameGapLeft();
        int rightDrawX = frameDrawX + frameWidth + BookmarkLayout.DEFAULT.frameGapRight();
        int leftDrawY = BookmarkLayout.DEFAULT.sideOffsetY();
        int rightDrawY = BookmarkLayout.DEFAULT.sideOffsetY();
//        if (hovered) { RenderSystem.setShaderColor(0.65F, 0.65F, 0.65F, 1.0F); }
        if (chapter.bookmarkImage() != null) {
            drawIcon(graphics, chapter.bookmarkImage(), x + bookmarkDrawX, y + bookmarkDrawY, bookmarkWidth, bookmarkHeight, mirrored);
        } else {
            drawAtlas(graphics, x + bookmarkDrawX, y + bookmarkDrawY, bookmarkWidth, bookmarkHeight, BOOKMARK_X[bookmarkRow], BOOKMARK_Y[bookmarkRow], BOOKMARK_WIDTH, BOOKMARK_HEIGHT, mirrored);
        }
        if (chapter.leftImage() != null) {
            drawIcon(graphics, chapter.leftImage(), x + leftDrawX, y + leftDrawY, leftWidth, leftHeight, false);
        } else {
            drawAtlas(graphics, x + leftDrawX, y + leftDrawY, leftWidth, leftHeight, SIDE_X[leftRow], SIDE_Y[leftRow], SIDE_WIDTH[leftRow], SIDE_HEIGHT[leftRow], false);
        }
        if (chapter.rightImage() != null) {
            drawIcon(graphics, chapter.rightImage(), x + rightDrawX, y + rightDrawY, rightWidth, rightHeight, true);
        } else {
            drawAtlas(graphics, x + rightDrawX, y + rightDrawY, rightWidth, rightHeight, SIDE_X[rightRow], SIDE_Y[rightRow], SIDE_WIDTH[rightRow], SIDE_HEIGHT[rightRow], true);
        }
        if (chapter.frameImage() != null) {
            drawIcon(graphics, chapter.frameImage(), x + frameDrawX, y + frameDrawY, frameWidth, frameHeight, mirrored);
        } else {
            drawAtlas(graphics, x + frameDrawX, y + frameDrawY, frameWidth, frameHeight, FRAME_X[frameRow], FRAME_Y[frameRow], FRAME_WIDTH, FRAME_HEIGHT, mirrored);
        }
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        int iconWidth = Math.min(chapter.icon().width(), imageWidth);
        int iconHeight = Math.min(chapter.icon().height(), imageHeight);
        int iconX = imageDrawX + (imageWidth - iconWidth) / 2;
        int iconY = imageDrawY + (imageHeight - iconHeight) / 2;
        drawIcon(graphics, chapter.icon(), x + iconX, y + iconY, iconWidth, iconHeight, false);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private static int mirrorX(int overallWidth, int x, int width) { return overallWidth - x - width; }

    private static void drawAtlas(GuiGraphics graphics, int x, int y, int width, int height, int u, int v, int sourceWidth, int sourceHeight, boolean mirrored) {
        if (mirrored) { graphics.blit(TEXTURE, x, y, width, height, (float) (u + sourceWidth), (float) v, -sourceWidth, sourceHeight, TEXTURE_WIDTH, TEXTURE_HEIGHT); }
        else { graphics.blit(TEXTURE, x, y, width, height, (float) u, (float) v, sourceWidth, sourceHeight, TEXTURE_WIDTH, TEXTURE_HEIGHT); }
    }

    public static void drawArrowLeft(GuiGraphics graphics, int x, int y, boolean hovered, boolean enabled) { drawArrow(graphics, LEFT_ARROW_U, x, y, hovered, enabled); }
    public static void drawArrowRight(GuiGraphics graphics, int x, int y, boolean hovered, boolean enabled) { drawArrow(graphics, RIGHT_ARROW_U, x, y, hovered, enabled); }
    public static void drawBackArrow(GuiGraphics graphics, int x, int y, boolean hovered) { drawArrow(graphics, BACK_ARROW_U, x, y, hovered, true); }

    private static void drawArrow(GuiGraphics graphics, int u, int x, int y, boolean hovered, boolean enabled) {
        if (!enabled) { RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 0.35F); }
        if (hovered) { graphics.blit(TEXTURE, x, y + 1, ARROW_WIDTH, ARROW_DARK_HEIGHT, (float) u, (float) ARROW_DARK_V, ARROW_WIDTH, ARROW_DARK_HEIGHT, TEXTURE_WIDTH, TEXTURE_HEIGHT); }
        else { graphics.blit(TEXTURE, x, y + 2, ARROW_WIDTH, ARROW_LIGHT_HEIGHT, (float) u, (float) ARROW_LIGHT_V, ARROW_WIDTH, ARROW_LIGHT_HEIGHT, TEXTURE_WIDTH, TEXTURE_HEIGHT); }
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public static void drawEye(GuiGraphics graphics, int x, int y, boolean hovered) {
        int v = hovered ? EYE_DARK_V : EYE_LIGHT_V;
        graphics.blit(TEXTURE, x, y, EYE_WIDTH, EYE_HEIGHT, (float) EYE_U, (float) v, EYE_WIDTH, EYE_HEIGHT, TEXTURE_WIDTH, TEXTURE_HEIGHT);
    }

    public static void drawIcon(GuiGraphics graphics, BookIcon icon, int x, int y, int width, int height) {
        drawIcon(graphics, icon, x, y, width, height, false);
    }

    public static void drawIcon(GuiGraphics graphics, BookIcon icon, int x, int y, int width, int height, boolean mirrored) {
        if (icon == null) { return; }
        if (icon instanceof BookIcon.ItemIcon itemIcon) {
            ItemIconRenderer.draw(graphics, itemIcon.stack(), x, y, width, height);
            return;
        }
        if (icon instanceof BookIcon.AtlasIcon atlasIcon) {
            if (mirrored) {
                graphics.blit(atlasIcon.texture(), x, y, width, height, (float) (atlasIcon.u() + atlasIcon.sourceWidth()), (float) atlasIcon.v(), -atlasIcon.sourceWidth(), atlasIcon.sourceHeight(), atlasIcon.textureWidth(), atlasIcon.textureHeight());
            } else {
                graphics.blit(atlasIcon.texture(), x, y, width, height, (float) atlasIcon.u(), (float) atlasIcon.v(), atlasIcon.sourceWidth(), atlasIcon.sourceHeight(), atlasIcon.textureWidth(), atlasIcon.textureHeight());
            }
            return;
        }
        if (icon instanceof BookIcon.TextureIcon textureIcon) {
            if (mirrored) {
                graphics.blit(textureIcon.texture(), x, y, width, height, (float) textureIcon.textureWidth(), 0.0F, -textureIcon.textureWidth(), textureIcon.textureHeight(), textureIcon.textureWidth(), textureIcon.textureHeight());
            } else {
                graphics.blit(textureIcon.texture(), x, y, width, height, 0.0F, 0.0F, textureIcon.textureWidth(), textureIcon.textureHeight(), textureIcon.textureWidth(), textureIcon.textureHeight());
            }
        }
    }
}
