package the_end_game.init.item.etericon.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;
import the_end_game.Main;
import the_end_game.init.item.etericon.book.BookCatalog;
import the_end_game.init.item.etericon.book.BookChapter;
import the_end_game.init.item.etericon.book.BookContent;
import the_end_game.init.item.etericon.book.BookElement;
import the_end_game.init.item.etericon.book.BookEntry;
import the_end_game.init.item.etericon.book.BookPage;
import the_end_game.init.item.etericon.book.BookProgress;
import the_end_game.init.item.etericon.book.EntryAccess;
import the_end_game.init.item.etericon.book.EntryState;
import the_end_game.init.item.etericon.book.UnlockCondition;
import the_end_game.init.item.etericon.network.ClickEntryPayload;
import the_end_game.init.item.etericon.network.ConfirmEntryPayload;
import the_end_game.init.item.etericon.client.scene.extra.ExtraScene;
import the_end_game.init.item.etericon.client.scene.book.BookScene;
import the_end_game.init.item.etericon.client.scene.book.EntityPreview;
import the_end_game.init.item.etericon.client.scene.map.MapScene;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public final class EtericonScreen extends Screen {
    private static final Map<UUID, SavedView> SAVED_VIEWS = new HashMap<>();
    private static final int MAP_PAPER_Y = 0;
    public static final int MAP_PAPER_WIDTH = 272;
    private static final int MAP_PAPER_HEIGHT = 180;

    private Mode mode = Mode.MAP;
    private ResourceLocation chapterId;
    private ResourceLocation entryId;
    private EntryState entryState = EntryState.LOCKED;
    private float sceneScale = 1.0F;
    private int sceneX;
    private int sceneY;
    private int sceneMouseX;
    private int sceneMouseY;
    private float partialTick;
    private BookEntry hoveredEntry;
    private BookChapter hoveredChapter;
    private List<Component> hoveredButtonTooltip;
    private boolean viewLoaded;
    private boolean overlayBase;
    private final ExtraScene extraScene = new ExtraScene(this);
    private final BookScene bookScene = new BookScene(this);
    private final MapScene mapScene = new MapScene(this);
    private final List<Clickable> clickables = new ArrayList<>();
    private final Map<BookContent.Multiblock, MultiblockPreviewState> multiblockPreviews = new HashMap<>();

    public EtericonScreen() { super(Component.translatable("gui.the_end_game.etericon.title")); }

    @Override protected void init() {
        super.init();
        EntityPreview.clear();
        if (!this.viewLoaded) {
            this.viewLoaded = true;
            Minecraft minecraft = Minecraft.getInstance();
            SavedView saved = minecraft.player == null ? null : SAVED_VIEWS.get(minecraft.player.getUUID());
            if (saved != null) {
                restoreView(saved);
                this.mapScene.setBookmarkPageInitialized(true);
            }
        }
        if (this.chapterId == null) {
            BookChapter first = firstUnlockedChapter();
            if (first != null) { this.chapterId = first.id(); }
        }
    }

    public BookChapter firstUnlockedChapter() {
        BookCatalog catalog = ClientEtericonData.catalog();
        BookProgress progress = ClientEtericonData.progress();
        for (BookChapter chapter : catalog.sortedChapters()) {
            if (chapter.isUnlocked(catalog, progress)) { return chapter; }
        }
        return null;
    }

    private void restoreView(SavedView saved) {
        BookCatalog catalog = ClientEtericonData.catalog();
        this.mode = saved.mode;
        this.chapterId = saved.chapterId;
        this.entryId = saved.entryId;
        this.entryState = saved.entryState;
        this.bookScene.setSpread(saved.spread);
        this.extraScene.setPage(saved.extraPageId);
        this.mapScene.setView(saved.panX, saved.panY, saved.zoom, saved.bookmarkPage);
        if (this.chapterId == null || catalog.chapter(this.chapterId) == null) {
            this.mode = Mode.MAP;
            this.chapterId = null;
            this.entryId = null;
            this.extraScene.setPage(null);
            this.bookScene.setSpread(0);
        }
        if (this.mode == Mode.BOOK) {
            BookEntry entry = this.entryId == null ? null : catalog.entry(this.entryId);
            if (entry == null) {
                this.mode = Mode.MAP;
                this.entryId = null;
            } else {
                this.chapterId = entry.chapter();
                this.entryState = ClientEtericonData.progress().stateOf(entry);
                List<BookPage> pages = entry.pagesFor(this.entryState);
                this.bookScene.clampSpread(pages.size());
            }
        } else if (this.mode == Mode.EXTRA) {
            if (this.extraScene.pageId() == null || catalog.page(this.extraScene.pageId()) == null) {
                this.mode = Mode.MAP;
                this.extraScene.setPage(null);
            }
        } else {
            this.mode = Mode.MAP;
            this.entryId = null;
            this.extraScene.setPage(null);
        }
    }

    @Override public void removed() {
        super.removed();
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player != null && this.chapterId != null) {
            SAVED_VIEWS.put(minecraft.player.getUUID(), new SavedView(this.mode, this.chapterId, this.entryId, this.entryState, this.bookScene.spread(), this.extraScene.pageId(), this.mapScene.panX(), this.mapScene.panY(), this.mapScene.zoom(), this.mapScene.bookmarkPage()));
        }
    }

    public void openChapter(ResourceLocation id) {
        BookChapter chapter = ClientEtericonData.catalog().chapter(id);
        if (chapter == null || !chapter.isUnlocked(ClientEtericonData.catalog(), ClientEtericonData.progress())) { return; }
        this.chapterId = id;
        this.mode = Mode.MAP;
        this.entryId = null;
        this.extraScene.setPage(null);
        this.mapScene.resetView();
        this.mapScene.unfocusSearch();
    }

    public void openExtra(ResourceLocation id) {
        if (id == null || ClientEtericonData.catalog().page(id) == null) { return; }
        this.extraScene.open(id, this.mode);
        this.mode = Mode.EXTRA;
        closeSearch();
    }

    public void openEntry(ResourceLocation id, EntryState state) {
        BookEntry entry = ClientEtericonData.catalog().entry(id);
        if (entry == null) { return; }
        this.chapterId = entry.chapter();
        this.entryId = id;
        this.entryState = state;
        this.bookScene.setSpread(0);
        this.extraScene.setPage(null);
        this.mode = Mode.BOOK;
        closeSearch();
    }

    public void toggleSearch() {
        if (this.mode != Mode.MAP) {
            this.mode = Mode.MAP;
            this.entryId = null;
            this.extraScene.setPage(null);
            this.mapScene.setBookmarkPageInitialized(false);
        }
        this.mapScene.toggleSearch();
    }

    public void closeSearch() {
        this.mapScene.closeSearch();
    }

    public void toggleSpecial() {
        if (this.mode == Mode.EXTRA) {
            closeExtra();
            return;
        }
        this.mapScene.closeSearch();
        ResourceLocation help = ResourceLocation.fromNamespaceAndPath(Main.MOD_ID, "how_to_use");
        this.extraScene.open(ClientEtericonData.catalog().page(help) != null ? help : null, this.mode);
        this.mode = Mode.EXTRA;
    }

    public void onProgress(BookProgress progress) {
        if (this.mode == Mode.BOOK && this.entryId != null) {
            BookEntry entry = ClientEtericonData.catalog().entry(this.entryId);
            if (entry != null && this.entryState == EntryState.AVAILABLE && progress.stateOf(entry) == EntryState.OPENED) {
                this.entryState = EntryState.OPENED;
                this.bookScene.setSpread(0);
            }
        }
    }

    private boolean usesBookScene() { return this.mode == Mode.BOOK || (this.mode == Mode.EXTRA && this.extraScene.returnMode() == Mode.BOOK); }

    @Override public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        this.partialTick = partialTick;
        float guiScale = Math.max(1.0F, (float) Minecraft.getInstance().getWindow().getGuiScale());
        this.mapScene.ensureLayout();
        float layoutWidth = usesBookScene() ? EtericonAtlas.BOOK_WIDTH : this.mapScene.mapSceneWidth();
        float fitWidth = this.mapScene.mapSceneWidth();
        float sceneHeight = EtericonLayout.SCENE_HEIGHT;
        float scale = Math.min(EtericonLayout.DEFAULT_SCENE_SCALE, Math.min((this.width - 12.0F) / fitWidth, (this.height - 12.0F) / sceneHeight));
