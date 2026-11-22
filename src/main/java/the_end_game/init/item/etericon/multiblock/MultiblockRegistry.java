package the_end_game.init.item.etericon.multiblock;

import net.minecraft.resources.ResourceLocation;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class MultiblockRegistry {
    private static final Map<ResourceLocation, IMultiblock> MULTIBLOCKS = new LinkedHashMap<>();
    public static void register(IMultiblock multiblock) {
        MULTIBLOCKS.put(multiblock.getName(), multiblock);
    }
    public static IMultiblock get(ResourceLocation id) {
        return MULTIBLOCKS.get(id);
    }
    public static Map<ResourceLocation, IMultiblock> all() {
        return Collections.unmodifiableMap(MULTIBLOCKS);
    }
}
