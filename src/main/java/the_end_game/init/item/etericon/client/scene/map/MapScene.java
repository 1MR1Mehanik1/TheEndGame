package the_end_game.init.item.etericon.client.scene.map;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import the_end_game.init.item.etericon.book.BookCatalog;
import the_end_game.init.item.etericon.book.BookChapter;
import the_end_game.init.item.etericon.book.BookEntry;
import the_end_game.init.item.etericon.book.BookProgress;
import the_end_game.init.item.etericon.client.ClientEtericonData;
import the_end_game.init.item.etericon.client.EtericonAtlas;
import the_end_game.init.item.etericon.client.EtericonLayout;
import the_end_game.init.item.etericon.client.EtericonScreen;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class MapScene {
    private static final float SEARCH_TEXT_SCALE = 0.6F;
    private static final int SEARCH_TEXT_INSET = 14;
    private static final int SEARCH_TEXT_RIGHT_PADDING = 8;
    private static final int MAP_PAPER_Y = 0;
    private static final int MAP_VIEW_OFFSET_X = 14;
    private static final int MAP_VIEW_Y = 10;
    private static final int MAP_VIEW_WIDTH = 244;
    private static final int MAP_VIEW_HEIGHT = 158;

    private final EtericonScreen screen;
    private final EntryNodeLayer entryNodeLayer;
    private double panX;
    private double panY;
    private double zoom = 1.0;
    private boolean dragging;
    private double dragX;
    private double dragY;
    private int mapStripWidth = 58;
    private int mapPaperX = 58;
    private int mapPaperRight = 330;
    private int mapSceneWidth = 420;
    private int mapViewX = 68;
    private String layoutVersion = "";
    private boolean layoutReady;
    private List<List<BookmarkPlacement>> cachedBookmarkPages = List.of();
    private int bookmarkPage;
    private boolean bookmarkPageInitialized;
    private boolean searchOpen;
    private boolean searchFocused;
    private String searchQuery = "";
    private int searchCursor;
    private int searchAnchor;
    private int searchScroll;
    private int searchSelected;
    private boolean searchSelectionActive;

    public MapScene(EtericonScreen screen) {
        this.screen = screen;
        this.entryNodeLayer = new EntryNodeLayer(screen, this);
    }

    public int mapPaperX() { return this.mapPaperX; }
    public int mapPaperRight() { return this.mapPaperRight; }
    public int mapSceneWidth() { return this.mapSceneWidth; }
    public int mapViewX() { return this.mapViewX; }
    public int mapViewY() { return MAP_VIEW_Y; }
    public int mapViewWidth() { return MAP_VIEW_WIDTH; }
    public int mapViewHeight() { return MAP_VIEW_HEIGHT; }
    public double panX() { return this.panX; }
    public double panY() { return this.panY; }
    public double zoom() { return this.zoom; }
    public int bookmarkPage() { return this.bookmarkPage; }
    public boolean bookmarkPageInitialized() { return this.bookmarkPageInitialized; }
    public void setBookmarkPage(int value) { this.bookmarkPage = value; }
    public void setBookmarkPageInitialized(boolean value) { this.bookmarkPageInitialized = value; }
    public boolean searchOpen() { return this.searchOpen; }
    public boolean searchFocused() { return this.searchFocused; }
    public String searchQuery() { return this.searchQuery; }
    public int searchCursor() { return this.searchCursor; }
    public int searchSelected() { return this.searchSelected; }
    public boolean searchSelectionActive() { return this.searchSelectionActive; }

    public void resetView() {
        this.panX = 0.0;
        this.panY = 0.0;
        this.zoom = 1.0;
        this.bookmarkPageInitialized = false;
    }

    public void setView(double panX, double panY, double zoom, int bookmarkPage) {
        this.panX = panX;
        this.panY = panY;
        this.zoom = Math.max(0.6, Math.min(2.5, zoom));
        this.bookmarkPage = bookmarkPage;
    }

    public void startDrag(double mouseX, double mouseY) {
        this.dragging = true;
        this.dragX = mouseX;
        this.dragY = mouseY;
    }

    public void stopDrag() { this.dragging = false; }

    public boolean isDragging() { return this.dragging; }

    public void drag(double mouseX, double mouseY, float sceneScale) {
        if (!this.dragging) { return; }
        this.panX += (mouseX - this.dragX) / sceneScale / this.zoom;
        this.panY += (mouseY - this.dragY) / sceneScale / this.zoom;
        this.panX = Math.max(-200.0, Math.min(200.0, this.panX));
        this.panY = Math.max(-160.0, Math.min(160.0, this.panY));
        this.dragX = mouseX;
        this.dragY = mouseY;
    }

    public void scrollZoom(int mouseX, int mouseY, double scrollY, float sceneScale) {
        if (scrollY == 0.0) { return; }
        double oldZoom = this.zoom;
        double newZoom = Math.max(0.6, Math.min(2.5, this.zoom * (scrollY > 0.0 ? 1.15 : 1.0 / 1.15)));
        if (newZoom != oldZoom) {
            double worldX = 136.0 + (mouseX - this.mapPaperX - 136.0 - this.panX) / oldZoom;
            double worldY = 90.0 + (mouseY - MAP_PAPER_Y - 90.0 - this.panY) / oldZoom;
            this.panX = mouseX - this.mapPaperX - 136.0 - (worldX - 136.0) * newZoom;
            this.panY = mouseY - MAP_PAPER_Y - 90.0 - (worldY - 90.0) * newZoom;
            this.zoom = newZoom;
        }
    }

    public void ensureLayout() {
        String version = ClientEtericonData.version();
        if (this.layoutReady && version.equals(this.layoutVersion)) { return; }
        this.layoutVersion = version;
        this.layoutReady = true;
        int stripWidth = 58;
        List<BookChapter> chapters = ClientEtericonData.catalog().sortedChapters();
        for (BookChapter chapter : chapters) {
            stripWidth = Math.max(stripWidth, EtericonAtlas.bookmarkWidth(chapter) + BookmarkLayout.DEFAULT.stripMargin());
        }
        this.mapStripWidth = stripWidth;
        this.mapPaperX = stripWidth;
        this.mapPaperRight = this.mapPaperX + EtericonAtlas.BOOK_WIDTH;
        this.mapSceneWidth = stripWidth * 2 + EtericonAtlas.BOOK_WIDTH;
        this.mapViewX = this.mapPaperX + MAP_VIEW_OFFSET_X;
        this.cachedBookmarkPages = this.buildBookmarkPages(chapters);
    }

    private List<List<BookmarkPlacement>> bookmarkPages() {
        this.ensureLayout();
        return this.cachedBookmarkPages;
    }

    public void draw(GuiGraphics graphics) {
        BookCatalog catalog = ClientEtericonData.catalog();
        BookChapter chapter = this.screen.chapterId() == null ? null : catalog.chapter(this.screen.chapterId());
        if (chapter == null || !chapter.isUnlocked(catalog, ClientEtericonData.progress())) {
            chapter = this.screen.firstUnlockedChapter();
            this.screen.setChapterId(chapter == null ? null : chapter.id());
        }
        EtericonAtlas.drawCoverFrame(graphics, this.mapPaperX, MAP_PAPER_Y);
        List<List<BookmarkPlacement>> pages = this.bookmarkPages();
        if (!this.bookmarkPageInitialized) {
            this.bookmarkPage = this.pageForBookmark(pages, this.screen.chapterId());
            this.bookmarkPageInitialized = true;
        }
        this.bookmarkPage = Math.max(0, Math.min(Math.max(0, pages.size() - 1), this.bookmarkPage));
        List<BookmarkPlacement> placements = pages.isEmpty() ? List.of() : pages.get(this.bookmarkPage);
        if (!this.screen.overlayBase()) {
            this.drawBookmarks(graphics, placements);
            this.drawStaticTabs(graphics);
        }
        if (chapter == null) {
            this.screen.drawCentered(graphics, Component.translatable("gui.the_end_game.etericon.no_chapters"), this.mapPaperX + 126, MAP_PAPER_Y + 82, 0.7F, 0xFF7A5A3A);
            if (!this.screen.overlayBase()) {
                this.drawBookmarkPageArrows(graphics, pages.size());
                if (this.searchOpen) { this.drawSearchBar(graphics); }
            }
            return;
        }
        BookProgress progress = ClientEtericonData.progress();
        boolean searchActive = this.searchOpen && !this.searchQuery.isBlank();
        List<BookEntry> entries = new ArrayList<>();
        for (BookEntry candidate : catalog.entriesFor(chapter.id())) {
            if (candidate.isVisible(progress)) { entries.add(candidate); }
        }
        EtericonAtlas.drawMap(graphics, this.mapPaperX, MAP_PAPER_Y);
        if (entries.isEmpty()) {
            if (!searchActive) { this.screen.drawCentered(graphics, Component.translatable("gui.the_end_game.etericon.empty_map"), this.mapPaperX + 126, MAP_PAPER_Y + 82, 0.7F, 0xFF7A5A3A); }
            if (!this.screen.overlayBase()) {
                this.drawBookmarkPageArrows(graphics, pages.size());
                if (this.searchOpen) { this.drawSearchBar(graphics); }
            }
            return;
        }
        int contentHeight = MAP_VIEW_HEIGHT;
        this.screen.enableScissor(graphics, this.mapViewX, MAP_VIEW_Y, MAP_VIEW_WIDTH, contentHeight);
        for (BookEntry entry : entries) {
            for (ResourceLocation required : entry.requires()) {
                BookEntry parent = catalog.entry(required);
                if (parent == null || !parent.chapter().equals(chapter.id()) || !parent.isVisible(progress)) { continue; }
                if (searchActive && (!this.matchesSearch(parent) || !this.matchesSearch(entry))) { continue; }
                int x1 = (int) this.mapX(parent.x());
                int y1 = (int) this.mapY(parent.y());
                int x2 = (int) this.mapX(entry.x());
                int y2 = (int) this.mapY(entry.y());
                CurveRenderer.draw(graphics, x1, y1, x2, y2, progress.stateOf(parent), progress.stateOf(entry));
            }
        }
        this.entryNodeLayer.draw(graphics, entries, progress, searchActive, this::matchesSearch);
        this.screen.disableScissor(graphics);
        if (!this.screen.overlayBase()) {
            this.drawBookmarkPageArrows(graphics, pages.size());
            if (this.searchOpen) { this.drawSearchBar(graphics); }
        }
    }

    private boolean isOverlayHovered() {
        return this.isSearchHovered() || this.isStaticTabsHovered();
    }

    boolean overlayHovered() { return this.isOverlayHovered(); }

    private boolean isSearchHovered() {
        if (!this.searchOpen) { return false; }
        int searchX = this.mapPaperX + (EtericonScreen.MAP_PAPER_WIDTH - EtericonLayout.SEARCH_WIDTH) / 2;
        if (this.screen.isInside(this.screen.sceneMouseX(), this.screen.sceneMouseY(), searchX, EtericonLayout.SEARCH_Y, EtericonLayout.SEARCH_WIDTH, EtericonLayout.SEARCH_HEIGHT)) {
            return true;
        }
        if (!this.searchFocused || this.searchQuery.isBlank()) { return false; }
        int visible = Math.min(5, this.searchResults().size());
        if (visible <= 0) { return false; }
        int listWidth = 140;
        int listX = searchX + (EtericonLayout.SEARCH_WIDTH - listWidth) / 2;
        int listY = EtericonLayout.SEARCH_Y + EtericonLayout.SEARCH_HEIGHT + 2;
        int listHeight = visible * 11 + 2;
        return this.screen.isInside(this.screen.sceneMouseX(), this.screen.sceneMouseY(), listX, listY, listWidth, listHeight);
    }

    private boolean isStaticTabsHovered() {
        int topX = this.mapPaperX + EtericonLayout.STATIC_TAB_TOP_INSET_X;
        int topY = -EtericonLayout.STATIC_TAB_HEIGHT + EtericonLayout.STATIC_TAB_TOP_INSET_Y;
        int topHeight = EtericonLayout.STATIC_TAB_HEIGHT - EtericonLayout.STATIC_TAB_TOP_INSET_Y;
        int bottomX = this.mapPaperRight - EtericonLayout.STATIC_TAB_WIDTH - EtericonLayout.STATIC_TAB_BOTTOM_INSET_X;
        int bottomY = EtericonAtlas.BOOK_HEIGHT;
        int bottomHeight = EtericonLayout.STATIC_TAB_HEIGHT - EtericonLayout.STATIC_TAB_BOTTOM_INSET_Y;
        return this.screen.isInside(this.screen.sceneMouseX(), this.screen.sceneMouseY(), topX, topY, EtericonLayout.STATIC_TAB_WIDTH, topHeight)
                || this.screen.isInside(this.screen.sceneMouseX(), this.screen.sceneMouseY(), bottomX, bottomY, EtericonLayout.STATIC_TAB_WIDTH, bottomHeight);
    }

    private boolean matchesSearch(BookEntry entry) {
        if (!this.searchOpen || this.searchQuery.isBlank()) { return true; }
        String query = this.searchQuery.toLowerCase(Locale.ROOT);
        return entry.title().getString().toLowerCase(Locale.ROOT).contains(query) || entry.id().toString().toLowerCase(Locale.ROOT).contains(query);
    }

    public void toggleSearch() {
        this.searchOpen = !this.searchOpen;
        this.searchFocused = false;
        this.searchCursor = 0;
        this.searchAnchor = 0;
        this.searchScroll = 0;
        this.searchSelected = 0;
        this.searchSelectionActive = false;
        if (!this.searchOpen) { this.searchQuery = ""; }
    }

    public void closeSearch() {
        this.searchOpen = false;
        this.searchFocused = false;
        this.searchQuery = "";
        this.searchCursor = 0;
        this.searchAnchor = 0;
        this.searchScroll = 0;
        this.searchSelected = 0;
        this.searchSelectionActive = false;
    }

    public void focusSearch() {
        this.searchFocused = true;
        this.searchCursor = this.searchQuery.length();
        this.searchAnchor = this.searchCursor;
    }

    public void unfocusSearch() { this.searchFocused = false; }

    public boolean keyPressed(int keyCode, int modifiers) {
        if (!this.searchOpen) { return false; }
        if (keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE) { this.closeSearch(); return true; }
        if (keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_ENTER) {
            if (this.searchFocused) { this.openSearchResult(); }
            return true;
        }
        if (!this.searchFocused) { return false; }
        boolean shift = (modifiers & org.lwjgl.glfw.GLFW.GLFW_MOD_SHIFT) != 0;
        boolean control = (modifiers & org.lwjgl.glfw.GLFW.GLFW_MOD_CONTROL) != 0;
        if (control && keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_A) {
            this.searchAnchor = 0;
            this.searchCursor = this.searchQuery.length();
            return true;
        }
        if (keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_BACKSPACE) {
            if (this.hasTextSelection()) {
                this.deleteTextSelection();
            } else if (this.searchCursor > 0) {
                int cursor = this.clampCursor(this.searchCursor);
                this.searchQuery = this.searchQuery.substring(0, cursor - 1) + this.searchQuery.substring(cursor);
                this.searchCursor = cursor - 1;
                this.searchAnchor = this.searchCursor;
            }
            this.searchSelected = 0;
            this.searchSelectionActive = false;
            return true;
        }
        if (keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_DELETE) {
            if (this.hasTextSelection()) {
                this.deleteTextSelection();
            } else {
                int cursor = this.clampCursor(this.searchCursor);
                if (cursor < this.searchQuery.length()) {
                    this.searchQuery = this.searchQuery.substring(0, cursor) + this.searchQuery.substring(cursor + 1);
                }
            }
            this.searchSelected = 0;
            this.searchSelectionActive = false;
            return true;
        }
        if (keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_UP) {
            this.searchSelected = Math.max(0, this.searchSelected - 1);
            this.searchSelectionActive = true;
            return true;
        }
        if (keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN) {
            int count = Math.min(5, this.searchResults().size());
            this.searchSelected = Math.min(Math.max(0, count - 1), this.searchSelected + 1);
            this.searchSelectionActive = true;
            return true;
        }
        if (keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT) {
            int cursor = this.clampCursor(this.searchCursor);
            this.searchCursor = control ? this.previousWord(cursor) : Math.max(0, cursor - 1);
            if (!shift) { this.searchAnchor = this.searchCursor; }
            return true;
        }
        if (keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT) {
            int cursor = this.clampCursor(this.searchCursor);
            this.searchCursor = control ? this.nextWord(cursor) : Math.min(this.searchQuery.length(), cursor + 1);
            if (!shift) { this.searchAnchor = this.searchCursor; }
            return true;
        }
        if (keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_HOME) {
            this.searchCursor = 0;
            this.searchAnchor = 0;
            return true;
        }
        if (keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_END) {
            this.searchCursor = this.searchQuery.length();
            this.searchAnchor = this.searchCursor;
            return true;
        }
        return false;
    }

    public boolean charTyped(char codePoint) {
        if (!this.searchOpen || !this.searchFocused || Character.isISOControl(codePoint)) { return false; }
        if (this.hasTextSelection()) { this.deleteTextSelection(); }
        if (this.searchQuery.length() >= 120) { return true; }
        int cursor = this.clampCursor(this.searchCursor);
        this.searchQuery = this.searchQuery.substring(0, cursor) + codePoint + this.searchQuery.substring(cursor);
        this.searchCursor = cursor + 1;
        this.searchAnchor = this.searchCursor;
        this.searchSelected = 0;
        this.searchSelectionActive = false;
        return true;
    }

    private int clampCursor(int value) { return Math.max(0, Math.min(this.searchQuery.length(), value)); }

    private int selectionStart() { return Math.min(this.clampCursor(this.searchAnchor), this.clampCursor(this.searchCursor)); }

    private int selectionEnd() { return Math.max(this.clampCursor(this.searchAnchor), this.clampCursor(this.searchCursor)); }

    private boolean hasTextSelection() { return this.selectionStart() != this.selectionEnd(); }

    private void deleteTextSelection() {
        int start = this.selectionStart();
        int end = this.selectionEnd();
        if (start == end) { return; }
        this.searchQuery = this.searchQuery.substring(0, start) + this.searchQuery.substring(end);
        this.searchCursor = start;
        this.searchAnchor = start;
    }

    private int textPixelWidth(String text) { return (int) (this.screen.font().width(text) * SEARCH_TEXT_SCALE); }

    private int previousWord(int from) {
        int cursor = from;
        while (cursor > 0 && !isWordCharacter(this.searchQuery.charAt(cursor - 1))) { cursor--; }
        while (cursor > 0 && isWordCharacter(this.searchQuery.charAt(cursor - 1))) { cursor--; }
        return cursor;
    }

    private int nextWord(int from) {
        int cursor = from;
        while (cursor < this.searchQuery.length() && isWordCharacter(this.searchQuery.charAt(cursor))) { cursor++; }
        while (cursor < this.searchQuery.length() && !isWordCharacter(this.searchQuery.charAt(cursor))) { cursor++; }
        return cursor;
    }

    private boolean isWordCharacter(char value) { return Character.isLetterOrDigit(value) || value == '_'; }

    private void updateSearchScroll(int viewWidth) {
        int cursor = this.clampCursor(this.searchCursor);
        int cursorWidth = this.textPixelWidth(this.searchQuery.substring(0, cursor));
        int totalWidth = this.textPixelWidth(this.searchQuery);
        if (cursorWidth - this.searchScroll > viewWidth) { this.searchScroll = cursorWidth - viewWidth; }
        if (cursorWidth - this.searchScroll < 0) { this.searchScroll = cursorWidth; }
        this.searchScroll = Math.max(0, Math.min(Math.max(0, totalWidth - viewWidth), this.searchScroll));
    }

    public void navigateBookmark(int direction) {
        BookCatalog catalog = ClientEtericonData.catalog();
        List<List<BookmarkPlacement>> pages = this.bookmarkPages();
        if (pages.isEmpty()) { return; }
        this.bookmarkPage = Math.max(0, Math.min(pages.size() - 1, this.bookmarkPage));
        List<BookmarkPlacement> placements = pages.get(this.bookmarkPage);
        if (placements.isEmpty()) { return; }
        int current = -1;
        for (int index = 0; index < placements.size(); index++) {
            if (placements.get(index).chapter.id().equals(this.screen.chapterId())) { current = index; break; }
        }
        if (current < 0) { current = 0; }
        BookProgress progress = ClientEtericonData.progress();
        int index = current;
        while (true) {
            index += direction;
            if (index < 0 || index >= placements.size()) { return; }
            BookChapter chapter = placements.get(index).chapter;
            if (chapter.isUnlocked(catalog, progress)) {
                this.screen.openChapter(chapter.id());
                return;
            }
        }
    }

    public void navigateBookmarkPage(int direction) {
        BookCatalog catalog = ClientEtericonData.catalog();
        List<List<BookmarkPlacement>> pages = this.bookmarkPages();
        if (pages.isEmpty()) { return; }
        int page = Math.max(0, Math.min(pages.size() - 1, this.bookmarkPage + direction));
        if (page == this.bookmarkPage) { return; }
        this.bookmarkPage = page;
        this.bookmarkPageInitialized = true;
        List<BookmarkPlacement> placements = pages.get(page);
        if (!placements.isEmpty()) {
            BookProgress progress = ClientEtericonData.progress();
            for (BookmarkPlacement placement : placements) {
                if (placement.chapter.isUnlocked(catalog, progress)) {
                    this.screen.openChapter(placement.chapter.id());
                    this.bookmarkPage = page;
                    this.bookmarkPageInitialized = true;
                    return;
                }
            }
        }
    }

    private void drawStaticTabs(GuiGraphics graphics) {
        int topX = this.mapPaperX + EtericonLayout.STATIC_TAB_TOP_INSET_X;
        int topY = -EtericonLayout.STATIC_TAB_HEIGHT + EtericonLayout.STATIC_TAB_TOP_INSET_Y;
        int topVisibleHeight = EtericonLayout.STATIC_TAB_HEIGHT - EtericonLayout.STATIC_TAB_TOP_INSET_Y;
        int bottomX = this.mapPaperRight - EtericonLayout.STATIC_TAB_WIDTH - EtericonLayout.STATIC_TAB_BOTTOM_INSET_X;
        int bottomY = EtericonAtlas.BOOK_HEIGHT - EtericonLayout.STATIC_TAB_BOTTOM_INSET_Y;
        int bottomVisibleY = EtericonAtlas.BOOK_HEIGHT;
        int bottomVisibleHeight = EtericonLayout.STATIC_TAB_HEIGHT - EtericonLayout.STATIC_TAB_BOTTOM_INSET_Y;
        boolean topHovered = this.screen.isInside(this.screen.sceneMouseX(), this.screen.sceneMouseY(), topX, topY, EtericonLayout.STATIC_TAB_WIDTH, topVisibleHeight);
        boolean bottomHovered = this.screen.isInside(this.screen.sceneMouseX(), this.screen.sceneMouseY(), bottomX, bottomVisibleY, EtericonLayout.STATIC_TAB_WIDTH, bottomVisibleHeight);
        this.screen.drawStaticTab(graphics, 1, topX, topY, true, topHovered);
        this.screen.drawStaticTab(graphics, 1, bottomX, bottomY, false, bottomHovered);
        if (topHovered) { this.screen.setHoveredButtonTooltip(List.of(Component.translatable("gui.the_end_game.etericon.search"))); }
        if (bottomHovered) { this.screen.setHoveredButtonTooltip(List.of(Component.translatable("gui.the_end_game.etericon.help"))); }
        this.screen.addClickable(topX, topY, EtericonLayout.STATIC_TAB_WIDTH, topVisibleHeight, this.screen::toggleSearch);
        this.screen.addClickable(bottomX, bottomVisibleY, EtericonLayout.STATIC_TAB_WIDTH, bottomVisibleHeight, this.screen::toggleSpecial);
    }

    private void drawSearchBar(GuiGraphics graphics) {
        graphics.pose().pushPose();
        graphics.pose().translate(0.0F, 0.0F, 150.0F);

        int x = this.mapPaperX + (EtericonScreen.MAP_PAPER_WIDTH - EtericonLayout.SEARCH_WIDTH) / 2;
        int y = EtericonLayout.SEARCH_Y;
        EtericonAtlas.drawSearchBar(graphics, x, y);
        int textX = x + SEARCH_TEXT_INSET;
        int textWidth = EtericonLayout.SEARCH_WIDTH - SEARCH_TEXT_INSET - SEARCH_TEXT_RIGHT_PADDING;
        this.updateSearchScroll(textWidth);
        this.screen.enableScissor(graphics, textX, y + 1, textWidth, EtericonLayout.SEARCH_HEIGHT - 2);
        if (this.searchQuery.isEmpty()) {
            this.screen.drawScaledText(graphics, Component.translatable("gui.the_end_game.etericon.search"), textX, y + 5, SEARCH_TEXT_SCALE, 0xFF7A6A5A, false);
        } else {
            if (this.hasTextSelection()) {
                int selectionX = textX + this.textPixelWidth(this.searchQuery.substring(0, this.selectionStart())) - this.searchScroll;
                int selectionEndX = textX + this.textPixelWidth(this.searchQuery.substring(0, this.selectionEnd())) - this.searchScroll;
                graphics.fill(selectionX, y + 3, selectionEndX, y + 12, 0x553B2A1A);
            }
            this.screen.drawScaledText(graphics, Component.literal(this.searchQuery), textX - this.searchScroll, y + 5, SEARCH_TEXT_SCALE, 0xFF2A1A0E, false);
        }
        this.drawSearchCaret(graphics, textX, y, textWidth);
        this.screen.disableScissor(graphics);
        this.screen.addClickable(x, y, EtericonLayout.SEARCH_WIDTH, EtericonLayout.SEARCH_HEIGHT, this::focusSearch);
        if (!this.searchQuery.isBlank() && this.searchFocused) { this.drawSearchResults(graphics, x, y); }
        graphics.pose().popPose();
    }

    private void drawSearchCaret(GuiGraphics graphics, int textX, int y, int textWidth) {
        if (!this.searchFocused) { return; }
        if ((System.currentTimeMillis() / 450L) % 2L != 0L) { return; }
        int cursor = this.clampCursor(this.searchCursor);
        int caretX = textX + this.textPixelWidth(this.searchQuery.substring(0, cursor)) - this.searchScroll;
        caretX = Math.max(textX, Math.min(textX + textWidth - 1, caretX));
        graphics.fill(caretX, y + 4, caretX + 1, y + 11, 0xFF2A1A0E);
    }
    private void drawSearchResults(GuiGraphics graphics, int searchX, int searchY) {
        List<BookEntry> results = this.searchResults();
        if (results.isEmpty()) { return; }
        this.searchSelected = Math.max(0, Math.min(this.searchSelected, results.size() - 1));
        int rowHeight = 11;
        int visible = Math.min(5, results.size());
        int listWidth = 140;
        int listX = searchX + (EtericonLayout.SEARCH_WIDTH - listWidth) / 2;
        int listY = searchY + EtericonLayout.SEARCH_HEIGHT + 2;
        int listHeight = visible * rowHeight + 2;
        graphics.fill(listX, listY, listX + listWidth, listY + listHeight, 0xE8F3E3D0);
        this.screen.drawBorder(graphics, listX, listY, listWidth, listHeight, 0xFF3B2A1A);
        for (int index = 0; index < visible; index++) {
            BookEntry entry = results.get(index);
            int rowY = listY + 1 + index * rowHeight;
            boolean hovered = this.screen.isInside(this.screen.sceneMouseX(), this.screen.sceneMouseY(), listX + 1, rowY, listWidth - 2, rowHeight);
            boolean selected = this.searchSelectionActive && index == this.searchSelected;
            if (selected || hovered) { graphics.fill(listX + 1, rowY, listX + listWidth - 1, rowY + rowHeight, 0x553B2A1A); }
            int textY = rowY + (rowHeight - Math.round(9.0F * 0.55F)) / 2;
            this.screen.enableScissor(graphics, listX + 2, rowY, listWidth - 4, rowHeight);
            this.screen.drawScaledText(graphics, entry.title(), listX + 4, textY, 0.55F, 0xFF2A1A0E, false);
            this.screen.disableScissor(graphics);
            int rowIndex = index;
            this.screen.addClickable(listX + 1, rowY, listWidth - 2, rowHeight, () -> {
                this.searchSelected = rowIndex;
                this.searchSelectionActive = true;
                this.openSearchResult();
            });
        }
        if (results.size() > visible) {
            this.screen.drawScaledText(graphics, Component.literal("+" + (results.size() - visible)), listX + listWidth - 18, listY + listHeight + 2, 0.45F, 0xFF5A4A3A, false);
        }
    }
    private List<BookEntry> searchResults() {
        List<BookEntry> results = new ArrayList<>();
        if (this.searchQuery.isBlank()) { return results; }
        BookCatalog catalog = ClientEtericonData.catalog();
        BookProgress progress = ClientEtericonData.progress();
        for (BookChapter chapter : catalog.sortedChapters()) {
            if (!chapter.isUnlocked(catalog, progress)) { continue; }
            for (BookEntry entry : catalog.entriesFor(chapter.id())) {
                if (entry.isVisible(progress) && this.matchesSearch(entry)) { results.add(entry); }
            }
        }
        return results;
    }
    private void openSearchResult() {
        List<BookEntry> results = this.searchResults();
        if (results.isEmpty()) { return; }
        this.searchSelected = Math.max(0, Math.min(this.searchSelected, results.size() - 1));
        BookEntry entry = results.get(this.searchSelected);
        this.screen.prepareSearchResult(entry.chapter());
        List<List<BookmarkPlacement>> pages = this.bookmarkPages();
        this.bookmarkPage = this.pageForBookmark(pages, entry.chapter());
        this.bookmarkPageInitialized = true;
        this.screen.requestEntry(entry.id());
    }

    private void drawBookmarks(GuiGraphics graphics, List<BookmarkPlacement> placements) {
        BookCatalog catalog = ClientEtericonData.catalog();
        BookProgress progress = ClientEtericonData.progress();
        for (BookmarkPlacement placement : placements) {
            int baseHitX = placement.right ? this.mapPaperRight : placement.x;
            int baseHitWidth = placement.right ? placement.x + placement.width - this.mapPaperRight : this.mapPaperX - placement.x;
            if (baseHitWidth <= 0) { continue; }
            boolean baseHovered = this.screen.isInside(this.screen.sceneMouseX(), this.screen.sceneMouseY(), baseHitX, placement.y, baseHitWidth, placement.height);
            boolean active = placement.chapter.id().equals(this.screen.chapterId());
            boolean unlocked = placement.chapter.isUnlocked(catalog, progress);
            int offset = baseHovered ? BookmarkLayout.DEFAULT.hoverOffset() : 0;
            if (active) { offset = Math.max(offset, BookmarkLayout.DEFAULT.hoverOffset() + 4); }
            int drawX = placement.x + (placement.right ? offset : -offset);
            if (!unlocked) { RenderSystem.setShaderColor(0.45F, 0.45F, 0.45F, 1.0F); }
            EtericonAtlas.drawBookmark(graphics, placement.chapter, baseHovered, !placement.right, drawX, placement.y);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            if (!this.isOverlayHovered() && this.isBookmarkBackgroundHovered(placement, drawX)) { this.screen.setHoveredChapter(placement.chapter); }
            if (!unlocked) { continue; }
            int hitX = placement.right ? this.mapPaperRight : drawX;
            int hitWidth = placement.right ? drawX + placement.width - this.mapPaperRight : this.mapPaperX - drawX;
            if (hitWidth > 0) { this.screen.addClickable(hitX, placement.y, hitWidth, placement.height, () -> this.screen.openChapter(placement.chapter.id())); }
        }
    }

    private boolean isBookmarkBackgroundHovered(BookmarkPlacement placement, int drawX) {
        BookChapter chapter = placement.chapter;
        boolean mirrored = !placement.right;
        int mirrorWidth = EtericonAtlas.normalBookmarkWidth(chapter);
        int bookmarkWidth = EtericonAtlas.bookmarkContentWidth(chapter);
        int bookmarkHeight = EtericonAtlas.bookmarkContentHeight(chapter);
        int bookmarkX = BookmarkLayout.DEFAULT.bookmarkX();
        if (mirrored) { bookmarkX = mirrorWidth - bookmarkX - bookmarkWidth; }
        int bookmarkAbsX = drawX + bookmarkX;
        int bookmarkAbsY = placement.y + BookmarkLayout.DEFAULT.bookmarkY();
        int visibleX;
        int visibleEnd;
        if (placement.right) {
            visibleX = Math.max(bookmarkAbsX, this.mapPaperRight);
            visibleEnd = bookmarkAbsX + bookmarkWidth;
        } else {
            visibleX = bookmarkAbsX;
            visibleEnd = Math.min(bookmarkAbsX + bookmarkWidth, this.mapPaperX);
        }
        return !(visibleEnd <= visibleX) && this.screen.isInside(this.screen.sceneMouseX(), this.screen.sceneMouseY(), visibleX, bookmarkAbsY, visibleEnd - visibleX, bookmarkHeight);
    }

    private List<List<BookmarkPlacement>> buildBookmarkPages(List<BookChapter> chapters) {
        List<List<BookmarkPlacement>> pages = new ArrayList<>();
        List<BookmarkPlacement> page = new ArrayList<>();
        boolean right = false;
        int y = BookmarkLayout.DEFAULT.bookmarkTop();
        for (BookChapter chapter : chapters) {
            int width = EtericonAtlas.bookmarkWidth(chapter);
            int height = EtericonAtlas.bookmarkHeight(chapter);
            while (true) {
                if (!right) {
                    if (y + height <= BookmarkLayout.DEFAULT.bookmarkBottom()) {
                        page.add(new BookmarkPlacement(chapter, false, this.mapPaperX + BookmarkLayout.DEFAULT.paperOverlap() - width, y, width, height));
                        y += height + BookmarkLayout.DEFAULT.bookmarkGap();
                        break;
                    }
                    right = true;
                    y = BookmarkLayout.DEFAULT.bookmarkTop();
                }
                if (y + height <= BookmarkLayout.DEFAULT.bookmarkBottom()) {
                    page.add(new BookmarkPlacement(chapter, true, this.mapPaperRight - BookmarkLayout.DEFAULT.paperOverlap(), y, width, height));
                    y += height + BookmarkLayout.DEFAULT.bookmarkGap();
                    break;
                }
                pages.add(page);
                page = new ArrayList<>();
                right = false;
                y = BookmarkLayout.DEFAULT.bookmarkTop();
            }
        }
        if (!page.isEmpty()) { pages.add(page); }
        return pages;
    }

    private int pageForBookmark(List<List<BookmarkPlacement>> pages, ResourceLocation id) {
        for (int index = 0; index < pages.size(); index++) {
            for (BookmarkPlacement placement : pages.get(index)) {
                if (placement.chapter.id().equals(id)) { return index; }
            }
        }
        return 0;
    }

    private void drawBookmarkPageArrows(GuiGraphics graphics, int pageCount) {
        graphics.pose().pushPose();
        graphics.pose().translate(0.0F, 0.0F, 150.0F);

        if (pageCount <= 1) { return; }
        int centerX = this.mapPaperX + 136;
        int y = BookmarkLayout.DEFAULT.pageArrowY();
        int arrowWidth = BookmarkLayout.DEFAULT.pageArrowWidth();
        int arrowHeight = BookmarkLayout.DEFAULT.pageArrowHeight();
        if (this.bookmarkPage > 0) {
            int x = centerX + BookmarkLayout.DEFAULT.pageArrowOffsetLeft();
            boolean hovered = !this.isOverlayHovered() && this.screen.isInside(this.screen.sceneMouseX(), this.screen.sceneMouseY(), x, y, arrowWidth, arrowHeight);
            if (hovered) {
//                graphics.fill(x, y, x + arrowWidth, y + arrowHeight, 0x22FFFFFF);
                this.screen.setHoveredButtonTooltip(List.of(Component.translatable("gui.the_end_game.etericon.previous_page")));
            }
            EtericonAtlas.drawArrowLeft(graphics, x, y, hovered, true);
            this.screen.addClickable(x, y, arrowWidth, arrowHeight, () -> {
                if (this.bookmarkPage > 0) {
                    this.bookmarkPage--;
                    this.bookmarkPageInitialized = true;
                }
            });
        }
        if (this.bookmarkPage < pageCount - 1) {
            int x = centerX + BookmarkLayout.DEFAULT.pageArrowOffsetRight();
            boolean hovered = !this.isOverlayHovered() && this.screen.isInside(this.screen.sceneMouseX(), this.screen.sceneMouseY(), x, y, arrowWidth, arrowHeight);
            if (hovered) {
//                graphics.fill(x, y, x + arrowWidth, y + arrowHeight, 0x22FFFFFF);
                this.screen.setHoveredButtonTooltip(List.of(Component.translatable("gui.the_end_game.etericon.next_page")));
            }
            EtericonAtlas.drawArrowRight(graphics, x, y, hovered, true);
            this.screen.addClickable(x, y, arrowWidth, arrowHeight, () -> {
                if (this.bookmarkPage < pageCount - 1) {
                    this.bookmarkPage++;
                    this.bookmarkPageInitialized = true;
                }
            });
        }
        graphics.pose().popPose();
    }

    double mapX(float x) { return this.mapPaperX + 136.0 + x/*(x - 136.0) */* this.zoom + this.panX; }
    double mapY(float y) { return MAP_PAPER_Y + 90.0 + y/*(y - 90.0) */* this.zoom + this.panY; }

    private record BookmarkPlacement(BookChapter chapter, boolean right, int x, int y, int width, int height) {}
}
