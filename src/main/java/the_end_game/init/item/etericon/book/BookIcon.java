package the_end_game.init.item.etericon.book;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public sealed interface BookIcon permits BookIcon.ItemIcon, BookIcon.AtlasIcon, BookIcon.TextureIcon {
    ResourceLocation ETERICON_GUI = ResourceLocation.fromNamespaceAndPath("the_end_game", "textures/other/book/etericon_gui.png");

    int width();
    int height();
    JsonObject toJson();

    private static int defaultTextureSize(ResourceLocation texture) {
        return ETERICON_GUI.equals(texture) ? 542 : 256;
    }

    static BookIcon fromJson(JsonElement element) {
        if (element == null || element.isJsonNull()) { return null; }
        if (element.isJsonPrimitive()) {
            ItemStack stack = BookJson.parseStack(element);
            return new ItemIcon(stack, 16, 16);
        }
        if (!element.isJsonObject()) { return null; }
        JsonObject json = element.getAsJsonObject();
        if (json.has("item")) {
            ItemStack stack = BookJson.parseStack(json);
            int width = json.has("width") ? json.get("width").getAsInt() : 16;
            int height = json.has("height") ? json.get("height").getAsInt() : 16;
            return new ItemIcon(stack, width, height);
        }
        if (json.has("atlas")) {
            ResourceLocation texture = ResourceLocation.tryParse(json.get("atlas").getAsString());
            if (texture == null) { return null; }
            int u = json.has("u") ? json.get("u").getAsInt() : 0;
            int v = json.has("v") ? json.get("v").getAsInt() : 0;
            int sw = json.has("w") ? json.get("w").getAsInt() : 16;
            int sh = json.has("h") ? json.get("h").getAsInt() : 16;
            int tw = json.has("texture_width") ? json.get("texture_width").getAsInt() : defaultTextureSize(texture);
            int th = json.has("texture_height") ? json.get("texture_height").getAsInt() : defaultTextureSize(texture);
            int width = json.has("width") ? json.get("width").getAsInt() : sw;
            int height = json.has("height") ? json.get("height").getAsInt() : sh;
            return new AtlasIcon(texture, tw, th, u, v, sw, sh, width, height);
        }
        if (json.has("texture")) {
            ResourceLocation texture = ResourceLocation.tryParse(json.get("texture").getAsString());
            if (texture == null) { return null; }
            int tw = json.has("texture_width") ? json.get("texture_width").getAsInt() : defaultTextureSize(texture);
            int th = json.has("texture_height") ? json.get("texture_height").getAsInt() : defaultTextureSize(texture);
            int width = json.has("width") ? json.get("width").getAsInt() : tw;
            int height = json.has("height") ? json.get("height").getAsInt() : th;
            return new TextureIcon(texture, tw, th, width, height);
        }
        return null;
    }

    record ItemIcon(ItemStack stack, int width, int height) implements BookIcon {
        public ItemIcon {
            stack = stack.isEmpty() ? new ItemStack(Items.BOOK) : stack.copy();
            width = Math.max(1, width);
            height = Math.max(1, height);
        }

        @Override public JsonObject toJson() {
            JsonObject json = new JsonObject();
            ResourceLocation id = BuiltInRegistries.ITEM.getKey(this.stack.getItem());
            json.addProperty("item", id.toString());
            json.addProperty("count", Math.max(1, this.stack.getCount()));
            json.addProperty("width", this.width);
            json.addProperty("height", this.height);
            return json;
        }
    }

    record AtlasIcon(ResourceLocation texture, int textureWidth, int textureHeight, int u, int v, int sourceWidth, int sourceHeight, int width, int height) implements BookIcon {
        public AtlasIcon {
            textureWidth = Math.max(1, textureWidth);
            textureHeight = Math.max(1, textureHeight);
            sourceWidth = Math.max(1, sourceWidth);
            sourceHeight = Math.max(1, sourceHeight);
            width = Math.max(1, width);
            height = Math.max(1, height);
        }

        @Override public JsonObject toJson() {
            JsonObject json = new JsonObject();
            json.addProperty("atlas", this.texture.toString());
            json.addProperty("texture_width", this.textureWidth);
            json.addProperty("texture_height", this.textureHeight);
            json.addProperty("u", this.u);
            json.addProperty("v", this.v);
            json.addProperty("w", this.sourceWidth);
            json.addProperty("h", this.sourceHeight);
            json.addProperty("width", this.width);
            json.addProperty("height", this.height);
            return json;
        }
    }

    record TextureIcon(ResourceLocation texture, int textureWidth, int textureHeight, int width, int height) implements BookIcon {
        public TextureIcon {
            textureWidth = Math.max(1, textureWidth);
            textureHeight = Math.max(1, textureHeight);
            width = Math.max(1, width);
            height = Math.max(1, height);
        }

        @Override public JsonObject toJson() {
            JsonObject json = new JsonObject();
            json.addProperty("texture", this.texture.toString());
            json.addProperty("texture_width", this.textureWidth);
            json.addProperty("texture_height", this.textureHeight);
            json.addProperty("width", this.width);
            json.addProperty("height", this.height);
            return json;
        }
    }
}
