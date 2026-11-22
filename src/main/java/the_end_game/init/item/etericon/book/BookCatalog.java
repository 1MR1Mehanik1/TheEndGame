package the_end_game.init.item.etericon.book;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class BookCatalog {
    private final Map<ResourceLocation, BookChapter> chapters;
    private final Map<ResourceLocation, BookEntry> entries;
    private final Map<ResourceLocation, BookPage> pages;

    public BookCatalog(Map<ResourceLocation, BookChapter> chapters, Map<ResourceLocation, BookEntry> entries, Map<ResourceLocation, BookPage> pages) {
        this.chapters = Collections.unmodifiableMap(new LinkedHashMap<>(chapters));
        this.entries = Collections.unmodifiableMap(new LinkedHashMap<>(entries));
        this.pages = Collections.unmodifiableMap(new LinkedHashMap<>(pages));
    }

    public static BookCatalog empty() { return new BookCatalog(Map.of(), Map.of(), Map.of()); }

    public Map<ResourceLocation, BookChapter> chapters() { return this.chapters; }
    public Map<ResourceLocation, BookEntry> entries() { return this.entries; }
    public Map<ResourceLocation, BookPage> pages() { return this.pages; }

    public List<BookChapter> sortedChapters() {
        List<BookChapter> result = new ArrayList<>(this.chapters.values());
        result.sort(Comparator.comparingInt(BookChapter::order).thenComparing(chapter -> chapter.id().toString()));
        return result;
    }

    public List<BookEntry> entriesFor(ResourceLocation chapter) {
        List<BookEntry> result = new ArrayList<>();
        for (BookEntry entry : this.entries.values()) {
            if (entry.chapter().equals(chapter)) {
                result.add(entry);
            }
        }
        result.sort(Comparator.comparingDouble(BookEntry::y).thenComparingDouble(BookEntry::x));
        return result;
    }

    public BookChapter chapter(ResourceLocation id) { return this.chapters.get(id); }
    public BookEntry entry(ResourceLocation id) { return this.entries.get(id); }
    public BookPage page(ResourceLocation id) { return this.pages.get(id); }

    public JsonObject toJson(HolderLookup.Provider provider) {
        JsonObject json = new JsonObject();
        JsonObject chaptersJson = new JsonObject();
        for (BookChapter chapter : this.chapters.values()) {
            chaptersJson.add(chapter.id().toString(), chapter.toJson(provider));
        }
        JsonObject entriesJson = new JsonObject();
        for (BookEntry entry : this.entries.values()) {
            entriesJson.add(entry.id().toString(), entry.toJson(provider));
        }
        JsonObject pagesJson = new JsonObject();
        for (BookPage page : this.pages.values()) {
            if (page.id() != null) {
                pagesJson.add(page.id().toString(), page.toJson(provider));
            }
        }
        json.add("chapters", chaptersJson);
        json.add("entries", entriesJson);
        json.add("pages", pagesJson);
        return json;
    }

    public static BookCatalog fromJson(JsonObject json, HolderLookup.Provider provider) {
        Map<ResourceLocation, BookPage> pages = new LinkedHashMap<>();
        if (json.has("pages") && json.get("pages").isJsonObject()) {
            for (Map.Entry<String, JsonElement> entry : json.getAsJsonObject("pages").entrySet()) {
                ResourceLocation id = ResourceLocation.tryParse(entry.getKey());
                if (id == null || !entry.getValue().isJsonObject()) {
                    continue;
                }
                pages.put(id, BookPage.fromJson(entry.getValue().getAsJsonObject(), provider, id));
            }
        }
        Map<ResourceLocation, BookChapter> chapters = new LinkedHashMap<>();
        if (json.has("chapters") && json.get("chapters").isJsonObject()) {
            for (Map.Entry<String, JsonElement> entry : json.getAsJsonObject("chapters").entrySet()) {
                ResourceLocation id = ResourceLocation.tryParse(entry.getKey());
                if (id == null || !entry.getValue().isJsonObject()) {
                    continue;
                }
                chapters.put(id, BookChapter.fromJson(id, entry.getValue().getAsJsonObject(), provider));
            }
        }
        Map<ResourceLocation, BookEntry> entries = new LinkedHashMap<>();
        if (json.has("entries") && json.get("entries").isJsonObject()) {
            for (Map.Entry<String, JsonElement> mapEntry : json.getAsJsonObject("entries").entrySet()) {
                ResourceLocation id = ResourceLocation.tryParse(mapEntry.getKey());
                if (id == null || !mapEntry.getValue().isJsonObject()) {
                    continue;
                }
                BookEntry entry = BookEntry.fromJson(id, mapEntry.getValue().getAsJsonObject(), pages, provider);
                if (entry != null && chapters.containsKey(entry.chapter())) {
                    entries.put(id, entry);
                }
            }
        }
        return new BookCatalog(chapters, entries, pages);
    }
}
