package the_end_game.handler.vanilla_3x3;

import com.google.gson.JsonParser;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public final class ClientCraftBlocked {
    public static void handle(CraftBlockedPayload payload) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || !(minecraft.player.containerMenu instanceof CraftBlockState state)) { return; }
        if (minecraft.player.containerMenu.containerId != payload.containerId()) { return; }
        Component missing = payload.blocked() ? parse(payload.message(), minecraft) : null;
        Component info = payload.blocked() ? parse(payload.info(), minecraft) : null;
        state.setBlocked(payload.blocked(), missing, info);
    }

    private static Component parse(String json, Minecraft minecraft) {
        if (json == null || json.isEmpty() || minecraft.level == null) { return null; }
        try { return Component.Serializer.fromJson(JsonParser.parseString(json), minecraft.level.registryAccess()); } catch (Exception exception) { return Component.literal(json); }
    }
}
