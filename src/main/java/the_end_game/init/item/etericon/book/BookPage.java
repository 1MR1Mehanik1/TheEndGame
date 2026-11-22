package the_end_game.init.item.etericon.book;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class BookPage {
    private final ResourceLocation id;
    private final List<BookElement> left = new ArrayList<>();
    private final List<BookElement> right = new ArrayList<>();

    public BookPage() { this(null); }

    public BookPage(ResourceLocation id) { this.id = id; }
    public ResourceLocation id() { return this.id; }

    public List<BookElement> left() { return Collections.unmodifiableList(this.left); }
    public List<BookElement> right() { return Collections.unmodifiableList(this.right); }

    public BookPage left(BookElement... elements) {
        Collections.addAll(this.left, elements);
        return this;
    }
    public BookPage right(BookElement... elements) {
        Collections.addAll(this.right, elements);
        return this;
    }

    public boolean isEmpty() { return this.left.isEmpty() && this.right.isEmpty(); }

    public JsonObject toJson(HolderLookup.Provider provider) {
        JsonObject json = new JsonObject();
        if (this.id != null) { json.addProperty("id", this.id.toString()); }
        if (!this.left.isEmpty()) {
            JsonArray array = new JsonArray();
            for (BookElement element : this.left) { array.add(element.toJson(provider)); }
            json.add("left", array);
        }
        if (!this.right.isEmpty()) {
            JsonArray array = new JsonArray();
            for (BookElement element : this.right) { array.add(element.toJson(provider)); }
            json.add("right", array);
        }
        return json;
    }

    public static BookPage fromJson(JsonObject json, HolderLookup.Provider provider) { return fromJson(json, provider, null); }
    public static BookPage fromJson(JsonObject json, HolderLookup.Provider provider, ResourceLocation id) {
        BookPage page = new BookPage(id);
        readSide(page.left, json.get("left"), provider);
        readSide(page.right, json.get("right"), provider);
        return page;
    }

    private static void readSide(List<BookElement> target, JsonElement element, HolderLookup.Provider provider) {
        if (element == null || !element.isJsonArray()) { return; }
        for (JsonElement child : element.getAsJsonArray()) {
            if (!child.isJsonObject()) { continue; }
            BookElement parsed = BookElement.fromJson(child.getAsJsonObject(), provider);
            if (parsed != null) { target.add(parsed); }
        }
    }
}
