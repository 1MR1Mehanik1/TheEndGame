package the_end_game.init.research;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class ResearchRegistry {
    private static final Map<Block, Research> BLOCKS = new HashMap<>();
    private static final Map<EntityType<?>, Research> ENTITIES = new HashMap<>();

    private ResearchRegistry() {
    }

    public static Research registerBlock(ResourceLocation id, Block block, float chance, DifficultyResearches difficulty) {
        validate(id, chance, difficulty);
        Research research = new Research(id, chance, difficulty);
        if (BLOCKS.putIfAbsent(Objects.requireNonNull(block), research) != null) {
            throw new IllegalArgumentException("Block already registered: " + block);
        }
        return research;
    }

    public static Research registerEntity(ResourceLocation id, EntityType<?> type, float chance, DifficultyResearches difficulty) {
        validate(id, chance, difficulty);
        Research research = new Research(id, chance, difficulty);
        if (ENTITIES.putIfAbsent(Objects.requireNonNull(type), research) != null) {
            throw new IllegalArgumentException("Entity already registered: " + type);
        }
        return research;
    }

    public static Research block(Block block) {
        return BLOCKS.get(block);
    }

    public static Research entity(EntityType<?> type) {
        return ENTITIES.get(type);
    }

    private static void validate(ResourceLocation id, float chance, DifficultyResearches difficulty) {
        Objects.requireNonNull(id);
        Objects.requireNonNull(difficulty);
        if (!Float.isFinite(chance) || chance < 0.0F || chance > 1.0F) {
            throw new IllegalArgumentException("Chance must be in [0; 1]: " + chance);
        }
    }

    public static final class Research {
        public final ResourceLocation id;
        public final float chance;
        public final DifficultyResearches difficulty;

        private Research(ResourceLocation id, float chance, DifficultyResearches difficulty) {
            this.id = id;
            this.chance = chance;
            this.difficulty = difficulty;
        }

        public ResourceLocation id() {
            return this.id;
        }

        public float chance() {
            return this.chance;
        }

        public DifficultyResearches difficulty() {
            return this.difficulty;
        }
    }
}
