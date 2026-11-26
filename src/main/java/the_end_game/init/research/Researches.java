package the_end_game.init.research;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Blocks;
import the_end_game.Main;

public final class Researches {
    public static final ResearchRegistry.Research RESZOMBIE = ResearchRegistry.registerEntity(
            ResourceLocation.fromNamespaceAndPath(Main.MOD_ID, "zombie"), EntityType.ZOMBIE, 1.0F, DifficultyResearches.EASY);
    public static final ResearchRegistry.Research RESTEST_BLOCK = ResearchRegistry.registerBlock(
            ResourceLocation.fromNamespaceAndPath(Main.MOD_ID, "test_block"), Blocks.STONE, 1.0F, DifficultyResearches.EASY);

    private Researches() {
    }

    public static void register() {
    }
}
