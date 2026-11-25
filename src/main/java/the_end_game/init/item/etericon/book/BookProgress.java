package the_end_game.init.item.etericon.book;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class BookProgress {
    public static final int CURRENT_VERSION = 1;
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final Codec<BookProgress> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.optionalFieldOf("version", CURRENT_VERSION).forGetter(BookProgress::version),
            Codec.unboundedMap(ResourceLocation.CODEC, EntryState.CODEC).optionalFieldOf("states", Map.of()).forGetter(BookProgress::states),
            Codec.list(ResourceLocation.CODEC).optionalFieldOf("theories", List.of()).forGetter(progress -> new ArrayList<>(progress.theories)),
            Codec.list(ResourceLocation.CODEC).optionalFieldOf("crafted", List.of()).forGetter(progress -> new ArrayList<>(progress.crafted)),
            Codec.list(ResourceLocation.CODEC).optionalFieldOf("studied", List.of()).forGetter(progress -> new ArrayList<>(progress.studied)),
            Codec.unboundedMap(ResourceLocation.CODEC, Codec.INT).optionalFieldOf("kills", Map.of()).forGetter(BookProgress::kills),
            Codec.unboundedMap(ResourceLocation.CODEC, Codec.INT).optionalFieldOf("stages", Map.of()).forGetter(BookProgress::stages),
            Codec.list(ResourceLocation.CODEC).optionalFieldOf("forgotten", List.of()).forGetter(progress -> new ArrayList<>(progress.forgotten))
    ).apply(instance, BookProgress::new));

    private final int version;
    private final Map<ResourceLocation, EntryState> states;
    private final Set<ResourceLocation> theories;
    private final Set<ResourceLocation> crafted;
    private final Set<ResourceLocation> studied;
    private final Map<ResourceLocation, Integer> kills;
    private final Map<ResourceLocation, Integer> stages;
    private final Set<ResourceLocation> forgotten;

    public BookProgress() { this(CURRENT_VERSION, Map.of(), List.of(), List.of(), List.of(), Map.of(), Map.of(), List.of()); }

    public BookProgress(
            Map<ResourceLocation, EntryState> states,
            List<ResourceLocation> theories,
            List<ResourceLocation> crafted,
            List<ResourceLocation> studied,
            Map<ResourceLocation, Integer> kills
    ) {
        this(CURRENT_VERSION, states, theories, crafted, studied, kills, Map.of(), List.of());
    }

    public BookProgress(
            int version,
            Map<ResourceLocation, EntryState> states,
            List<ResourceLocation> theories,
            List<ResourceLocation> crafted,
            List<ResourceLocation> studied,
            Map<ResourceLocation, Integer> kills,
            List<ResourceLocation> forgotten
    ) {
        this(version, states, theories, crafted, studied, kills, Map.of(), forgotten);
    }

    public BookProgress(
            int version,
            Map<ResourceLocation, EntryState> states,
            List<ResourceLocation> theories,
            List<ResourceLocation> crafted,
            List<ResourceLocation> studied,
            Map<ResourceLocation, Integer> kills,
            Map<ResourceLocation, Integer> stages,
            List<ResourceLocation> forgotten
    ) {
        this.version = CURRENT_VERSION;
        if (version > CURRENT_VERSION) { LOGGER.warn("Etericon progress version {} is newer than {}", version, CURRENT_VERSION); }
        this.states = new HashMap<>(states);
        this.theories = new LinkedHashSet<>(theories);
        this.crafted = new LinkedHashSet<>(crafted);
        this.studied = new LinkedHashSet<>(studied);
        this.kills = new HashMap<>(kills);
        this.stages = new HashMap<>(stages);
        this.forgotten = new LinkedHashSet<>(forgotten);
    }

    public int version() { return this.version; }

    public Map<ResourceLocation, EntryState> states() { return Collections.unmodifiableMap(this.states); }

    public Set<ResourceLocation> completedTheories() { return Collections.unmodifiableSet(this.theories); }

    public Set<ResourceLocation> craftedItems() { return Collections.unmodifiableSet(this.crafted); }

    public Set<ResourceLocation> studiedTargets() { return Collections.unmodifiableSet(this.studied); }

    public Map<ResourceLocation, Integer> kills() { return Collections.unmodifiableMap(this.kills); }

    public Map<ResourceLocation, Integer> stages() { return Collections.unmodifiableMap(this.stages); }

    public int stageOf(ResourceLocation id) { return Math.max(0, this.stages.getOrDefault(id, 0)); }

    public int stageOf(BookEntry entry) { return stageOf(entry.id()); }

    public void setStage(ResourceLocation id, int stage) { this.stages.put(id, Math.max(0, stage)); }

    public void advanceStage(ResourceLocation id) { this.stages.merge(id, 1, Integer::sum); }

    public Set<ResourceLocation> forgotten() { return Collections.unmodifiableSet(this.forgotten); }

    public boolean isForgotten(ResourceLocation id) { return this.forgotten.contains(id); }

    public EntryState stateOf(BookEntry entry) { return this.states.getOrDefault(entry.id(), EntryState.LOCKED); }

    public EntryState stateOf(ResourceLocation id) { return this.states.getOrDefault(id, EntryState.LOCKED); }

    public void setState(ResourceLocation id, EntryState state) {
        if (state == EntryState.OPENED) { restore(id); }
        else if (state == EntryState.LOCKED) { forget(id); }
        else { this.states.put(id, state); this.forgotten.remove(id); }
    }

    public void restore(ResourceLocation id) {
        this.forgotten.remove(id);
        this.states.put(id, EntryState.OPENED);
    }

    public void forget(ResourceLocation id) {
        this.forgotten.add(id);
        this.states.put(id, EntryState.LOCKED);
        this.stages.remove(id);
    }

    public void completeTheory(ResourceLocation theory) { this.theories.add(theory); }

    public void markCrafted(ResourceLocation item) { this.crafted.add(item); }

    public void markStudied(ResourceLocation target) { this.studied.add(target); }

    public void addKill(ResourceLocation entity, int count) { if (count > 0) { this.kills.merge(entity, count, Integer::sum); } }

    public void open(BookEntry entry) {
        this.forgotten.remove(entry.id());
        this.states.put(entry.id(), EntryState.OPENED);
    }

    public boolean requirementsMet(BookEntry entry) { return EntryAccess.requirementsMet(entry, this); }

    public void recalculate(BookCatalog catalog) {
        for (BookEntry entry : catalog.entries().values()) {
            if (this.forgotten.contains(entry.id())) {
                this.states.put(entry.id(), EntryState.LOCKED);
                this.stages.remove(entry.id());
                continue;
            }
            EntryState current = this.states.getOrDefault(entry.id(), EntryState.LOCKED);
            if (current == EntryState.OPENED) {
                this.states.put(entry.id(), EntryState.OPENED);
                if (entry.hasStages()) { this.stages.put(entry.id(), entry.stages().size()); }
                continue;
            }
            if (!EntryAccess.isAvailable(entry, this)) {
                this.states.put(entry.id(), EntryState.LOCKED);
                continue;
            }
            this.states.put(entry.id(), EntryState.AVAILABLE);
            this.advanceStages(entry);
        }
    }

    private void advanceStages(BookEntry entry) {
        if (!entry.hasStages()) { return; }
        int index = stageOf(entry.id());
        while (index < entry.stages().size() && EntryAccess.stageReady(entry.stages().get(index), this)) {
            index++;
        }
        if (index >= entry.stages().size()) {
            this.states.put(entry.id(), EntryState.OPENED);
            this.stages.put(entry.id(), entry.stages().size());
        } else if (index > 0) {
            this.stages.put(entry.id(), index);
        }
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("version", this.version);
        JsonObject statesJson = new JsonObject();
        for (Map.Entry<ResourceLocation, EntryState> entry : this.states.entrySet()) { statesJson.addProperty(entry.getKey().toString(), entry.getValue().getSerializedName()); }
        json.add("states", statesJson);
        json.add("theories", writeSet(this.theories));
        json.add("crafted", writeSet(this.crafted));
        json.add("studied", writeSet(this.studied));
        JsonObject killsJson = new JsonObject();
        for (Map.Entry<ResourceLocation, Integer> entry : this.kills.entrySet()) { killsJson.addProperty(entry.getKey().toString(), entry.getValue()); }
        json.add("kills", killsJson);
        JsonObject stagesJson = new JsonObject();
        for (Map.Entry<ResourceLocation, Integer> entry : this.stages.entrySet()) { stagesJson.addProperty(entry.getKey().toString(), entry.getValue()); }
        json.add("stages", stagesJson);
        json.add("forgotten", writeSet(this.forgotten));
        return json;
    }

    private static JsonArray writeSet(Set<ResourceLocation> values) {
        JsonArray array = new JsonArray();
        for (ResourceLocation value : values) { array.add(value.toString()); }
        return array;
    }

    public static BookProgress fromJson(JsonObject json) {
        Map<ResourceLocation, EntryState> states = new HashMap<>();
        if (json.has("states") && json.get("states").isJsonObject()) {
            for (Map.Entry<String, JsonElement> entry : json.getAsJsonObject("states").entrySet()) {
                ResourceLocation id = ResourceLocation.tryParse(entry.getKey());
                if (id != null) { states.put(id, EntryState.byName(entry.getValue().getAsString())); }
            }
        }
        List<ResourceLocation> theories = readSet(json, "theories");
        List<ResourceLocation> crafted = readSet(json, "crafted");
        List<ResourceLocation> studied = readSet(json, "studied");
        List<ResourceLocation> forgotten = readSet(json, "forgotten");
        int version = json.has("version") ? json.get("version").getAsInt() : 0;
        Map<ResourceLocation, Integer> kills = new HashMap<>();
        if (json.has("kills") && json.get("kills").isJsonObject()) {
            for (Map.Entry<String, JsonElement> entry : json.getAsJsonObject("kills").entrySet()) {
                ResourceLocation id = ResourceLocation.tryParse(entry.getKey());
                if (id != null) { kills.put(id, entry.getValue().getAsInt()); }
            }
        }
        Map<ResourceLocation, Integer> stages = new HashMap<>();
        if (json.has("stages") && json.get("stages").isJsonObject()) {
            for (Map.Entry<String, JsonElement> entry : json.getAsJsonObject("stages").entrySet()) {
                ResourceLocation id = ResourceLocation.tryParse(entry.getKey());
                if (id != null) { stages.put(id, Math.max(0, entry.getValue().getAsInt())); }
            }
        }
        return new BookProgress(version, states, theories, crafted, studied, kills, stages, forgotten);
    }

    private static List<ResourceLocation> readSet(JsonObject json, String key) {
        List<ResourceLocation> result = new ArrayList<>();
        if (json.has(key) && json.get(key).isJsonArray()) {
            for (JsonElement element : json.getAsJsonArray(key)) {
                ResourceLocation id = ResourceLocation.tryParse(element.getAsString());
                if (id != null) { result.add(id); }
            }
        }
        return result;
    }
}
