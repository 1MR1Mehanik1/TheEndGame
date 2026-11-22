package gcapi_model_animation.api.geometry;

import com.google.gson.*;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ModelDisplay {
    public final Map<String, Transform> displays;

    public ModelDisplay(Map<String, Transform> displays) { this.displays = displays; }

    public Transform get(String key) { return displays.getOrDefault(key, Transform.IDENTITY); }

    public static ModelDisplay load(ResourceLocation location) {
        try {
            Optional<Resource> res = Minecraft.getInstance().getResourceManager().getResource(location);
            if (res.isPresent()) {
                try (InputStream in = res.get().open();
                     InputStreamReader reader = new InputStreamReader(in)) {
                    JsonObject root = new Gson().fromJson(reader, JsonObject.class);
                    if (root.has("display")) return fromJsonObject(root.getAsJsonObject("display"));
                }
            }
        } catch (Exception ignored) { }

        try (InputStream stream = ModelDisplay.class.getResourceAsStream("/assets/" + location.getNamespace() + "/" + location.getPath())) {
            if (stream != null) {
                JsonObject root = new Gson().fromJson(new InputStreamReader(stream), JsonObject.class);
                if (root.has("display")) return fromJsonObject(root.getAsJsonObject("display"));
            }
        } catch (Exception ignored) { }
        return EMPTY;
    }

    public static ModelDisplay fromJsonObject(JsonObject displayObj) {
        Map<String, Transform> map = new HashMap<>();
        for (Map.Entry<String, JsonElement> entry : displayObj.entrySet()) {
            JsonObject obj = entry.getValue().getAsJsonObject();
            float[] rotation    = readFloatArray(obj, "rotation", 3);
            float[] translation = readFloatArray(obj, "translation", 3);
            float[] scale       = readFloatArray(obj, "scale", 3);
            if (rotation != null || translation != null || scale != null) {
                map.put(entry.getKey(), new Transform(rotation, translation, scale));
            }
        }
        return new ModelDisplay(map);
    }

    private static float[] readFloatArray(JsonObject obj, String key, int size) {
        if (!obj.has(key)) return null;
        JsonArray arr = obj.getAsJsonArray(key);
        float[] out = new float[size];
        for (int i = 0; i < size; i++) out[i] = arr.get(i).getAsFloat();
        return out;
    }

    public static final ModelDisplay EMPTY = new ModelDisplay(new HashMap<>());

    public static class Transform {
        public static final Transform IDENTITY = new Transform(null, null, null);
        public final float[] rotation;
        public final float[] translation;
        public final float[] scale;
        public Transform(float[] r, float[] t, float[] s) { rotation = r; translation = t; scale = s; }
        public boolean hasRotation() { return rotation != null; }
        public boolean hasTranslation() { return translation != null; }
        public boolean hasScale() { return scale != null; }
    }
}