//        scale = Math.min(scale, Math.min(1000.0F / (fitWidth * guiScale), 680.0F / (sceneHeight * guiScale)));
        this.sceneScale = Math.max(EtericonLayout.MIN_SCENE_SCALE, scale);
        this.sceneX = Math.round((this.width - layoutWidth * this.sceneScale) / 2.0F);
        this.sceneY = Math.round((this.height - sceneHeight * this.sceneScale) / 2.0F + EtericonLayout.SCENE_TOP_OVERHANG * this.sceneScale);

        this.sceneMouseX = (int) Math.floor((mouseX - this.sceneX) / this.sceneScale);
        this.sceneMouseY = (int) Math.floor((mouseY - this.sceneY) / this.sceneScale);
        this.clickables.clear();
        this.hoveredEntry = null;
        this.hoveredChapter = null;
        this.hoveredButtonTooltip = null;
        graphics.pose().pushPose();
        graphics.pose().translate(this.sceneX, this.sceneY, 0.0F);
        graphics.pose().scale(this.sceneScale, this.sceneScale, 1.0F);
        if (this.mode == Mode.EXTRA) { this.extraScene.drawBase(graphics); }
        else {
            switch (this.mode) {
                case MAP -> drawMap(graphics);
                case BOOK -> drawBook(graphics);
                default -> { }
            }
        }
        graphics.pose().popPose();

        if (this.mode == Mode.EXTRA) {
            graphics.pose().pushPose();
            graphics.pose().translate(0.0F, 0.0F, 100.0F);
            graphics.fill(0, 0, this.width, this.height, 0x99000000);
            graphics.pose().popPose();
        }

//        graphics.pose().pushPose();
//        graphics.pose().translate(this.sceneX, this.sceneY, 0.0F);
//        graphics.pose().scale(this.sceneScale, this.sceneScale, 1.0F);
        if (this.mode == Mode.EXTRA) { this.extraScene.drawPage(graphics); }
        int tooltipWidth = usesBookScene() ? EtericonAtlas.BOOK_WIDTH : this.mapScene.mapSceneWidth();
        if (this.hoveredEntry != null) {
            EtericonTooltip.draw(graphics, this.font, this.sceneMouseX, this.sceneMouseY, buildEntryTooltip(this.hoveredEntry), tooltipWidth, EtericonAtlas.BOOK_HEIGHT);
        } else if (this.hoveredChapter != null) {
            List<Component> lines = new ArrayList<>();
            lines.add(this.hoveredChapter.title().copy().withStyle(ChatFormatting.BOLD));
            if (!this.hoveredChapter.subtitle().getString().isEmpty()) { lines.add(this.hoveredChapter.subtitle()); }
            BookCatalog catalog = ClientEtericonData.catalog();
            BookProgress chapterProgress = ClientEtericonData.progress();
            if (!this.hoveredChapter.isUnlocked(catalog, chapterProgress)) { lines.add(Component.translatable("gui.the_end_game.etericon.chapter_locked").withStyle(ChatFormatting.RED)); }
            lines.add(Component.literal("------------------").withStyle(ChatFormatting.DARK_GRAY));
            lines.add(Component.translatable("gui.the_end_game.etericon.chapter_progress", EntryAccess.chapterPercent(this.hoveredChapter, catalog, chapterProgress) + "%").withStyle(ChatFormatting.GRAY));
            EtericonTooltip.draw(graphics, this.font, this.sceneMouseX, this.sceneMouseY, lines, tooltipWidth, EtericonAtlas.BOOK_HEIGHT, EntryAccess.chapterFraction(this.hoveredChapter, catalog, chapterProgress));
        } else if (this.hoveredButtonTooltip != null) {
            EtericonTooltip.draw(graphics, this.font, this.sceneMouseX, this.sceneMouseY, this.hoveredButtonTooltip, -EtericonLayout.SCENE_TOP_OVERHANG, tooltipWidth, EtericonLayout.SCENE_HEIGHT);
        }
