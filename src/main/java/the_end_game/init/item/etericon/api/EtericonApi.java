package the_end_game.init.item.etericon.api;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.registries.BuiltInRegistries;
import the_end_game.init.item.etericon.EtericonAttachments;
import the_end_game.init.item.etericon.book.BookCatalog;
import the_end_game.init.item.etericon.book.BookChapter;
import the_end_game.init.item.etericon.book.BookElement;
import the_end_game.init.item.etericon.book.BookEntry;
import the_end_game.init.item.etericon.book.BookIcon;
import the_end_game.init.item.etericon.book.BookManager;
import the_end_game.init.item.etericon.book.BookPage;
import the_end_game.init.item.etericon.book.BookProgress;
import the_end_game.init.item.etericon.book.EntryState;
import the_end_game.init.item.etericon.book.EntryVisibility;
import the_end_game.init.item.etericon.book.UnlockCondition;
import the_end_game.init.item.etericon.book.UnlockShape;
import the_end_game.init.item.etericon.network.EtericonNetwork;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public final class EtericonApi {
    private static final Map<ResourceLocation, BookChapter> CHAPTERS = new LinkedHashMap<>();
    private static final Map<ResourceLocation, BookEntry> ENTRIES = new LinkedHashMap<>();
    private static final Map<ResourceLocation, BookPage> PAGES = new LinkedHashMap<>();

    public static Map<ResourceLocation, BookChapter> chapters() { return Collections.unmodifiableMap(CHAPTERS); }
    public static Map<ResourceLocation, BookEntry> entries() { return Collections.unmodifiableMap(ENTRIES); }
    public static Map<ResourceLocation, BookPage> pages() { return Collections.unmodifiableMap(PAGES); }

    public static BookCatalog javaCatalog() { return new BookCatalog(CHAPTERS, ENTRIES, PAGES); }

    public static ChapterBuilder chapter(ResourceLocation id) { return new ChapterBuilder(id); }
    public static EntryBuilder entry(ResourceLocation id) { return new EntryBuilder(id); }
    public static PageBuilder page(ResourceLocation id) { return new PageBuilder(id); }

    public static BookProgress getProgress(ServerPlayer player) { return player.getData(EtericonAttachments.PROGRESS); }

    public static EntryState getState(ServerPlayer player, ResourceLocation entry) {
        BookProgress progress = getProgress(player);
        return progress.stateOf(entry);
    }

    public static void addResearchPoints(ServerPlayer player, int amount) { mutate(player, progress -> progress.addResearchPoints(amount)); }

    public static void completeTheory(ServerPlayer player, ResourceLocation theory) { mutate(player, progress -> progress.completeTheory(theory)); }

    public static void markCrafted(ServerPlayer player, ResourceLocation item) { mutate(player, progress -> progress.markCrafted(item)); }
    public static void markCrafted(ServerPlayer player, Item item) { markCrafted(player, BuiltInRegistries.ITEM.getKey(item)); }
    public static void markCrafted(ServerPlayer player, ItemStack stack) { if (!stack.isEmpty()) { markCrafted(player, stack.getItem()); } }

    public static void markStudied(ServerPlayer player, ResourceLocation target) { mutate(player, progress -> progress.markStudied(target)); }

    public static void addKill(ServerPlayer player, ResourceLocation entity, int count) { mutate(player, progress -> progress.addKill(entity, count)); }
    public static void addKill(ServerPlayer player, EntityType<?> entity, int count) { addKill(player, BuiltInRegistries.ENTITY_TYPE.getKey(entity), count); }

    public static void openBook(ServerPlayer player) { EtericonNetwork.open(player); }
    public static void sync(ServerPlayer player) { EtericonNetwork.sync(player); }

    public static void mutate(ServerPlayer player, Consumer<BookProgress> action) {
        BookProgress progress = player.getData(EtericonAttachments.PROGRESS);
        Map<ResourceLocation, EntryState> before = new HashMap<>(progress.states());
        action.accept(progress);
        BookCatalog catalog = BookManager.getOrJavaOnly();
        progress.recalculate(catalog);
        player.setData(EtericonAttachments.PROGRESS, progress);
        EtericonNetwork.sync(player);
        List<ResourceLocation> notices = new ArrayList<>();
        for (BookEntry entry : catalog.entries().values()) {
            if (progress.stateOf(entry) == EntryState.AVAILABLE
                    && before.getOrDefault(entry.id(), EntryState.LOCKED) != EntryState.AVAILABLE
                    && entry.isVisible(progress)) {
                notices.add(entry.id());
            }
        }
        EtericonNetwork.notifyAvailable(player, notices);
    }

    public static void setState(ServerPlayer player, ResourceLocation entry, EntryState state) {
        BookProgress progress = player.getData(EtericonAttachments.PROGRESS);
        progress.setState(entry, state);
        progress.recalculate(BookManager.getOrJavaOnly());
        player.setData(EtericonAttachments.PROGRESS, progress);
        EtericonNetwork.sync(player);
    }

    public static void setStates(ServerPlayer player, Collection<ResourceLocation> entries, EntryState state) {
        BookProgress progress = player.getData(EtericonAttachments.PROGRESS);
        for (ResourceLocation entry : entries) { progress.setState(entry, state); }
        progress.recalculate(BookManager.getOrJavaOnly());
        player.setData(EtericonAttachments.PROGRESS, progress);
        EtericonNetwork.sync(player);
    }

    public static final class ChapterBuilder {
        private final ResourceLocation id;
        private Component title;
        private Component subtitle;
        private BookIcon icon;
        private int order;
        private int bookmark;
        private int frame;
        private int left;
        private int right;
        private int bookmarkWidth;
        private int bookmarkHeight;
        private int frameWidth;
        private int frameHeight;
        private BookIcon bookmarkImage;
        private BookIcon frameImage;
        private BookIcon leftImage;
        private BookIcon rightImage;
        private boolean startUnlocked = true;
        private final List<UnlockCondition> unlocks = new ArrayList<>();

        private ChapterBuilder(ResourceLocation id) {
            this.id = id;
        }

        public ChapterBuilder title(Component title) {
            this.title = title;
            return this;
        }

        public ChapterBuilder subtitle(Component subtitle) {
            this.subtitle = subtitle;
            return this;
        }

        public ChapterBuilder icon(BookIcon icon) {
            this.icon = icon;
            return this;
        }

        public ChapterBuilder order(int order) {
            this.order = order;
            return this;
        }

        public ChapterBuilder bookmark(int bookmark) {
            this.bookmark = bookmark;
            return this;
        }

        public ChapterBuilder frame(int frame) {
            this.frame = frame;
            return this;
        }

        public ChapterBuilder left(int left) {
            this.left = left;
            return this;
        }

        public ChapterBuilder right(int right) {
            this.right = right;
            return this;
        }

        public ChapterBuilder bookmarkSize(int width, int height) {
            this.bookmarkWidth = width;
            this.bookmarkHeight = height;
            return this;
        }

        public ChapterBuilder frameSize(int width, int height) {
            this.frameWidth = width;
            this.frameHeight = height;
            return this;
        }

        public ChapterBuilder bookmarkImage(BookIcon bookmarkImage) {
            this.bookmarkImage = bookmarkImage;
            return this;
        }

        public ChapterBuilder frameImage(BookIcon frameImage) {
            this.frameImage = frameImage;
            return this;
        }

        public ChapterBuilder leftImage(BookIcon leftImage) {
            this.leftImage = leftImage;
            return this;
        }

        public ChapterBuilder rightImage(BookIcon rightImage) {
            this.rightImage = rightImage;
            return this;
        }

        public ChapterBuilder startUnlocked() {
            this.startUnlocked = true;
            return this;
        }

        public ChapterBuilder locked() {
            this.startUnlocked = false;
            return this;
        }

        public ChapterBuilder unlock(UnlockCondition... conditions) {
            for (UnlockCondition condition : conditions) {
                if (condition != null) { this.unlocks.add(condition); }
            }
            return this;
        }

        public BookChapter build() {
            return new BookChapter(this.id, this.title, this.subtitle, this.icon, this.order, this.bookmark, this.frame, this.left, this.right, this.bookmarkWidth, this.bookmarkHeight, this.frameWidth, this.frameHeight, this.bookmarkImage, this.frameImage, this.leftImage, this.rightImage, this.startUnlocked, this.unlocks);
        }

        public BookChapter register() {
            BookChapter chapter = build();
            CHAPTERS.put(this.id, chapter);
            return chapter;
        }
    }

    public static final class EntryBuilder {
        private final BookEntry.Builder builder;

        private EntryBuilder(ResourceLocation id) { this.builder = BookEntry.builder(id); }

        public EntryBuilder chapter(ResourceLocation chapter) {
            this.builder.chapter(chapter);
            return this;
        }

        public EntryBuilder title(Component title) {
            this.builder.title(title);
            return this;
        }

        public EntryBuilder tooltip(Component tooltip) {
            this.builder.tooltip(tooltip);
            return this;
        }

        public EntryBuilder shape(UnlockShape shape) {
            this.builder.shape(shape);
            return this;
        }

        public EntryBuilder pos(float x, float y) {
            this.builder.pos(x, y);
            return this;
        }

        public EntryBuilder requires(ResourceLocation... ids) {
            this.builder.requires(ids);
            return this;
        }

        public EntryBuilder unlock(UnlockCondition... conditions) {
            this.builder.unlock(conditions);
            return this;
        }

        public EntryBuilder startUnlocked() {
            this.builder.startUnlocked();
            return this;
        }

        public EntryBuilder visibility(EntryVisibility visibility) {
            this.builder.visibility(visibility);
            return this;
        }

        public EntryBuilder hiddenUntil(UnlockCondition... conditions) {
            this.builder.hiddenUntil(conditions);
            return this;
        }

        public EntryBuilder stage(UnlockCondition... conditions) {
            this.builder.stage(conditions);
            return this;
        }

        public EntryBuilder icon(BookIcon icon) {
            this.builder.icon(icon);
            return this;
        }

        public EntryBuilder pages(BookPage... pages) {
            this.builder.pages(pages);
            return this;
        }

        public EntryBuilder availablePages(BookPage... pages) {
            this.builder.availablePages(pages);
            return this;
        }

        public EntryBuilder openedPages(BookPage... pages) {
            this.builder.openedPages(pages);
            return this;
        }

        public BookEntry build() {
            return this.builder.build();
        }

        public BookEntry register() {
            BookEntry entry = build();
            ENTRIES.put(entry.id(), entry);
            return entry;
        }
    }

    public static final class PageBuilder {
        private final ResourceLocation id;
        private final BookPage page;

        private PageBuilder(ResourceLocation id) {
            this.id = id;
            this.page = new BookPage(id);
        }

        public PageBuilder left(BookElement... elements) {
            this.page.left(elements);
            return this;
        }

        public PageBuilder right(BookElement... elements) {
            this.page.right(elements);
            return this;
        }

        public BookPage build() { return this.page; }

        public BookPage register() {
            PAGES.put(this.id, this.page);
            return this.page;
        }
    }
}
