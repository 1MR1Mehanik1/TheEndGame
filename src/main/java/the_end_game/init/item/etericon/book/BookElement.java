package the_end_game.init.item.etericon.book;

import com.google.gson.JsonObject;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;

public final class BookElement {
    private final BookContent content;
    private Integer x;
    private Integer y;
    private Integer width;
    private Integer height;
    private int gap = 2;

    public BookElement(BookContent content) { this.content = content; }
    public BookContent content() { return this.content; }

    public Integer x() { return this.x; }
    public Integer y() { return this.y; }

    public Integer width() { return this.width; }
    public Integer height() { return this.height; }

    public int gap() { return this.gap; }

    public BookElement at(int x, int y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public BookElement width(int width) {
        this.width = width;
        return this;
    }

    public BookElement height(int height) {
        this.height = height;
        return this;
    }

    public BookElement gap(int gap) {
        this.gap = gap;
        return this;
    }

    public JsonObject toJson(HolderLookup.Provider provider) {
        JsonObject json = this.content.toJson(provider);
        if (this.x != null) { json.addProperty("x", this.x); }
        if (this.y != null) { json.addProperty("y", this.y); }
        if (this.width != null) { json.addProperty("width", this.width); }
        if (this.height != null) { json.addProperty("height", this.height); }
        if (this.gap != 0) { json.addProperty("gap", this.gap); }
        return json;
    }

    public static BookElement fromJson(JsonObject json, HolderLookup.Provider provider) {
        BookContent content = BookContent.fromJson(json, provider);
        if (content == null) { return null; }
        BookElement element = new BookElement(content);
        if (json.has("x")) { element.x = json.get("x").getAsInt(); }
        if (json.has("y")) { element.y = json.get("y").getAsInt(); }
        if (json.has("width")) { element.width = json.get("width").getAsInt(); }
        if (json.has("height")) { element.height = json.get("height").getAsInt(); }
        if (json.has("gap")) { element.gap = json.get("gap").getAsInt(); }
        return element;
    }

    public static BookElement title(net.minecraft.network.chat.Component text) {
        return new BookElement(new BookContent.Title(text, true, 0.95F, 0xFF2A1A0E));
    }

    public static BookElement text(net.minecraft.network.chat.Component text) {
        return new BookElement(new BookContent.Text(text, 0.62F, 0xFF3B2A1A, false));
    }

    public static BookElement textTitle(net.minecraft.network.chat.Component title, net.minecraft.network.chat.Component text) {
        return new BookElement(new BookContent.TextTitle(title, text, 0.62F));
    }

    public static BookElement image(BookIcon icon) {
        return new BookElement(new BookContent.Image(icon, net.minecraft.network.chat.Component.empty()));
    }

    public static BookElement image(BookIcon icon, net.minecraft.network.chat.Component caption) {
        return new BookElement(new BookContent.Image(icon, caption));
    }

    public static BookElement imageText(BookIcon icon, net.minecraft.network.chat.Component text) {
        return new BookElement(new BookContent.ImageText(icon, text, true, 40, 0.62F, net.minecraft.network.chat.Component.empty()));
    }

    public static BookElement container(BookElement... children) {
        return new BookElement(new BookContent.Container(java.util.List.of(children), false, 2));
    }

    public static BookElement recipe(ResourceLocation recipe) {
        return new BookElement(new BookContent.RecipeLink(recipe, java.util.List.of(), net.minecraft.world.item.ItemStack.EMPTY, net.minecraft.network.chat.Component.empty()));
    }

    public static BookElement crafting(java.util.List<net.minecraft.world.item.ItemStack> grid, net.minecraft.world.item.ItemStack result) {
        return new BookElement(new BookContent.Crafting(grid, result, net.minecraft.network.chat.Component.empty()));
    }

    public static BookElement entity(ResourceLocation entity) {
        return new BookElement(new BookContent.Entity(entity, 1.0F, true, net.minecraft.network.chat.Component.empty()));
    }

    public static BookElement item(net.minecraft.world.item.ItemStack stack) {
        return new BookElement(new BookContent.Item(stack, 1.0F, net.minecraft.network.chat.Component.empty()));
    }

    public static BookElement link(net.minecraft.network.chat.Component text, ResourceLocation target) {
        return new BookElement(new BookContent.Link(text, target));
    }
}