//        graphics.pose().popPose();
    }

    @Override public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) { graphics.fill(0, 0, this.width, this.height, 0xC0100818); }

    public void setOverlayBase(boolean value) { this.overlayBase = value; }

    public void clearInteraction() {
        this.clickables.clear();
        this.hoveredEntry = null;
        this.hoveredChapter = null;
        this.hoveredButtonTooltip = null;
    }

    public int mapPaperX() { return this.mapScene.mapPaperX(); }

    public ResourceLocation chapterId() { return this.chapterId; }

    public void setChapterId(ResourceLocation id) { this.chapterId = id; }

    public void setHoveredEntry(BookEntry entry) { this.hoveredEntry = entry; }

    public void setHoveredChapter(BookChapter chapter) { this.hoveredChapter = chapter; }

    public net.minecraft.client.gui.Font font() { return this.font; }

    public float partialTick() { return this.partialTick; }

    public ResourceLocation entryId() { return this.entryId; }

    public EntryState entryState() { return this.entryState; }

    public boolean overlayBase() { return this.overlayBase; }

    public int sceneMouseX() { return this.sceneMouseX; }

    public int sceneMouseY() { return this.sceneMouseY; }

    public void setHoveredButtonTooltip(List<Component> tooltip) { this.hoveredButtonTooltip = tooltip; }

    public void addClickable(int x, int y, int width, int height, Runnable action) { this.clickables.add(new Clickable(x, y, width, height, action)); }

    public void drawStaticTab(GuiGraphics graphics, int type, int x, int y, boolean up, boolean hovered) {
        graphics.pose().pushPose();
        if (hovered) {
            if (up) { y -= 3; }
            else { y += 3; }
        }
        if (up) {
            graphics.pose().translate(x, y + EtericonLayout.STATIC_TAB_HEIGHT, 0.0F);
            graphics.pose().mulPose(Axis.ZP.rotationDegrees(-90.0F));
        } else {
            graphics.pose().translate(x + EtericonLayout.STATIC_TAB_WIDTH, y, 0.0F);
            graphics.pose().mulPose(Axis.ZP.rotationDegrees(90.0F));
        }
        EtericonAtlas.drawBookmarkBackground(graphics, type, 0, 0, EtericonLayout.STATIC_TAB_HEIGHT, EtericonLayout.STATIC_TAB_WIDTH);
        graphics.pose().popPose();

        if (up) { EtericonAtlas.drawAtlasIcon(graphics, 468, 470, 11, 11, x + 2, y + 6, 9, 9); }
        else { EtericonAtlas.drawAtlasIcon(graphics, 408, 419, 36, 36, x + 2, y + EtericonLayout.STATIC_TAB_HEIGHT - 16, 10, 10); }
    }

    private record SavedView(Mode mode, ResourceLocation chapterId, ResourceLocation entryId, EntryState entryState, int spread, ResourceLocation extraPageId, double panX, double panY, double zoom, int bookmarkPage) {}

    public void drawMap(GuiGraphics graphics) {
        this.mapScene.draw(graphics);
    }

    public void drawBook(GuiGraphics graphics) {
        this.bookScene.draw(graphics);
    }

    public void drawBackButton(GuiGraphics graphics, int x, Runnable action) { drawBackButton(graphics, x, 1, action); }

    public void drawBackButton(GuiGraphics graphics, int x, int y, Runnable action) {
        boolean hovered = isInside(this.sceneMouseX, this.sceneMouseY, x, y, 18, 12);
        if (hovered) {
//            graphics.fill(x, y, x + 18, y + 12, 0x22FFFFFF);
            this.hoveredButtonTooltip = List.of(Component.translatable("gui.the_end_game.etericon.back"));
        }
        EtericonAtlas.drawBackArrow(graphics, x, y, hovered);
        addClickable(x, y, 18, 12, action);
    }

    public int drawContent(GuiGraphics graphics, BookContent content, int x, int y, int availableWidth, Integer width, Integer height) {
        if (content instanceof BookContent.Title title) {
            int textWidth = this.font.width(title.text());
            float scale = title.scale();
            if (textWidth > 0 && textWidth * scale > availableWidth) { scale = availableWidth / (float) textWidth; }
            int drawX = title.center() ? x + (availableWidth - (int) (textWidth * scale)) / 2 : x;
            if (title.center()) {
                int lineY = y + Math.max(3, Math.round(9.0F * scale / 2.0F));
                int leftEnd = drawX - 6;
                int rightStart = drawX + (int) (textWidth * scale) + 6;
                if (leftEnd > x + 4) { graphics.fill(x + 4, lineY, leftEnd, lineY + 1, 0x552A1A0E); }
                if (rightStart < x + availableWidth - 4) { graphics.fill(rightStart, lineY, x + availableWidth - 4, lineY + 1, 0x552A1A0E); }
            }
            drawScaledText(graphics, title.text(), drawX, y, scale, title.color(), false);
            return Math.max(10, Math.round(9.0F * scale) + 4);
        }
        if (content instanceof BookContent.Text text) { return drawTextBlock(graphics, text.text(), x, y, width == null ? availableWidth : width, text.scale(), text.color(), text.shadow()); }
        if (content instanceof BookContent.TextTitle textTitle) {
            int textWidth = this.font.width(textTitle.title());
            float titleScale = 0.75F;
            if (textWidth > 0 && textWidth * titleScale > availableWidth) { titleScale = availableWidth / (float) textWidth; }
            drawScaledText(graphics, textTitle.title().copy().withStyle(ChatFormatting.BOLD), x, y, titleScale, 0xFF2A1A0E, false);
            int titleHeight = Math.round(9.0F * titleScale) + 3;
            return titleHeight + drawTextBlock(graphics, textTitle.text(), x, y + titleHeight, width == null ? availableWidth : width, textTitle.scale(), 0xFF3B2A1A, false);
        }
        if (content instanceof BookContent.Image image) {
            int imageWidth = width == null ? image.icon().width() : width;
            int imageHeight = height == null ? image.icon().height() : height;
            graphics.fill(x - 1, y - 1, x + imageWidth + 1, y + imageHeight + 1, 0x553B2A1A);
            EtericonAtlas.drawIcon(graphics, image.icon(), x, y, imageWidth, imageHeight);
            int result = imageHeight;
            if (!image.caption().getString().isEmpty()) {
                drawCenteredIn(graphics, image.caption(), x, imageWidth, y + imageHeight + 2, 0.6F, 0xFF5A4A3A);
                result += 10;
            }
            return result;
        }
        if (content instanceof BookContent.ImageText imageText) { return drawImageText(graphics, imageText, x, y, availableWidth); }
        if (content instanceof BookContent.Container container) { return drawContainer(graphics, container, x, y, availableWidth); }
        if (content instanceof BookContent.Crafting crafting) { return drawCrafting(graphics, crafting, x, y); }
        if (content instanceof BookContent.RecipeLink recipe) { return drawRecipe(graphics, recipe, x, y); }
        if (content instanceof BookContent.Multiblock multiblock) { return drawMultiblock(graphics, multiblock, x, y, availableWidth); }
        if (content instanceof BookContent.Entity entity) {
            int boxWidth = width == null ? Math.min(availableWidth, 92) : Math.min(width, availableWidth);
            int boxHeight = height == null ? 64 : height;
            graphics.fill(x, y, x + boxWidth, y + boxHeight, 0x33FFFFFF);
            drawBorder(graphics, x, y, boxWidth, boxHeight, 0x663B2A1A);
            EntityPreview.draw(graphics, entity.entity(), x + 1, y + 1, boxWidth - 2, boxHeight - 2, this.partialTick, entity.rotate());
            int result = boxHeight;
            if (!entity.caption().getString().isEmpty()) {
                drawCenteredIn(graphics, entity.caption(), x, boxWidth, y + boxHeight + 2, 0.6F, 0xFF5A4A3A);
                result += 10;
            }
            return result;
        }
        if (content instanceof BookContent.Item item) {
            int size = Math.max(8, Math.round(16.0F * item.scale()));
            drawStack(graphics, item.stack(), x, y, size);
            Component name = item.stack().getHoverName();
            drawScaledText(graphics, name, x + size + 4, y + Math.max(0, (size - 9) / 2), 0.65F, 0xFF3B2A1A, false);
            if (item.stack().getCount() > 1) {
                drawScaledText(graphics, Component.literal("x" + item.stack().getCount()), x + size + 4, y + Math.max(0, (size - 9) / 2) + 8, 0.55F, 0xFF5A4A3A, false);
            }
            int result = Math.max(size, 12);
            if (!item.caption().getString().isEmpty()) {
                drawScaledText(graphics, item.caption(), x, y + result + 1, 0.6F, 0xFF5A4A3A, false);
                result += 9;
            }
            return result;
        }
        if (content instanceof BookContent.Link link) {
            float scale = 0.65F;
            int textWidth = (int) (this.font.width(link.text()) * scale);
            boolean hovered = isInside(this.sceneMouseX, this.sceneMouseY, x, y, Math.max(1, textWidth), 9);
            int color = hovered ? 0xFFA45CFF : 0xFF7B2FBE;
            drawScaledText(graphics, link.text(), x, y, scale, color, false);
            if (hovered) { graphics.fill(x, y + 8, x + textWidth, y + 9, color); }
            addClickable(x, y, Math.max(1, textWidth), 9, () -> openExtra(link.target()));
            return 9;
        }
        return 0;
    }

    private int drawTextBlock(GuiGraphics graphics, Component text, int x, int y, int width, float scale, int color, boolean shadow) {
        int wrapWidth = Math.max(8, (int) (width / scale));
        List<FormattedCharSequence> lines = this.font.split(text, wrapWidth);
        if (lines.isEmpty()) { return 0; }
        graphics.pose().pushPose();
        graphics.pose().translate(x, y, 0.0F);
        graphics.pose().scale(scale, scale, 1.0F);
        for (int index = 0; index < lines.size(); index++) { graphics.drawString(this.font, lines.get(index), 0, index * 9, color, shadow); }
        graphics.pose().popPose();
        return lines.size() * Math.max(6, Math.round(9.0F * scale)) + 1;
    }

    private int drawImageText(GuiGraphics graphics, BookContent.ImageText content, int x, int y, int availableWidth) {
        int imageWidth = Math.min(content.imageWidth(), Math.max(8, availableWidth - 20));
        int imageHeight = Math.max(8, Math.round(content.icon().height() * imageWidth / (float) Math.max(1, content.icon().width())));
        int textWidth = Math.max(8, availableWidth - imageWidth - 4);
        int textHeight = drawTextBlock(graphics, content.text(), x + (content.imageLeft() ? imageWidth + 4 : 0), y, textWidth, content.scale(), 0xFF3B2A1A, false);
        int imageX = content.imageLeft() ? x : x + availableWidth - imageWidth;
        EtericonAtlas.drawIcon(graphics, content.icon(), imageX, y, imageWidth, imageHeight);
        int result = Math.max(imageHeight, textHeight);
        if (!content.caption().getString().isEmpty()) {
            drawCenteredIn(graphics, content.caption(), x, availableWidth, y + result + 2, 0.6F, 0xFF5A4A3A);
            result += 10;
        }
        return result;
    }

    private int drawContainer(GuiGraphics graphics, BookContent.Container container, int x, int y, int availableWidth) {
        int cursor = 0;
        int cross = 0;
        int count = container.children().size();
        for (int index = 0; index < count; index++) {
            BookElement child = container.children().get(index);
            int remaining = count - index;
            int childWidth = child.width() != null ? Math.min(child.width(), availableWidth) : container.horizontal()
                    ? Math.max(1, (availableWidth - cursor - container.gap() * Math.max(0, remaining - 1)) / remaining)
                    : availableWidth;
            int childX = x + (child.x() != null ? child.x() : container.horizontal() ? cursor : 0);
            int childY = y + (child.y() != null ? child.y() : container.horizontal() ? 0 : cursor);
            int height = drawContent(graphics, child.content(), childX, childY, childWidth, child.width(), child.height());
            if (container.horizontal()) {
                cursor += childWidth + container.gap();
                cross = Math.max(cross, height);
            } else {
                cursor += height + child.gap();
                cross = Math.max(cross, childWidth);
            }
        }
        return container.horizontal() ? cross : Math.max(0, cursor - container.gap());
    }

    private int drawRecipe(GuiGraphics graphics, BookContent.RecipeLink link, int x, int y) {
        List<ItemStack> inputs = new ArrayList<>();
        ItemStack result = link.result().copy();
        Minecraft minecraft = Minecraft.getInstance();
        if (link.recipe() != null && minecraft.level != null) {
            var holder = minecraft.level.getRecipeManager().byKey(link.recipe()).orElse(null);
            Recipe<?> recipe = holder == null ? null : holder.value();
            if (recipe != null) {
                try {
                    for (Ingredient ingredient : recipe.getIngredients()) {
                        ItemStack[] items = ingredient.getItems();
                        inputs.add(items.length == 0 ? ItemStack.EMPTY : items[0]);
                    }
                    result = recipe.getResultItem(minecraft.level.registryAccess());
                } catch (Exception ignored) {
                    inputs.clear();
                }
            }
        }
        if (inputs.isEmpty()) {
            return drawCrafting(graphics, new BookContent.Crafting(link.grid(), result, link.caption()), x, y);
        }
        if (inputs.size() > 3) {
            List<ItemStack> grid = new ArrayList<>();
            for (int index = 0; index < 9; index++) { grid.add(index < inputs.size() ? inputs.get(index) : ItemStack.EMPTY); }
            return drawCrafting(graphics, new BookContent.Crafting(grid, result, link.caption()), x, y);
        }
        int slot = 18;
        int step = slot + 2;
        for (int index = 0; index < inputs.size() && index < 3; index++) {
            drawRecipeSlot(graphics, inputs.get(index), x + index * step, y);
        }
        int arrowX = x + Math.min(3, inputs.size()) * step + 2;
        drawScaledText(graphics, Component.literal("Р Р†РІР‚В РІР‚в„ў"), arrowX, y + 5, 0.8F, 0xFF5A4A3A, false);
        int resultX = arrowX + 14;
        drawRecipeSlot(graphics, result, resultX, y);
        int height = slot + (link.caption().getString().isEmpty() ? 0 : 9);
        if (!link.caption().getString().isEmpty()) {
            drawScaledText(graphics, link.caption(), x, y + slot, 0.6F, 0xFF5A4A3A, false);
        }
        return height;
    }

    private void drawRecipeSlot(GuiGraphics graphics, ItemStack stack, int x, int y) {
        graphics.fill(x, y, x + 18, y + 18, 0x44FFFFFF);
        drawBorder(graphics, x, y, 18, 18, 0x803B2A1A);
        if (!stack.isEmpty()) { drawStack(graphics, stack, x + 1, y + 1, 16); }
    }

    private int drawMultiblock(GuiGraphics graphics, BookContent.Multiblock content, int x, int y, int availableWidth) {
        MultiblockPreviewState state = multiblockPreview(content);
        if (state == null) {
            drawScaledText(graphics, Component.translatable("gui.the_end_game.etericon.multiblock.invalid"), x, y, 0.55F, 0xFF8A2A2A, false);
            return 10;
        }
        the_end_game.init.item.etericon.multiblock.Multiblock multiblock = state.multiblock;
        int displayWidth = multiblock.getRotatedWidth(state.rotation);
        int displayDepth = multiblock.getRotatedDepth(state.rotation);
        int cell = Math.max(4, Math.min(10, (availableWidth - 2) / Math.max(1, Math.max(displayWidth, displayDepth))));
        int gridWidth = displayWidth * cell;
        int gridHeight = displayDepth * cell;
        graphics.fill(x, y, x + gridWidth, y + gridHeight, 0x33FFFFFF);
        drawBorder(graphics, x, y, gridWidth, gridHeight, 0x803B2A1A);
        char[][] display = new char[displayWidth][displayDepth];
        Mirror mirror = state.mirrored ? Mirror.LEFT_RIGHT : Mirror.NONE;
        for (int sourceX = 0; sourceX < multiblock.getWidth(); sourceX++) {
            for (int sourceZ = 0; sourceZ < multiblock.getDepth(); sourceZ++) {
                int[] transformed = multiblock.transform(sourceX, sourceZ, state.rotation, mirror);
                display[transformed[0]][transformed[1]] = multiblock.getChar(sourceX, multiblock.getHeight() - 1 - state.layer, sourceZ);
            }
        }
        for (int displayX = 0; displayX < displayWidth; displayX++) {
            for (int displayZ = 0; displayZ < displayDepth; displayZ++) {
                char symbol = display[displayX][displayZ];
                int cellX = x + displayX * cell;
                int cellY = y + displayZ * cell;
                if (symbol == content.anchor()) { drawBorder(graphics, cellX, cellY, cell, cell, 0xFFD8A020); }
                ResourceLocation blockId = content.symbols().get(symbol);
                if (blockId == null) { continue; }
                net.minecraft.world.level.block.Block block = net.minecraft.core.registries.BuiltInRegistries.BLOCK.get(blockId);
                if (block == net.minecraft.world.level.block.Blocks.AIR) { continue; }
                drawStack(graphics, new ItemStack(block), cellX + 1, cellY + 1, Math.max(2, cell - 2));
            }
        }
        int controlY = y + gridHeight + 2;
        int controlX = x;
        drawPreviewButton(graphics, "<", controlX, controlY, () -> state.layer = Math.max(0, state.layer - 1));
        controlX += 11;
        drawScaledText(graphics, Component.literal((state.layer + 1) + "/" + multiblock.getHeight()), controlX, controlY + 1, 0.45F, 0xFF3B2A1A, false);
        controlX += 20;
        drawPreviewButton(graphics, ">", controlX, controlY, () -> state.layer = Math.min(multiblock.getHeight() - 1, state.layer + 1));
        controlX += 11;
        drawPreviewButton(graphics, "R", controlX, controlY, () -> state.rotation = state.rotation.getRotated(Rotation.CLOCKWISE_90));
        controlX += 11;
        drawPreviewButton(graphics, "M", controlX, controlY, () -> state.mirrored = !state.mirrored);
        return gridHeight + 14;
    }

    private void drawPreviewButton(GuiGraphics graphics, String label, int x, int y, Runnable action) {
        graphics.fill(x, y, x + 10, y + 9, 0x44FFFFFF);
        drawBorder(graphics, x, y, 10, 9, 0x803B2A1A);
        drawScaledText(graphics, Component.literal(label), x + 2, y + 1, 0.55F, 0xFF3B2A1A, false);
        addClickable(x, y, 10, 9, action);
    }

    private MultiblockPreviewState multiblockPreview(BookContent.Multiblock content) {
        MultiblockPreviewState state = this.multiblockPreviews.get(content);
        if (state == null) {
            try {
                state = new MultiblockPreviewState(content.toMultiblock());
                this.multiblockPreviews.put(content, state);
            } catch (RuntimeException exception) {
                return null;
            }
        }
        return state;
    }

    private int[] multiblockSize(BookContent.Multiblock content, int width) {
        int max = 1;
        for (List<String> layer : content.layers()) {
            for (String row : layer) { max = Math.max(max, row.length()); }
            max = Math.max(max, layer.size());
        }
        int cell = Math.max(4, Math.min(10, (width - 2) / max));
        return new int[]{cell, max};
    }

    private static final class MultiblockPreviewState {
        private final the_end_game.init.item.etericon.multiblock.Multiblock multiblock;
        private int layer;
        private Rotation rotation = Rotation.NONE;
        private boolean mirrored;

        private MultiblockPreviewState(the_end_game.init.item.etericon.multiblock.Multiblock multiblock) {
            this.multiblock = multiblock;
        }
    }

    private int drawCrafting(GuiGraphics graphics, BookContent.Crafting crafting, int x, int y) {
        int slot = 18;
        int gap = 1;
        for (int index = 0; index < 9; index++) {
            int slotX = x + (index % 3) * (slot + gap);
            int slotY = y + (index / 3) * (slot + gap);
            graphics.fill(slotX, slotY, slotX + slot, slotY + slot, 0x44FFFFFF);
            drawBorder(graphics, slotX, slotY, slot, slot, 0x803B2A1A);
            ItemStack stack = crafting.grid().get(index);
            if (!stack.isEmpty()) { drawStack(graphics, stack, slotX + 1, slotY + 1, 16); }
        }
        int arrowX = x + 60;
        drawScaledText(graphics, Component.literal("Р Р†РІР‚В РІР‚в„ў"), arrowX, y + 22, 0.8F, 0xFF5A4A3A, false);
        int resultX = x + 72;
        int resultY = y + 19;
        graphics.fill(resultX, resultY, resultX + 20, resultY + 20, 0x55FFFFFF);
        drawBorder(graphics, resultX, resultY, 20, 20, 0x803B2A1A);
        if (!crafting.result().isEmpty()) { drawStack(graphics, crafting.result(), resultX + 2, resultY + 2, 16); }
        int result = 58;
        if (!crafting.caption().getString().isEmpty()) {
            drawScaledText(graphics, crafting.caption(), x, y + 58, 0.6F, 0xFF5A4A3A, false);
            result += 9;
        }
        return result;
    }

    public void drawStack(GuiGraphics graphics, ItemStack stack, int x, int y, int size) {
        ItemIconRenderer.draw(graphics, stack, x, y, size, size);
    }

    private List<Component> buildEntryTooltip(BookEntry entry) {
        BookProgress progress = ClientEtericonData.progress();
        EntryState state = progress.stateOf(entry);
        List<Component> lines = new ArrayList<>();
        lines.add(entry.title().copy().withStyle(ChatFormatting.BOLD));
        ChatFormatting stateColor = switch (state) {
            case LOCKED -> ChatFormatting.GRAY;
            case AVAILABLE -> ChatFormatting.GREEN;
            case OPENED -> ChatFormatting.DARK_PURPLE;
        };
        lines.add(Component.translatable("gui.the_end_game.etericon.state." + state.getSerializedName()).withStyle(stateColor));
        if (!entry.tooltip().getString().isEmpty()) { lines.add(entry.tooltip()); }
        if (!entry.requires().isEmpty()) {
            lines.add(Component.translatable("gui.the_end_game.etericon.requires").withStyle(ChatFormatting.BOLD));
            for (ResourceLocation required : entry.requires()) {
                BookEntry parent = ClientEtericonData.catalog().entry(required);
                if (parent == null) {
                    lines.add(Component.literal("Р Р†Р вЂљРЎС› " + required).withStyle(ChatFormatting.GRAY));
                    continue;
                }
                EntryState parentState = progress.stateOf(parent);
                ChatFormatting color = parentState == EntryState.OPENED ? ChatFormatting.GREEN : ChatFormatting.RED;
                lines.add(Component.literal("Р Р†Р вЂљРЎС› ").append(parent.title()).append(Component.literal(" Р Р†Р вЂљРІР‚Сњ ")).append(Component.translatable("gui.the_end_game.etericon.state." + parentState.getSerializedName())).withStyle(color));
            }
        }
        if (!entry.unlocks().isEmpty()) {
            lines.add(Component.translatable("gui.the_end_game.etericon.unlock").withStyle(ChatFormatting.BOLD));
            for (UnlockCondition condition : entry.unlocks()) { lines.add(unlockLine(condition, progress)); }
        }
        if (state == EntryState.AVAILABLE && entry.hasStages()) {
            int stageIndex = EntryAccess.stageIndex(entry, progress);
            if (stageIndex < entry.stages().size()) {
                lines.add(Component.translatable("gui.the_end_game.etericon.stage", stageIndex + 1, entry.stages().size()).withStyle(ChatFormatting.BOLD));
                for (UnlockCondition condition : entry.stages().get(stageIndex)) { lines.add(unlockLine(condition, progress)); }
            }
        }
        UnlockCondition.Research research = progress.findResearch(entry);
        if (state == EntryState.LOCKED && progress.requirementsMet(entry)) {
            UnlockCondition.Research requirement = researchRequirement(entry);
            if (research != null) {
                lines.add(Component.translatable("gui.the_end_game.etericon.click_invest", research.points()).withStyle(ChatFormatting.GOLD));
            } else if (requirement != null) {
                lines.add(Component.translatable("gui.the_end_game.etericon.research_missing", progress.researchPoints(), requirement.points()).withStyle(ChatFormatting.RED));
            }
        } else if (state == EntryState.AVAILABLE) {
            lines.add(Component.translatable("gui.the_end_game.etericon.click_read").withStyle(ChatFormatting.GREEN));
        } else if (state == EntryState.OPENED) {
            lines.add(Component.translatable("gui.the_end_game.etericon.click_open").withStyle(ChatFormatting.GRAY));
        }
        return lines;
    }

    private UnlockCondition.Research researchRequirement(BookEntry entry) {
        for (UnlockCondition condition : entry.unlocks()) {
            if (condition instanceof UnlockCondition.Research research) { return research; }
        }
        return null;
    }

    private Component unlockLine(UnlockCondition condition, BookProgress progress) {
        boolean satisfied = condition.isSatisfied(progress);
        Component prefix = Component.literal(satisfied ? "Р Р†РЎС™РІР‚Сљ " : "Р Р†РЎС™РІР‚вЂќ ").withStyle(satisfied ? ChatFormatting.GREEN : ChatFormatting.RED);
        if (condition instanceof UnlockCondition.Research research) {
            boolean affordable = progress.researchPoints() >= research.points();
            prefix = Component.literal("Р Р†РІР‚вЂќРІР‚В  ").withStyle(affordable ? ChatFormatting.GOLD : ChatFormatting.GRAY);
            Component line = Component.translatable("gui.the_end_game.etericon.unlock.research", research.points());
            return Component.empty().append(prefix).append(line.copy().withStyle(affordable ? ChatFormatting.GOLD : ChatFormatting.GRAY));
        }
        if (condition instanceof UnlockCondition.Theory theory) {
            return Component.empty().append(prefix).append(Component.translatable("gui.the_end_game.etericon.unlock.theory", theory.theory().toString()));
        }
        if (condition instanceof UnlockCondition.Craft craft) {
            return Component.empty().append(prefix).append(Component.translatable("gui.the_end_game.etericon.unlock.craft", targetName(craft.item())));
        }
        if (condition instanceof UnlockCondition.Study study) {
            return Component.empty().append(prefix).append(Component.translatable("gui.the_end_game.etericon.unlock.study", targetName(study.target())));
        }
        if (condition instanceof UnlockCondition.Kill kill) {
            int current = progress.kills().getOrDefault(kill.entity(), 0);
            return Component.empty().append(prefix).append(Component.translatable("gui.the_end_game.etericon.unlock.kill", targetName(kill.entity()), current, kill.count()));
        }
        return Component.empty();
    }

    private Component targetName(ResourceLocation id) {
        Optional<EntityType<?>> entity = BuiltInRegistries.ENTITY_TYPE.getOptional(id);
        if (entity.isPresent()) { return entity.get().getDescription(); }
        return BuiltInRegistries.ITEM.getOptional(id).<Component>map(item -> new ItemStack(item).getHoverName()).orElse(Component.literal(id.toString()));
    }

    public void prepareSearchResult(ResourceLocation chapter) {
        this.chapterId = chapter;
        this.entryId = null;
        this.extraScene.setPage(null);
        this.bookScene.setSpread(0);
    }

    public void requestEntry(ResourceLocation id) {
        closeSearch();
        PacketDistributor.sendToServer(new ClickEntryPayload(id));
    }

    public void confirmEntry() {
        closeSearch();
        if (this.entryId != null) {
            PacketDistributor.sendToServer(new ConfirmEntryPayload(this.entryId));
        }
    }

    public void backToMap() {
        this.mode = Mode.MAP;
        this.entryId = null;
        this.extraScene.setPage(null);
        this.bookScene.setSpread(0);
        this.mapScene.setBookmarkPageInitialized(false);
    }

    public void closeExtra() {
        Mode returnMode = this.extraScene.returnMode();
        this.extraScene.clear();
        this.mode = returnMode;
        if (returnMode == Mode.MAP) {
            this.entryId = null;
        }
        closeSearch();
    }

    public void drawCentered(GuiGraphics graphics, Component text, int centerX, int y, float scale, int color) {
        int width = this.font.width(text);
        drawScaledText(graphics, text, centerX - (int) (width * scale / 2.0F), y, scale, color, false);
    }

    private void drawCenteredIn(GuiGraphics graphics, Component text, int x, int width, int y, float scale, int color) {
        int textWidth = this.font.width(text);
        drawScaledText(graphics, text, x + (width - (int) (textWidth * scale)) / 2, y, scale, color, false);
    }

    public void drawScaledText(GuiGraphics graphics, Component text, int x, int y, float scale, int color, boolean shadow) {
        graphics.pose().pushPose();
        graphics.pose().translate(x, y, 0.0F);
        graphics.pose().scale(scale, scale, 1.0F);
        graphics.drawString(this.font, text, 0, 0, color, shadow);
        graphics.pose().popPose();
    }

    public void drawBorder(GuiGraphics graphics, int x, int y, int width, int height, int color) {
        graphics.fill(x, y, x + width, y + 1, color);
        graphics.fill(x, y + height - 1, x + width, y + height, color);
        graphics.fill(x, y, x + 1, y + height, color);
        graphics.fill(x + width - 1, y, x + width, y + height, color);
    }

    private void drawCurve(GuiGraphics graphics, int x1, int y1, int x2, int y2, EntryState state1, EntryState state2) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        double length = Math.sqrt(dx * dx + dy * dy);
        if (length < 0.001) { return; }
        int color;
        int edgeColor;
        double amplitude;
        double waves;
        double speed;
        double radius;
        double edgeRadius;
        boolean bothOpened = state1 == EntryState.OPENED && state2 == EntryState.OPENED;
        boolean openedAndAvailable = (state1 == EntryState.OPENED && state2 == EntryState.AVAILABLE) || (state1 == EntryState.AVAILABLE && state2 == EntryState.OPENED);
        if (bothOpened) {
            color = 0x402b0202;
            edgeColor = 0x05FF0000;
            amplitude = 2.6;
            waves = 2.2;
            speed = 0.0022;
            radius = 1.3;
            edgeRadius = 2.4;
        } else if (openedAndAvailable) {
            color = 0x40614848;
            edgeColor = 0x05FF0000;
            amplitude = 2.2;
            waves = 2.0;
            speed = 0.0011;
            radius = 1.1;
            edgeRadius = 1.9;
        } else {
            color = 0x706E6E6E;
            edgeColor = 0x306E6E6E;
            amplitude = 0.0;
            waves = 0.0;
            speed = 0.0;
            radius = 0.8;
            edgeRadius = 1.2;
        }
        double normalX = -dy / length;
        double normalY = dx / length;
        double baseArc = Math.min(7.0, length * 0.06);
        double phase = System.currentTimeMillis() * speed;
        int steps = 256;
        for (int index = 0; index <= steps; index++) {
            double t = index / (double) steps;
            double envelope = Math.sin(Math.PI * t);
            double wave = Math.sin(t * Math.PI * 2.0 * waves - phase) * amplitude * envelope;
            double offset = envelope * baseArc + wave;
            double x = x1 + dx * t + normalX * offset;
            double y = y1 + dy * t + normalY * offset;
            drawSoftDot(graphics, x, y, radius, color, edgeColor, edgeRadius);
        }
    }

    private void drawSoftDot(GuiGraphics graphics, double x, double y, double radius, int color, int edgeColor, double edgeRadius) {
        int minX = (int) Math.floor(x - edgeRadius);
        int maxX = (int) Math.ceil(x + edgeRadius);
        int minY = (int) Math.floor(y - edgeRadius);
        int maxY = (int) Math.ceil(y + edgeRadius);
        for (int pixelX = minX; pixelX <= maxX; pixelX++) {
            for (int pixelY = minY; pixelY <= maxY; pixelY++) {
                double dotX = pixelX + 0.5 - x;
                double dotY = pixelY + 0.5 - y;
                double distance = Math.sqrt(dotX * dotX + dotY * dotY);
                if (distance <= radius) { graphics.fill(pixelX, pixelY, pixelX + 1, pixelY + 1, color); }
                else if (distance <= edgeRadius) { graphics.fill(pixelX, pixelY, pixelX + 1, pixelY + 1, edgeColor); }
            }
        }
    }

    public void enableScissor(GuiGraphics graphics, int x, int y, int width, int height) {
        int x1 = (int) Math.floor(this.sceneX + x * this.sceneScale);
        int y1 = (int) Math.floor(this.sceneY + y * this.sceneScale);
        int x2 = (int) Math.ceil(this.sceneX + (x + width) * this.sceneScale);
        int y2 = (int) Math.ceil(this.sceneY + (y + height) * this.sceneScale);
        graphics.enableScissor(x1, y1, x2, y2);
    }

    public void disableScissor(GuiGraphics graphics) { graphics.disableScissor(); }

    public boolean isInside(int mouseX, int mouseY, int x, int y, int width, int height) { return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height; }

    @Override public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) { return super.mouseClicked(mouseX, mouseY, button); }
        int mx = (int) Math.floor((mouseX - this.sceneX) / this.sceneScale);
        int my = (int) Math.floor((mouseY - this.sceneY) / this.sceneScale);
        for (int index = this.clickables.size() - 1; index >= 0; index--) {
            Clickable clickable = this.clickables.get(index);
            if (isInside(mx, my, clickable.x, clickable.y, clickable.width, clickable.height)) {
                clickable.action.run();
                return true;
            }
        }
        this.mapScene.unfocusSearch();
        if (this.mode == Mode.MAP && isInside(mx, my, this.mapScene.mapViewX(), this.mapScene.mapViewY(), this.mapScene.mapViewWidth(), this.mapScene.mapViewHeight())) {
            this.mapScene.startDrag(mouseX, mouseY);
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override public boolean mouseReleased(double mouseX, double mouseY, int button) {
        this.mapScene.stopDrag();
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (this.mode == Mode.MAP && this.mapScene.isDragging()) {
            this.mapScene.drag(mouseX, mouseY, this.sceneScale);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        int mx = (int) Math.floor((mouseX - this.sceneX) / this.sceneScale);
        int my = (int) Math.floor((mouseY - this.sceneY) / this.sceneScale);
        if (this.mode == Mode.MAP && isInside(mx, my, this.mapScene.mapViewX(), this.mapScene.mapViewY(), this.mapScene.mapViewWidth(), this.mapScene.mapViewHeight()) && scrollY != 0.0) {
            this.mapScene.scrollZoom(mx, my, scrollY, this.sceneScale);
            return true;
        }
        if (this.mode == Mode.BOOK && scrollY != 0.0 && this.entryId != null) {
            BookEntry entry = ClientEtericonData.catalog().entry(this.entryId);
            if (entry != null) {
                List<BookPage> pages = this.bookScene.displayPages(entry);
                if (scrollY > 0.0) { this.bookScene.setSpread(Math.max(0, this.bookScene.spread() - 1)); }
                else { this.bookScene.setSpread(Math.min(Math.max(0, pages.size() - 1), this.bookScene.spread() + 1)); }
            }
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.mapScene.keyPressed(keyCode, modifiers)) { return true; }
        if (!this.mapScene.searchFocused() && (this.mode == Mode.MAP || this.mode == Mode.EXTRA)) {
            if (keyCode == GLFW.GLFW_KEY_UP || keyCode == GLFW.GLFW_KEY_W) {
                this.mapScene.navigateBookmark(-1);
                return true;
            }
            if (keyCode == GLFW.GLFW_KEY_DOWN || keyCode == GLFW.GLFW_KEY_S) {
                this.mapScene.navigateBookmark(1);
                return true;
            }
            if (keyCode == GLFW.GLFW_KEY_LEFT || keyCode == GLFW.GLFW_KEY_A) {
                this.mapScene.navigateBookmarkPage(-1);
                return true;
            }
            if (keyCode == GLFW.GLFW_KEY_RIGHT || keyCode == GLFW.GLFW_KEY_D) {
                this.mapScene.navigateBookmarkPage(1);
                return true;
            }
        }
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            if (this.mode == Mode.EXTRA) { closeExtra(); }
            else { this.onClose(); }
            return true;
        }
        if (this.mode == Mode.BOOK) {
            BookEntry entry = this.entryId == null ? null : ClientEtericonData.catalog().entry(this.entryId);
            if (entry != null) {
                List<BookPage> pages = this.bookScene.displayPages(entry);
                if (keyCode == GLFW.GLFW_KEY_LEFT && this.bookScene.spread() > 0) {
                    this.bookScene.setSpread(this.bookScene.spread() - 1);
                    return true;
                }
                if (keyCode == GLFW.GLFW_KEY_RIGHT && this.bookScene.spread() < pages.size() - 1) {
                    this.bookScene.setSpread(this.bookScene.spread() + 1);
                    return true;
                }
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override public boolean charTyped(char codePoint, int modifiers) {
        if (this.mapScene.charTyped(codePoint)) { return true; }
        return super.charTyped(codePoint, modifiers);
    }

    @Override public boolean isPauseScreen() { return false; }

    public enum Mode {
        MAP,
        BOOK,
        EXTRA
    }

    private record Clickable(int x, int y, int width, int height, Runnable action) {}
}
