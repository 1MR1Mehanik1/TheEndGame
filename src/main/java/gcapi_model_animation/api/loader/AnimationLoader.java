package gcapi_model_animation.api.loader;

import com.google.gson.*;
import gcapi_model_animation.api.animation.*;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class AnimationLoader {
    private static final Gson GSON = new Gson();

    public static Map<String, AnimationData> load(ResourceLocation location) {
        try {
            Optional<Resource> res = Minecraft.getInstance().getResourceManager().getResource(location);
            if (res.isPresent()) { try (InputStream in = res.get().open()) { return parseStream(in); } }
        } catch (Exception ignored) { }

        try (InputStream stream = AnimationLoader.class.getResourceAsStream("/assets/" + location.getNamespace() + "/" + location.getPath())) {
            if (stream != null) return parseStream(stream);
        } catch (Exception ex) { ex.printStackTrace(); }
        return null;
    }

    private static Map<String, AnimationData> parseStream(InputStream stream) {
        try (InputStreamReader reader = new InputStreamReader(stream)) {
            JsonObject root = GSON.fromJson(reader, JsonObject.class);
            JsonObject animationsObj = root.getAsJsonObject("animations");
            Map<String, AnimationData> result = new HashMap<>();
            if (animationsObj != null) {
                for (Map.Entry<String, JsonElement> e : animationsObj.entrySet()) {
                    result.put(e.getKey(), parseAnimation(e.getValue().getAsJsonObject()));
                }
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static AnimationData parseAnimation(JsonObject animObj) {
        double length = animObj.has("animation_length") ? animObj.get("animation_length").getAsDouble() : 0;
        boolean loop = animObj.has("loop") && animObj.get("loop").getAsBoolean();
        Map<String, BoneAnimation> boneAnims = new HashMap<>();
        JsonObject bonesObj = animObj.getAsJsonObject("bones");
        if (bonesObj != null) {
            for (Map.Entry<String, JsonElement> e : bonesObj.entrySet()) {
                boneAnims.put(e.getKey(), parseBoneAnimation(e.getValue().getAsJsonObject()));
            }
        }
        return new AnimationData(length, loop, boneAnims);
    }

    private static BoneAnimation parseBoneAnimation(JsonObject boneObj) {
        List<Keyframe> positionKeys = new ArrayList<>();
        List<Keyframe> rotationKeys = new ArrayList<>();
        List<Keyframe> scaleKeys = new ArrayList<>();
        if (boneObj.has("position")) parseKeyframes(boneObj.getAsJsonObject("position"), positionKeys, false);
        if (boneObj.has("rotation")) parseKeyframes(boneObj.getAsJsonObject("rotation"), rotationKeys, false);
        if (boneObj.has("scale")) parseKeyframes(boneObj.getAsJsonObject("scale"), scaleKeys, true);
        return new BoneAnimation(positionKeys, rotationKeys, scaleKeys);
    }

    private static void parseKeyframes(JsonObject channelObj, List<Keyframe> list, boolean isScale) {
        for (Map.Entry<String, JsonElement> e : channelObj.entrySet()) {
            float time = Float.parseFloat(e.getKey());
            JsonElement v = e.getValue();
            float x = 0, y = 0, z = 0;
            if (v.isJsonObject()) {
                JsonObject obj = v.getAsJsonObject();
                if (obj.has("post") && obj.get("post").isJsonObject()) {
                    JsonArray vec = obj.getAsJsonObject("post").getAsJsonArray("vector");
                    x = vec.get(0).getAsFloat(); y = vec.get(1).getAsFloat(); z = vec.get(2).getAsFloat();
                } else if (obj.has("vector")) {
                    JsonArray vec = obj.getAsJsonArray("vector");
                    x = vec.get(0).getAsFloat(); y = vec.get(1).getAsFloat(); z = vec.get(2).getAsFloat();
                }
            } else if (v.isJsonArray()) {
                JsonArray a = v.getAsJsonArray();
                x = a.get(0).getAsFloat(); y = a.get(1).getAsFloat(); z = a.get(2).getAsFloat();
            } else if (v.isJsonPrimitive()) {
                x = v.getAsFloat(); y = x; z = x;
            }
            list.add(new Keyframe(time, x, y, z));
        }
        list.sort(Comparator.comparingDouble(k -> k.time));
    }
}