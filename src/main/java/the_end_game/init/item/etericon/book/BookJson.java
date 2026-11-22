package the_end_game.init.item.etericon.book;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;
import the_end_game.init.item.etericon.api.EtericonApi;

import java.io.Reader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class BookJson {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static Component parseComponent(JsonElement element, HolderLookup.Provider provider) {
        if (element == null || element.isJsonNull()) { return Component.empty(); }
        if (element.isJsonPrimitive()) { return Component.literal(element.getAsString()); }
        try {
            Component component = Component.Serializer.fromJson(element, provider);
            return component == null ? Component.empty() : component;
        } catch (Exception exception) {
            LOGGER.warn("Invalid text component: {}", element, exception);
            return Component.literal(element.toString());
        }
    }

    public static JsonElement componentToJson(Component component, HolderLookup.Provider provider) {
        try {
            return JsonParser.parseString(Component.Serializer.toJson(component, provider));
        } catch (Exception exception) {
            LOGGER.warn("Unable to serialize text component", exception);
            return JsonParser.parseString("{\"text\":\"\"}");
        }
    }

    public static int parseColor(JsonElement element, int fallback) {
        if (element == null || element.isJsonNull()) { return fallback; }
        if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber()) { return element.getAsInt(); }
        String text = element.getAsString().trim();
        if (text.startsWith("#")) { text = text.substring(1); }
        try {
            if (text.length() == 6) { return 0xFF000000 | Integer.parseUnsignedInt(text, 16); }
            if (text.length() == 8) { return (int) Long.parseLong(text, 16); }
        } catch (NumberFormatException exception) { LOGGER.warn("Invalid color: {}", element); }
        return fallback;
    }

    public static String colorToJson(int color) { return String.format("#%08X", color); }

    public static ItemStack parseStack(JsonElement element) {
        if (element == null || element.isJsonNull()) { return ItemStack.EMPTY; }
        if (element.isJsonPrimitive()) { return parseItem(element.getAsString(), 1); }
        if (!element.isJsonObject()) { return ItemStack.EMPTY; }
        JsonObject json = element.getAsJsonObject();
        JsonElement idElement = json.get("item");
        if (idElement == null) { idElement = json.get("id"); }
        if (idElement == null || !idElement.isJsonPrimitive()) { return ItemStack.EMPTY; }
        int count = json.has("count") ? json.get("count").getAsInt() : 1;
        return parseItem(idElement.getAsString(), count);
    }

    private static ItemStack parseItem(String id, int count) {
        if (id == null || id.isEmpty()) { return ItemStack.EMPTY; }
        ResourceLocation location = ResourceLocation.tryParse(id);
        if (location == null) {
            LOGGER.warn("Invalid item id: {}", id);
            return ItemStack.EMPTY;
        }
        Optional<Item> item = BuiltInRegistries.ITEM.getOptional(location);
        if (item.isEmpty()) {
            LOGGER.warn("Unknown item: {}", location);
            return ItemStack.EMPTY;
        }
        return new ItemStack(item.get(), Math.max(1, Math.min(99, count)));
    }

    public static JsonElement stackToJson(ItemStack stack) {
        if (stack == null || stack.isEmpty()) { return JsonParser.parseString("\"\""); }
        String id = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
        if (stack.getCount() <= 1) { return JsonParser.parseString("\"" + id + "\""); }
        JsonObject json = new JsonObject();
        json.addProperty("item", id);
        json.addProperty("count", stack.getCount());
        return json;
    }

    public static String catalogToJson(BookCatalog catalog) { return catalog.toJson(net.minecraft.core.RegistryAccess.EMPTY).toString(); }

    public static BookCatalog catalogFromJson(String json, HolderLookup.Provider provider) {
        JsonElement element = JsonParser.parseString(json);
        return BookCatalog.fromJson(element.getAsJsonObject(), provider);
    }

    public static BookCatalog load(ResourceManager resourceManager, HolderLookup.Provider provider) {
        Map<ResourceLocation, JsonObject> chapterFiles = new LinkedHashMap<>();
        Map<ResourceLocation, JsonObject> entryFiles = new LinkedHashMap<>();
        Map<ResourceLocation, JsonObject> pageFiles = new LinkedHashMap<>();
        Map<ResourceLocation, Resource> resources = resourceManager.listResources("etericon", path -> path.getPath().endsWith(".json"));
        List<Map.Entry<ResourceLocation, Resource>> sorted = new ArrayList<>(resources.entrySet());
        sorted.sort(Comparator.comparing(entry -> entry.getKey().toString()));
        for (Map.Entry<ResourceLocation, Resource> entry : sorted) {
            ResourceLocation location = entry.getKey();
            String path = location.getPath();
            String prefix;
            Map<ResourceLocation, JsonObject> target;
            if (path.startsWith("etericon/chapters/")) {
                prefix = "etericon/chapters/";
                target = chapterFiles;
            } else if (path.startsWith("etericon/entries/")) {
                prefix = "etericon/entries/";
                target = entryFiles;
            } else if (path.startsWith("etericon/pages/")) {
                prefix = "etericon/pages/";
                target = pageFiles;
            } else { continue; }
            String idPath = path.substring(prefix.length(), path.length() - 5);
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath(location.getNamespace(), idPath);
            try (Reader reader = entry.getValue().openAsReader()) {
                JsonElement element = JsonParser.parseReader(reader);
                if (element != null && element.isJsonObject()) { target.put(id, element.getAsJsonObject()); }
            } catch (Exception exception) { LOGGER.error("Unable to read Etericon file {}", location, exception); }
        }
        Map<ResourceLocation, BookPage> pages = new LinkedHashMap<>(EtericonApi.pages());
        for (Map.Entry<ResourceLocation, JsonObject> entry : pageFiles.entrySet()) {
            try {
                pages.put(entry.getKey(), BookPage.fromJson(entry.getValue(), provider, entry.getKey()));
            } catch (Exception exception) {
                LOGGER.error("Unable to parse Etericon page {}", entry.getKey(), exception);
            }
        }
        Map<ResourceLocation, BookChapter> chapters = new LinkedHashMap<>(EtericonApi.chapters());
        for (Map.Entry<ResourceLocation, JsonObject> entry : chapterFiles.entrySet()) {
            try {
                chapters.put(entry.getKey(), BookChapter.fromJson(entry.getKey(), entry.getValue(), provider));
            } catch (Exception exception) {
                LOGGER.error("Unable to parse Etericon chapter {}", entry.getKey(), exception);
            }
        }
        Map<ResourceLocation, BookEntry> entries = new LinkedHashMap<>(EtericonApi.entries());
        for (Map.Entry<ResourceLocation, JsonObject> entry : entryFiles.entrySet()) {
            try {
                BookEntry parsed = BookEntry.fromJson(entry.getKey(), entry.getValue(), pages, provider);
                if (parsed == null) {
                    LOGGER.warn("Etericon entry {} has no chapter", entry.getKey());
                    continue;
                }
                if (!chapters.containsKey(parsed.chapter())) {
                    LOGGER.warn("Etericon entry {} references unknown chapter {}", entry.getKey(), parsed.chapter());
                    continue;
                }
                entries.put(entry.getKey(), parsed);
            } catch (Exception exception) { LOGGER.error("Unable to parse Etericon entry {}", entry.getKey(), exception); }
        }
        BookCatalog catalog = new BookCatalog(chapters, entries, pages);
        for (EtericonValidator.Issue issue : EtericonValidator.validate(catalog)) {
            if (issue.severity() == EtericonValidator.Severity.ERROR) { LOGGER.error("Etericon validation: {}", issue.message()); }
            else { LOGGER.warn("Etericon validation: {}", issue.message()); }
        }
        return catalog;
    }
}
