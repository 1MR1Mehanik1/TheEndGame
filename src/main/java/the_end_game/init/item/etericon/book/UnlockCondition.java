package the_end_game.init.item.etericon.book;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;

public sealed interface UnlockCondition permits UnlockCondition.Research, UnlockCondition.Theory, UnlockCondition.Craft, UnlockCondition.Study, UnlockCondition.Kill {
    UnlockShape shape();
    boolean isSatisfied(BookProgress progress);
    JsonObject toJson();

    static UnlockCondition fromJson(JsonObject json) {
        if (json == null || !json.has("type")) { return null; }
        String type = json.get("type").getAsString();
        return switch (type) {
            case "research" -> new Research(json.has("points") ? json.get("points").getAsInt() : 0);
            case "theory" -> {
                ResourceLocation id = readLocation(json, "theory");
                yield id == null ? null : new Theory(id);
            }
            case "craft" -> {
                ResourceLocation id = readLocation(json, "item");
                yield id == null ? null : new Craft(id);
            }
            case "study" -> {
                ResourceLocation id = readLocation(json, "target");
                yield id == null ? null : new Study(id);
            }
            case "kill" -> {
                ResourceLocation id = readLocation(json, "entity");
                yield id == null ? null : new Kill(id, json.has("count") ? json.get("count").getAsInt() : 1);
            }
            default -> null;
        };
    }

    private static ResourceLocation readLocation(JsonObject json, String key) {
        if (!json.has(key)) { return null; }
        return ResourceLocation.tryParse(json.get(key).getAsString());
    }

    record Research(int points) implements UnlockCondition {
        public Research { points = Math.max(0, points); }

        @Override public UnlockShape shape() { return UnlockShape.CIRCLE; }
        @Override public boolean isSatisfied(BookProgress progress) { return false; }
        @Override public JsonObject toJson() {
            JsonObject json = new JsonObject();
            json.addProperty("type", "research");
            json.addProperty("points", this.points);
            return json;
        }
    }

    record Theory(ResourceLocation theory) implements UnlockCondition {
        @Override public UnlockShape shape() { return UnlockShape.SQUARE; }
        @Override public boolean isSatisfied(BookProgress progress) { return progress.completedTheories().contains(this.theory); }
        @Override public JsonObject toJson() {
            JsonObject json = new JsonObject();
            json.addProperty("type", "theory");
            json.addProperty("theory", this.theory.toString());
            return json;
        }
    }

    record Craft(ResourceLocation item) implements UnlockCondition {
        @Override public UnlockShape shape() { return UnlockShape.STAR4; }
        @Override public boolean isSatisfied(BookProgress progress) { return progress.craftedItems().contains(this.item); }
        @Override public JsonObject toJson() {
            JsonObject json = new JsonObject();
            json.addProperty("type", "craft");
            json.addProperty("item", this.item.toString());
            return json;
        }
    }

    record Study(ResourceLocation target) implements UnlockCondition {
        @Override public UnlockShape shape() { return UnlockShape.PENTAGON; }
        @Override public boolean isSatisfied(BookProgress progress) { return progress.studiedTargets().contains(this.target); }
        @Override public JsonObject toJson() {
            JsonObject json = new JsonObject();
            json.addProperty("type", "study");
            json.addProperty("target", this.target.toString());
            return json;
        }
    }

    record Kill(ResourceLocation entity, int count) implements UnlockCondition {
        public Kill { count = Math.max(1, count); }

        @Override public UnlockShape shape() { return UnlockShape.STAR8; }
        @Override public boolean isSatisfied(BookProgress progress) { return progress.kills().getOrDefault(this.entity, 0) >= this.count; }
        @Override public JsonObject toJson() {
            JsonObject json = new JsonObject();
            json.addProperty("type", "kill");
            json.addProperty("entity", this.entity.toString());
            json.addProperty("count", this.count);
            return json;
        }
    }
}
