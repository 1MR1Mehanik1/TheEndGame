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
import java.util.List;

public record BookChapter(ResourceLocation id, Component title, Component subtitle, BookIcon icon,
        int order, int bookmark, int frame, int left, int right, int bookmarkWidth, int bookmarkHeight, int frameWidth, int frameHeight,
        BookIcon bookmarkImage, BookIcon frameImage, BookIcon leftImage, BookIcon rightImage,
        boolean startUnlocked, List<UnlockCondition> unlocks) {
    public BookChapter {
        title = title == null ? Component.literal(id.toString()) : title;
        subtitle = subtitle == null ? Component.empty() : subtitle;
        icon = icon == null ? new BookIcon.ItemIcon(new ItemStack(Items.BOOK), 16, 16) : icon;
        bookmark = Math.max(0, Math.min(3, bookmark));
        frame = Math.max(0, Math.min(4, frame));
        left = Math.max(0, Math.min(6, left));
        right = Math.max(0, Math.min(6, right));
        bookmarkWidth = Math.max(0, bookmarkWidth);
        bookmarkHeight = Math.max(0, bookmarkHeight);
        frameWidth = Math.max(0, frameWidth);
        frameHeight = Math.max(0, frameHeight);
        unlocks = unlocks == null ? List.of() : List.copyOf(unlocks);
    }

    public boolean isUnlocked(BookCatalog catalog, BookProgress progress) {
        if (this.startUnlocked) { return true; }
        for (UnlockCondition condition : this.unlocks) {
            if (condition.isSatisfied(progress)) { return true; }
        }
        for (BookEntry entry : catalog.entriesFor(this.id)) {
            if (entry.isVisible(progress)) { return true; }
        }
        return false;
    }

    public JsonObject toJson(HolderLookup.Provider provider) {
        JsonObject json = new JsonObject();
        json.add("title", BookJson.componentToJson(this.title, provider));
        json.add("subtitle", BookJson.componentToJson(this.subtitle, provider));
        json.add("icon", this.icon.toJson());
        json.addProperty("order", this.order);
        json.addProperty("bookmark", this.bookmark);
        json.addProperty("frame", this.frame);
        json.addProperty("left", this.left);
        json.addProperty("right", this.right);
        json.addProperty("bookmark_width", this.bookmarkWidth);
        json.addProperty("bookmark_height", this.bookmarkHeight);
        json.addProperty("frame_width", this.frameWidth);
        json.addProperty("frame_height", this.frameHeight);
        if (this.bookmarkImage != null) { json.add("bookmark_image", this.bookmarkImage.toJson()); }
        if (this.frameImage != null) { json.add("frame_image", this.frameImage.toJson()); }
        if (this.leftImage != null) { json.add("left_image", this.leftImage.toJson()); }
        if (this.rightImage != null) { json.add("right_image", this.rightImage.toJson()); }
        if (!this.startUnlocked) { json.addProperty("start_unlocked", false); }
        if (!this.unlocks.isEmpty()) {
            JsonArray array = new JsonArray();
            for (UnlockCondition condition : this.unlocks) { array.add(condition.toJson()); }
            json.add("unlock", array);
        }
        return json;
    }

    public static BookChapter fromJson(ResourceLocation id, JsonObject json, HolderLookup.Provider provider) {
        return new BookChapter(
                id,
                BookJson.parseComponent(json.get("title"), provider),
                BookJson.parseComponent(json.get("subtitle"), provider),
                BookIcon.fromJson(json.get("icon")),
                json.has("order") ? json.get("order").getAsInt() : 0,
                json.has("bookmark") ? json.get("bookmark").getAsInt() : 0,
                json.has("frame") ? json.get("frame").getAsInt() : 0,
                json.has("left") ? json.get("left").getAsInt() : 0,
                json.has("right") ? json.get("right").getAsInt() : 0,
                json.has("bookmark_width") ? json.get("bookmark_width").getAsInt() : 0,
                json.has("bookmark_height") ? json.get("bookmark_height").getAsInt() : 0,
                json.has("frame_width") ? json.get("frame_width").getAsInt() : 0,
                json.has("frame_height") ? json.get("frame_height").getAsInt() : 0,
                BookIcon.fromJson(json.get("bookmark_image")),
                BookIcon.fromJson(json.get("frame_image")),
                BookIcon.fromJson(json.get("left_image")),
                BookIcon.fromJson(json.get("right_image")),
                !json.has("start_unlocked") || json.get("start_unlocked").getAsBoolean(),
                readUnlocks(json.get("unlock"))
        );
    }

    private static List<UnlockCondition> readUnlocks(JsonElement element) {
        List<UnlockCondition> unlocks = new ArrayList<>();
        if (element == null || !element.isJsonArray()) { return unlocks; }
        for (JsonElement child : element.getAsJsonArray()) {
            if (child.isJsonObject()) {
                UnlockCondition condition = UnlockCondition.fromJson(child.getAsJsonObject());
                if (condition != null) { unlocks.add(condition); }
            }
        }
        return unlocks;
    }

    public static Builder builder(ResourceLocation id) { return new Builder(id); }

    public static final class Builder {
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

        private Builder(ResourceLocation id) { this.id = id; }

        public Builder title(Component title) {
            this.title = title;
            return this;
        }

        public Builder subtitle(Component subtitle) {
            this.subtitle = subtitle;
            return this;
        }

        public Builder icon(BookIcon icon) {
            this.icon = icon;
            return this;
        }

        public Builder order(int order) {
            this.order = order;
            return this;
        }

        public Builder bookmark(int bookmark) {
            this.bookmark = bookmark;
            return this;
        }

        public Builder frame(int frame) {
            this.frame = frame;
            return this;
        }

        public Builder left(int left) {
            this.left = left;
            return this;
        }

        public Builder right(int right) {
            this.right = right;
            return this;
        }

        public Builder bookmarkSize(int width, int height) {
            this.bookmarkWidth = width;
            this.bookmarkHeight = height;
            return this;
        }

        public Builder frameSize(int width, int height) {
            this.frameWidth = width;
            this.frameHeight = height;
            return this;
        }

        public Builder bookmarkImage(BookIcon bookmarkImage) {
            this.bookmarkImage = bookmarkImage;
            return this;
        }

        public Builder frameImage(BookIcon frameImage) {
            this.frameImage = frameImage;
            return this;
        }

        public Builder leftImage(BookIcon leftImage) {
            this.leftImage = leftImage;
            return this;
        }

        public Builder rightImage(BookIcon rightImage) {
            this.rightImage = rightImage;
            return this;
        }

        public Builder startUnlocked() {
            this.startUnlocked = true;
            return this;
        }

        public Builder locked() {
            this.startUnlocked = false;
            return this;
        }

        public Builder unlock(UnlockCondition... conditions) {
            for (UnlockCondition condition : conditions) {
                if (condition != null) { this.unlocks.add(condition); }
            }
            return this;
        }

        public BookChapter build() {
            return new BookChapter(this.id, this.title, this.subtitle, this.icon, this.order, this.bookmark, this.frame, this.left, this.right, this.bookmarkWidth, this.bookmarkHeight, this.frameWidth, this.frameHeight, this.bookmarkImage, this.frameImage, this.leftImage, this.rightImage, this.startUnlocked, this.unlocks);
        }
    }
}
