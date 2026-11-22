package the_end_game.handler.vanilla_3x3;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class BelowRecipeListener extends SimpleJsonResourceReloadListener {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final HolderLookup.Provider registries;

    public BelowRecipeListener(HolderLookup.Provider registries) {
        super(new Gson(), "recipe");
        this.registries = registries;
    }

    @Override protected void apply(Map<ResourceLocation, JsonElement> resources, ResourceManager manager, ProfilerFiller profiler) {
        RegistryOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, this.registries);
        Map<ResourceLocation, BelowData.Requirement> parsed = new HashMap<>();
        resources.forEach((id, json) -> {
            if (!json.isJsonObject()) { return; }
            JsonElement below = json.getAsJsonObject().get("below");
            if (below == null) { return; }

            JsonObject belowObject;
            if (below.isJsonPrimitive()) {
                belowObject = new JsonObject();
                belowObject.add("blocks", below);
            } else if (below.isJsonObject()) { belowObject = below.getAsJsonObject().deepCopy(); } else { return; }

            if (!belowObject.has("blocks") && belowObject.has("block")) { belowObject.add("blocks", belowObject.remove("block")); }
            normalizeState(belowObject);

            ResourceLocation recipeId = id.getPath().startsWith("recipe/") ? ResourceLocation.fromNamespaceAndPath(id.getNamespace(), id.getPath().substring("recipe/".length())) : id;
            BlockPredicate.CODEC.parse(ops, belowObject).resultOrPartial(error -> LOGGER.error("Invalid below in recipe {}: {}", recipeId, error)).ifPresent(predicate -> {
                if (predicate.blocks().isEmpty() && predicate.properties().isEmpty() && predicate.nbt().isEmpty()) {
                    LOGGER.error("Recipe {} has an empty below predicate", recipeId);
                    return;
                }
                parsed.put(recipeId, new BelowData.Requirement(predicate, state(belowObject), hasNbt(belowObject), parseInfo(belowObject.get("info"))));
            });
        });
        BelowData.set(parsed);
    }

    private Component parseInfo(JsonElement element) {
        if (element == null || element.isJsonNull()) { return null; }
        try {
            return Component.Serializer.fromJson(element, this.registries);
        } catch (Exception exception) {
            LOGGER.error("Invalid info component in below", exception);
            return null;
        }
    }

    private static void normalizeState(JsonObject below) {
        JsonElement state = below.get("state");
        if (state == null || !state.isJsonObject()) { return; }
        JsonObject stateObject = state.getAsJsonObject();
        List<String> keys = new ArrayList<>(stateObject.keySet());
        for (String key : keys) {
            JsonElement value = stateObject.get(key);
            if (value.isJsonPrimitive() && !value.getAsJsonPrimitive().isString()) { stateObject.addProperty(key, value.getAsString()); }
        }
    }

    private static String state(JsonObject below) {
        JsonElement state = below.get("state");
        if (state == null || !state.isJsonObject()) { return ""; }
        List<String> parts = new ArrayList<>();
        state.getAsJsonObject().entrySet().forEach(entry -> {
            JsonElement value = entry.getValue();
            parts.add(entry.getKey() + "=" + (value.isJsonPrimitive() ? value.getAsString() : value.toString()));
        });
        return String.join(", ", parts);
    }

    private static boolean hasNbt(JsonObject below) {
        JsonElement nbt = below.get("nbt");
        return nbt != null && !nbt.isJsonNull();
    }
}
