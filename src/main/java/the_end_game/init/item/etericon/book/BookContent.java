package the_end_game.init.item.etericon.book;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public sealed interface BookContent permits BookContent.Title, BookContent.Text, BookContent.TextTitle, BookContent.Image, BookContent.ImageText, BookContent.Container, BookContent.Crafting, BookContent.RecipeLink, BookContent.Entity, BookContent.Item, BookContent.Link, BookContent.Multiblock, BookContent.Empty {
    String type();
    JsonObject toJson(HolderLookup.Provider provider);

    static BookContent fromJson(JsonObject json, HolderLookup.Provider provider) {
        if (json == null || !json.has("type")) { return null; }
        String type = json.get("type").getAsString();
        return switch (type) {
            case "title" -> new Title(
                    BookJson.parseComponent(json.get("text"), provider),
                    !json.has("center") || json.get("center").getAsBoolean(),
                    json.has("scale") ? json.get("scale").getAsFloat() : 0.95F,
                    BookJson.parseColor(json.get("color"), 0xFF2A1A0E)
            );
            case "text" -> new Text(
                    BookJson.parseComponent(json.get("text"), provider),
                    json.has("scale") ? json.get("scale").getAsFloat() : 0.62F,
                    BookJson.parseColor(json.get("color"), 0xFF3B2A1A),
                    json.has("shadow") && json.get("shadow").getAsBoolean()
            );
            case "text_title" -> new TextTitle(
                    BookJson.parseComponent(json.get("title"), provider),
                    BookJson.parseComponent(json.get("text"), provider),
                    json.has("scale") ? json.get("scale").getAsFloat() : 0.62F
            );
            case "image" -> {
                BookIcon icon = BookIcon.fromJson(json.get("image"));
                yield icon == null ? null : new Image(icon, BookJson.parseComponent(json.get("caption"), provider));
            }
            case "image_text" -> {
                BookIcon icon = BookIcon.fromJson(json.get("image"));
                if (icon == null) { yield null; }
                yield new ImageText(icon, BookJson.parseComponent(json.get("text"), provider),
                        !json.has("image_left") || json.get("image_left").getAsBoolean(),
                        json.has("image_width") ? json.get("image_width").getAsInt() : 40,
                        json.has("scale") ? json.get("scale").getAsFloat() : 0.62F,
                        BookJson.parseComponent(json.get("caption"), provider));
            }
            case "container" -> {
                List<BookElement> children = new ArrayList<>();
                if (json.has("children") && json.get("children").isJsonArray()) {
                    for (JsonElement child : json.getAsJsonArray("children")) {
                        if (child.isJsonObject()) {
                            BookElement element = BookElement.fromJson(child.getAsJsonObject(), provider);
                            if (element != null) { children.add(element); }
                        }
                    }
                }
                yield new Container(children, json.has("horizontal") && json.get("horizontal").getAsBoolean(),
                        json.has("gap") ? json.get("gap").getAsInt() : 2);
            }
            case "crafting" -> {
                List<ItemStack> grid = readGrid(json.get("grid"));
                ItemStack result = BookJson.parseStack(json.get("result"));
                yield new Crafting(grid, result, BookJson.parseComponent(json.get("caption"), provider));
            }
            case "recipe" -> {
                ResourceLocation recipe = json.has("recipe") ? ResourceLocation.tryParse(json.get("recipe").getAsString()) : null;
                List<ItemStack> grid = readGrid(json.get("grid"));
                ItemStack result = BookJson.parseStack(json.get("result"));
                if (recipe == null && result.isEmpty() && grid.stream().allMatch(ItemStack::isEmpty)) { yield null; }
                yield new RecipeLink(recipe, grid, result, BookJson.parseComponent(json.get("caption"), provider));
            }
            case "multiblock" -> {
                if (!json.has("layers") || !json.get("layers").isJsonArray()) { yield null; }
                List<List<String>> layers = new ArrayList<>();
                for (JsonElement layerElement : json.getAsJsonArray("layers")) {
                    if (!layerElement.isJsonArray()) { continue; }
                    List<String> rows = new ArrayList<>();
                    for (JsonElement rowElement : layerElement.getAsJsonArray()) { rows.add(rowElement.getAsString()); }
                    layers.add(rows);
                }
                Map<Character, ResourceLocation> symbols = new LinkedHashMap<>();
                if (json.has("symbols") && json.get("symbols").isJsonObject()) {
                    for (Map.Entry<String, JsonElement> entry : json.getAsJsonObject("symbols").entrySet()) {
                        if (entry.getKey().isEmpty()) { continue; }
                        ResourceLocation block = ResourceLocation.tryParse(entry.getValue().getAsString());
                        if (block != null) { symbols.put(entry.getKey().charAt(0), block); }
                    }
                }
                ResourceLocation id = ResourceLocation.tryParse(json.has("id") ? json.get("id").getAsString() : "the_end_game:inline");
                char anchor = json.has("anchor") && !json.get("anchor").getAsString().isEmpty() ? json.get("anchor").getAsString().charAt(0) : '0';
                yield new Multiblock(id == null ? ResourceLocation.fromNamespaceAndPath("the_end_game", "inline") : id, layers, symbols, anchor);
            }
            case "entity" -> {
                if (!json.has("entity")) { yield null; }
                ResourceLocation entity = ResourceLocation.tryParse(json.get("entity").getAsString());
                yield entity == null ? null : new Entity(entity, json.has("scale") ? json.get("scale").getAsFloat() : 1.0F, !json.has("rotate") || json.get("rotate").getAsBoolean(), BookJson.parseComponent(json.get("caption"), provider));
            }
            case "item" -> {
                ItemStack stack = BookJson.parseStack(json);
                yield stack.isEmpty() ? null : new Item(stack, json.has("scale") ? json.get("scale").getAsFloat() : 1.0F, BookJson.parseComponent(json.get("caption"), provider));
            }
            case "link" -> {
                if (!json.has("target")) { yield null; }
                ResourceLocation target = ResourceLocation.tryParse(json.get("target").getAsString());
                yield target == null ? null : new Link(BookJson.parseComponent(json.get("text"), provider), target);
            }
            case "empty" -> new Empty();
            default -> null;
        };
    }

    private static List<ItemStack> readGrid(JsonElement element) {
        List<ItemStack> grid = new ArrayList<>();
        if (element != null && element.isJsonArray()) {
            JsonArray array = element.getAsJsonArray();
            for (int i = 0; i < 9; i++) { grid.add(i < array.size() ? BookJson.parseStack(array.get(i)) : ItemStack.EMPTY); }
        } else {
            for (int i = 0; i < 9; i++) { grid.add(ItemStack.EMPTY); }
        }
        return grid;
    }

    record Title(Component text, boolean center, float scale, int color) implements BookContent {
        public Title {
            text = text == null ? Component.empty() : text;
            scale = Math.max(0.2F, scale);
        }

        @Override public String type() { return "title"; }
        @Override public JsonObject toJson(HolderLookup.Provider provider) {
            JsonObject json = new JsonObject();
            json.addProperty("type", "title");
            json.add("text", BookJson.componentToJson(this.text, provider));
            json.addProperty("center", this.center);
            json.addProperty("scale", this.scale);
            json.addProperty("color", BookJson.colorToJson(this.color));
            return json;
        }
    }

    record Text(Component text, float scale, int color, boolean shadow) implements BookContent {
        public Text {
            text = text == null ? Component.empty() : text;
            scale = Math.max(0.2F, scale);
        }

        @Override public String type() { return "text"; }
        @Override public JsonObject toJson(HolderLookup.Provider provider) {
            JsonObject json = new JsonObject();
            json.addProperty("type", "text");
            json.add("text", BookJson.componentToJson(this.text, provider));
            json.addProperty("scale", this.scale);
            json.addProperty("color", BookJson.colorToJson(this.color));
            json.addProperty("shadow", this.shadow);
            return json;
        }
    }

    record TextTitle(Component title, Component text, float scale) implements BookContent {
        public TextTitle {
            title = title == null ? Component.empty() : title;
            text = text == null ? Component.empty() : text;
            scale = Math.max(0.2F, scale);
        }

        @Override public String type() { return "text_title"; }
        @Override public JsonObject toJson(HolderLookup.Provider provider) {
            JsonObject json = new JsonObject();
            json.addProperty("type", "text_title");
            json.add("title", BookJson.componentToJson(this.title, provider));
            json.add("text", BookJson.componentToJson(this.text, provider));
            json.addProperty("scale", this.scale);
            return json;
        }
    }

    record Image(BookIcon icon, Component caption) implements BookContent {
        public Image { caption = caption == null ? Component.empty() : caption; }

        @Override public String type() { return "image"; }
        @Override public JsonObject toJson(HolderLookup.Provider provider) {
            JsonObject json = new JsonObject();
            json.addProperty("type", "image");
            json.add("image", this.icon.toJson());
            json.add("caption", BookJson.componentToJson(this.caption, provider));
            return json;
        }
    }

    record ImageText(BookIcon icon, Component text, boolean imageLeft, int imageWidth, float scale, Component caption) implements BookContent {
        public ImageText {
            text = text == null ? Component.empty() : text;
            imageWidth = Math.max(8, imageWidth);
            scale = Math.max(0.2F, scale);
            caption = caption == null ? Component.empty() : caption;
        }

        @Override public String type() { return "image_text"; }
        @Override public JsonObject toJson(HolderLookup.Provider provider) {
            JsonObject json = new JsonObject();
            json.addProperty("type", "image_text");
            json.add("image", this.icon.toJson());
            json.add("text", BookJson.componentToJson(this.text, provider));
            json.addProperty("image_left", this.imageLeft);
            json.addProperty("image_width", this.imageWidth);
            json.addProperty("scale", this.scale);
            json.add("caption", BookJson.componentToJson(this.caption, provider));
            return json;
        }
    }

    record Container(List<BookElement> children, boolean horizontal, int gap) implements BookContent {
        public Container {
            children = List.copyOf(children);
            gap = Math.max(0, gap);
        }

        @Override public String type() { return "container"; }
        @Override public JsonObject toJson(HolderLookup.Provider provider) {
            JsonObject json = new JsonObject();
            json.addProperty("type", "container");
            json.addProperty("horizontal", this.horizontal);
            json.addProperty("gap", this.gap);
            JsonArray array = new JsonArray();
            for (BookElement child : this.children) { array.add(child.toJson(provider)); }
            json.add("children", array);
            return json;
        }
    }

    record Crafting(List<ItemStack> grid, ItemStack result, Component caption) implements BookContent {
        public Crafting {
            List<ItemStack> copy = new ArrayList<>();
            for (int i = 0; i < 9; i++) { copy.add(i < grid.size() ? grid.get(i).copy() : ItemStack.EMPTY); }
            grid = List.copyOf(copy);
            result = result == null ? ItemStack.EMPTY : result.copy();
            caption = caption == null ? Component.empty() : caption;
        }

        @Override public String type() { return "crafting"; }
        @Override public JsonObject toJson(HolderLookup.Provider provider) {
            JsonObject json = new JsonObject();
            json.addProperty("type", "crafting");
            JsonArray array = new JsonArray();
            for (ItemStack stack : this.grid) { array.add(BookJson.stackToJson(stack)); }
            json.add("grid", array);
            json.add("result", BookJson.stackToJson(this.result));
            json.add("caption", BookJson.componentToJson(this.caption, provider));
            return json;
        }
    }

    record RecipeLink(ResourceLocation recipe, List<ItemStack> grid, ItemStack result, Component caption) implements BookContent {
        public RecipeLink {
            List<ItemStack> copy = new ArrayList<>();
            for (int i = 0; i < 9; i++) { copy.add(i < grid.size() ? grid.get(i).copy() : ItemStack.EMPTY); }
            grid = List.copyOf(copy);
            result = result == null ? ItemStack.EMPTY : result.copy();
            caption = caption == null ? Component.empty() : caption;
        }

        @Override public String type() { return "recipe"; }
        @Override public JsonObject toJson(HolderLookup.Provider provider) {
            JsonObject json = new JsonObject();
            json.addProperty("type", "recipe");
            if (this.recipe != null) { json.addProperty("recipe", this.recipe.toString()); }
            JsonArray array = new JsonArray();
            for (ItemStack stack : this.grid) { array.add(BookJson.stackToJson(stack)); }
            json.add("grid", array);
            if (!this.result.isEmpty()) { json.add("result", BookJson.stackToJson(this.result)); }
            json.add("caption", BookJson.componentToJson(this.caption, provider));
            return json;
        }
    }

    record Multiblock(ResourceLocation id, List<List<String>> layers, Map<Character, ResourceLocation> symbols, char anchor) implements BookContent {
        public Multiblock {
            layers = layers.stream().map(List::copyOf).toList();
            symbols = Map.copyOf(symbols);
        }

        public the_end_game.init.item.etericon.multiblock.Multiblock toMultiblock() {
            Map<Character, the_end_game.init.item.etericon.multiblock.Matcher> matchers = new java.util.LinkedHashMap<>();
            for (Map.Entry<Character, ResourceLocation> entry : this.symbols.entrySet()) {
                net.minecraft.world.level.block.Block block = net.minecraft.core.registries.BuiltInRegistries.BLOCK.get(entry.getValue());
                if (block != null && block != net.minecraft.world.level.block.Blocks.AIR) {
                    matchers.put(entry.getKey(), the_end_game.init.item.etericon.multiblock.Matcher.block(block));
                }
            }
            matchers.putIfAbsent('-', the_end_game.init.item.etericon.multiblock.Matcher.wildcard());
            return new the_end_game.init.item.etericon.multiblock.Multiblock(this.id, this.layers, matchers);
        }

        @Override public String type() { return "multiblock"; }
        @Override public JsonObject toJson(HolderLookup.Provider provider) {
            JsonObject json = new JsonObject();
            json.addProperty("type", "multiblock");
            json.addProperty("id", this.id.toString());
            JsonArray layerArray = new JsonArray();
            for (List<String> layer : this.layers) {
                JsonArray rows = new JsonArray();
                for (String row : layer) { rows.add(row); }
                layerArray.add(rows);
            }
            json.add("layers", layerArray);
            JsonObject symbolObject = new JsonObject();
            for (Map.Entry<Character, ResourceLocation> entry : this.symbols.entrySet()) {
                symbolObject.addProperty(entry.getKey().toString(), entry.getValue().toString());
            }
            json.add("symbols", symbolObject);
            json.addProperty("anchor", String.valueOf(this.anchor));
            return json;
        }
    }

    record Entity(ResourceLocation entity, float scale, boolean rotate, Component caption) implements BookContent {
        public Entity {
            scale = Math.max(0.1F, scale);
            caption = caption == null ? Component.empty() : caption;
        }

        @Override public String type() { return "entity"; }
        @Override public JsonObject toJson(HolderLookup.Provider provider) {
            JsonObject json = new JsonObject();
            json.addProperty("type", "entity");
            json.addProperty("entity", this.entity.toString());
            json.addProperty("scale", this.scale);
            json.addProperty("rotate", this.rotate);
            json.add("caption", BookJson.componentToJson(this.caption, provider));
            return json;
        }
    }

    record Item(ItemStack stack, float scale, Component caption) implements BookContent {
        public Item {
            stack = stack == null ? ItemStack.EMPTY : stack.copy();
            scale = Math.max(0.2F, scale);
            caption = caption == null ? Component.empty() : caption;
        }

        @Override public String type() { return "item"; }
        @Override public JsonObject toJson(HolderLookup.Provider provider) {
            JsonObject json = new JsonObject();
            json.addProperty("type", "item");
            JsonElement stackJson = BookJson.stackToJson(this.stack);
            if (stackJson.isJsonObject()) {
                for (String key : stackJson.getAsJsonObject().keySet()) { json.add(key, stackJson.getAsJsonObject().get(key)); }
            } else { json.add("item", stackJson); }
            json.addProperty("scale", this.scale);
            json.add("caption", BookJson.componentToJson(this.caption, provider));
            return json;
        }
    }

    record Link(Component text, ResourceLocation target) implements BookContent {
        public Link { text = text == null ? Component.empty() : text; }

        @Override public String type() { return "link"; }
        @Override public JsonObject toJson(HolderLookup.Provider provider) {
            JsonObject json = new JsonObject();
            json.addProperty("type", "link");
            json.add("text", BookJson.componentToJson(this.text, provider));
            json.addProperty("target", this.target.toString());
            return json;
        }
    }

    record Empty() implements BookContent {
        @Override public String type() { return "empty"; }
        @Override public JsonObject toJson(HolderLookup.Provider provider) {
            JsonObject json = new JsonObject();
            json.addProperty("type", "empty");
            return json;
        }
    }
}
