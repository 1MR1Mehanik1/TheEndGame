package the_end_game.init.item.etericon.book;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class BookEntry {
    private final ResourceLocation id;
    private final ResourceLocation chapter;
    private final Component title;
    private final Component tooltip;
    private final UnlockShape shape;
    private final float x;
    private final float y;
    private final List<ResourceLocation> requires;
    private final List<UnlockCondition> unlocks;
    private final boolean startUnlocked;
    private final EntryVisibility visibility;
    private final BookIcon icon;
    private final List<BookPage> pages;
    private final List<BookPage> availablePages;
    private final List<BookPage> openedPages;
    private final List<List<UnlockCondition>> stages;
    private final List<UnlockCondition> hiddenUntil;

    public BookEntry(ResourceLocation id, ResourceLocation chapter, Component title, Component tooltip, UnlockShape shape, float x, float y,
            List<ResourceLocation> requires, List<UnlockCondition> unlocks,
            boolean startUnlocked, EntryVisibility visibility, BookIcon icon,
            List<BookPage> pages, List<BookPage> availablePages, List<BookPage> openedPages) {
        this(id, chapter, title, tooltip, shape, x, y, requires, unlocks, startUnlocked, visibility, icon, pages, availablePages, openedPages, List.of(), List.of());
    }

    public BookEntry(ResourceLocation id, ResourceLocation chapter, Component title, Component tooltip, UnlockShape shape, float x, float y,
            List<ResourceLocation> requires, List<UnlockCondition> unlocks,
            boolean startUnlocked, EntryVisibility visibility, BookIcon icon,
            List<BookPage> pages, List<BookPage> availablePages, List<BookPage> openedPages,
            List<List<UnlockCondition>> stages, List<UnlockCondition> hiddenUntil) {
        this.id = id;
        this.chapter = chapter;
        this.title = title == null ? Component.literal(id.toString()) : title;
        this.tooltip = tooltip == null ? Component.empty() : tooltip;
        this.shape = shape == null ? UnlockShape.CIRCLE : shape;
        this.x = x;
        this.y = y;
        this.requires = List.copyOf(requires);
        this.unlocks = List.copyOf(unlocks);
        this.startUnlocked = startUnlocked;
        this.visibility = visibility == null ? EntryVisibility.ALWAYS : visibility;
        this.icon = icon == null ? new BookIcon.ItemIcon(new ItemStack(Items.BOOK), 16, 16) : icon;
        this.pages = List.copyOf(pages);
        this.availablePages = List.copyOf(availablePages);
        this.openedPages = List.copyOf(openedPages);
        this.stages = copyStages(stages);
        this.hiddenUntil = List.copyOf(hiddenUntil);
    }

    private static List<List<UnlockCondition>> copyStages(List<List<UnlockCondition>> stages) {
        List<List<UnlockCondition>> copy = new ArrayList<>();
        for (List<UnlockCondition> stage : stages) { copy.add(List.copyOf(stage)); }
        return List.copyOf(copy);
    }

    public ResourceLocation id() { return this.id; }
    public ResourceLocation chapter() { return this.chapter; }
    public Component title() { return this.title; }
    public Component tooltip() { return this.tooltip; }
    public UnlockShape shape() { return this.shape; }
    public float x() { return this.x; }
    public float y() { return this.y; }
    public List<ResourceLocation> requires() { return this.requires; }
    public List<UnlockCondition> unlocks() { return this.unlocks; }
    public boolean startUnlocked() { return this.startUnlocked; }
    public EntryVisibility visibility() { return this.visibility; }

    public boolean isVisible(BookProgress progress) { return EntryAccess.isVisible(this, progress); }

    public BookIcon icon() { return this.icon; }
    public List<BookPage> pages() { return this.pages; }
    public List<BookPage> availablePages() { return this.availablePages; }
    public List<BookPage> openedPages() { return this.openedPages; }
    public boolean hasPreview() { return !this.availablePages.isEmpty(); }
    public List<List<UnlockCondition>> stages() { return this.stages; }
    public List<UnlockCondition> hiddenUntil() { return this.hiddenUntil; }
    public boolean hasStages() { return !this.stages.isEmpty(); }

    public List<BookPage> pagesFor(EntryState state) {
        if (state == EntryState.AVAILABLE && this.hasPreview()) { return this.availablePages; }
        if (state == EntryState.OPENED && !this.openedPages.isEmpty()) { return this.openedPages; }
        if (!this.pages.isEmpty()) { return this.pages; }
        if (!this.openedPages.isEmpty()) { return this.openedPages; }
        return this.availablePages;
    }

    public JsonObject toJson(HolderLookup.Provider provider) {
        JsonObject json = new JsonObject();
        json.add("title", BookJson.componentToJson(this.title, provider));
        if (!this.tooltip.getString().isEmpty()) { json.add("tooltip", BookJson.componentToJson(this.tooltip, provider)); }
        json.addProperty("chapter", this.chapter.toString());
        json.addProperty("shape", this.shape.getSerializedName());
        json.addProperty("x", this.x);
        json.addProperty("y", this.y);
        if (!this.requires.isEmpty()) {
            JsonArray array = new JsonArray();
            for (ResourceLocation required : this.requires) { array.add(required.toString()); }
            json.add("requires", array);
        }
        if (!this.unlocks.isEmpty()) {
            JsonArray array = new JsonArray();
            for (UnlockCondition unlock : this.unlocks) { array.add(unlock.toJson()); }
            json.add("unlock", array);
        }
        if (this.startUnlocked) { json.addProperty("start_unlocked", true); }
        if (this.visibility != EntryVisibility.ALWAYS) { json.addProperty("visibility", this.visibility.getSerializedName()); }
        if (!this.hiddenUntil.isEmpty()) {
            JsonArray array = new JsonArray();
            for (UnlockCondition condition : this.hiddenUntil) { array.add(condition.toJson()); }
            json.add("hidden_until", array);
        }
        if (!this.stages.isEmpty()) {
            JsonArray stagesJson = new JsonArray();
            for (List<UnlockCondition> stage : this.stages) {
                JsonObject stageJson = new JsonObject();
                JsonArray conditionsJson = new JsonArray();
                for (UnlockCondition condition : stage) { conditionsJson.add(condition.toJson()); }
                stageJson.add("conditions", conditionsJson);
                stagesJson.add(stageJson);
            }
            json.add("stages", stagesJson);
        }
        json.add("icon", this.icon.toJson());
        writePages(json, "pages", this.pages, provider);
        writePages(json, "available_pages", this.availablePages, provider);
        writePages(json, "opened_pages", this.openedPages, provider);
        return json;
    }

    private static void writePages(JsonObject json, String key, List<BookPage> pages, HolderLookup.Provider provider) {
        if (pages.isEmpty()) { return; }
        JsonArray array = new JsonArray();
        for (BookPage page : pages) { array.add(page.toJson(provider)); }
        json.add(key, array);
    }

    public static BookEntry fromJson(ResourceLocation id, JsonObject json, Map<ResourceLocation, BookPage> pages, HolderLookup.Provider provider) {
        if (!json.has("chapter")) { return null; }
        ResourceLocation chapter = ResourceLocation.tryParse(json.get("chapter").getAsString());
        if (chapter == null) { return null; }
        List<UnlockCondition> unlocks = new ArrayList<>();
        if (json.has("unlock") && json.get("unlock").isJsonArray()) {
            for (JsonElement element : json.getAsJsonArray("unlock")) {
                if (element.isJsonObject()) {
                    UnlockCondition condition = UnlockCondition.fromJson(element.getAsJsonObject());
                    if (condition != null) { unlocks.add(condition); }
                }
            }
        }
        UnlockShape shape = json.has("shape") ? UnlockShape.byName(json.get("shape").getAsString()) : unlocks.isEmpty() ? UnlockShape.CIRCLE : UnlockShape.forUnlock(unlocks.get(0));
        List<ResourceLocation> requires = new ArrayList<>();
        if (json.has("requires") && json.get("requires").isJsonArray()) {
            for (JsonElement element : json.getAsJsonArray("requires")) {
                ResourceLocation required = ResourceLocation.tryParse(element.getAsString());
                if (required != null) { requires.add(required); }
            }
        }
        return new BookEntry(
                id,
                chapter,
                BookJson.parseComponent(json.get("title"), provider),
                BookJson.parseComponent(json.get("tooltip"), provider),
                shape,
                json.has("x") ? json.get("x").getAsFloat() : 136.0F,
                json.has("y") ? json.get("y").getAsFloat() : 90.0F,
                requires,
                unlocks,
                json.has("start_unlocked") && json.get("start_unlocked").getAsBoolean(),
                json.has("visibility") ? EntryVisibility.byName(json.get("visibility").getAsString()) : EntryVisibility.ALWAYS,
                BookIcon.fromJson(json.get("icon")),
                readPages(json.get("pages"), pages, provider),
                readPages(json.get("available_pages"), pages, provider),
                readPages(json.get("opened_pages"), pages, provider),
                readStages(json.get("stages")),
                readConditions(json.get("hidden_until"))
        );
    }

    private static List<List<UnlockCondition>> readStages(JsonElement element) {
        List<List<UnlockCondition>> stages = new ArrayList<>();
        if (element == null || !element.isJsonArray()) { return stages; }
        for (JsonElement child : element.getAsJsonArray()) {
            if (!child.isJsonObject()) { continue; }
            stages.add(readConditions(child.getAsJsonObject().get("conditions")));
        }
        return stages;
    }

    private static List<UnlockCondition> readConditions(JsonElement element) {
        List<UnlockCondition> conditions = new ArrayList<>();
        if (element == null || !element.isJsonArray()) { return conditions; }
        for (JsonElement child : element.getAsJsonArray()) {
            if (child.isJsonObject()) {
                UnlockCondition condition = UnlockCondition.fromJson(child.getAsJsonObject());
                if (condition != null) { conditions.add(condition); }
            }
        }
        return conditions;
    }

    private static List<BookPage> readPages(JsonElement element, Map<ResourceLocation, BookPage> pages, HolderLookup.Provider provider) {
        List<BookPage> result = new ArrayList<>();
        if (element == null || !element.isJsonArray()) { return result; }
        for (JsonElement child : element.getAsJsonArray()) {
            if (child.isJsonObject()) {
                result.add(BookPage.fromJson(child.getAsJsonObject(), provider));
            } else if (child.isJsonPrimitive()) {
                ResourceLocation id = ResourceLocation.tryParse(child.getAsString());
                if (id != null) {
                    BookPage page = pages.get(id);
                    if (page != null) { result.add(page); }
                }
            }
        }
        return result;
    }

    public static Builder builder(ResourceLocation id) {
        return new Builder(id);
    }

    public static final class Builder {
        private final ResourceLocation id;
        private ResourceLocation chapter;
        private Component title;
        private Component tooltip;
        private UnlockShape shape;
        private float x = 136.0F;
        private float y = 90.0F;
        private final List<ResourceLocation> requires = new ArrayList<>();
        private final List<UnlockCondition> unlocks = new ArrayList<>();
        private boolean startUnlocked;
        private EntryVisibility visibility = EntryVisibility.ALWAYS;
        private BookIcon icon;
        private final List<BookPage> pages = new ArrayList<>();
        private final List<BookPage> availablePages = new ArrayList<>();
        private final List<BookPage> openedPages = new ArrayList<>();
        private final List<List<UnlockCondition>> stages = new ArrayList<>();
        private final List<UnlockCondition> hiddenUntil = new ArrayList<>();

        private Builder(ResourceLocation id) { this.id = id; }

        public Builder chapter(ResourceLocation chapter) {
            this.chapter = chapter;
            return this;
        }

        public Builder title(Component title) {
            this.title = title;
            return this;
        }

        public Builder tooltip(Component tooltip) {
            this.tooltip = tooltip;
            return this;
        }

        public Builder shape(UnlockShape shape) {
            this.shape = shape;
            return this;
        }

        public Builder pos(float x, float y) {
            this.x = x;
            this.y = y;
            return this;
        }

        public Builder requires(ResourceLocation... ids) {
            Collections.addAll(this.requires, ids);
            return this;
        }

        public Builder unlock(UnlockCondition... conditions) {
            Collections.addAll(this.unlocks, conditions);
            return this;
        }

        public Builder startUnlocked() {
            this.startUnlocked = true;
            return this;
        }

        public Builder visibility(EntryVisibility visibility) {
            this.visibility = visibility;
            return this;
        }

        public Builder icon(BookIcon icon) {
            this.icon = icon;
            return this;
        }

        public Builder pages(BookPage... pages) {
            Collections.addAll(this.pages, pages);
            return this;
        }

        public Builder availablePages(BookPage... pages) {
            Collections.addAll(this.availablePages, pages);
            return this;
        }

        public Builder openedPages(BookPage... pages) {
            Collections.addAll(this.openedPages, pages);
            return this;
        }

        public Builder stage(UnlockCondition... conditions) {
            List<UnlockCondition> stage = new ArrayList<>();
            Collections.addAll(stage, conditions);
            this.stages.add(stage);
            return this;
        }

        public Builder hiddenUntil(UnlockCondition... conditions) {
            Collections.addAll(this.hiddenUntil, conditions);
            return this;
        }

        public BookEntry build() {
            if (this.chapter == null) { throw new IllegalStateException("Entry " + this.id + " has no chapter"); }
            UnlockShape resolved = this.shape != null ? this.shape : this.unlocks.isEmpty() ? UnlockShape.CIRCLE : UnlockShape.forUnlock(this.unlocks.get(0));
            return new BookEntry(
                    this.id,
                    this.chapter,
                    this.title,
                    this.tooltip,
                    resolved,
                    this.x,
                    this.y,
                    this.requires,
                    this.unlocks,
                    this.startUnlocked,
                    this.visibility,
                    this.icon,
                    this.pages,
                    this.availablePages,
                    this.openedPages,
                    this.stages,
                    this.hiddenUntil
            );
        }
    }
}
