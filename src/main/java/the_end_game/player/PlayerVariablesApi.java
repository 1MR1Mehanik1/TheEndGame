package the_end_game.player;

import net.minecraft.server.level.ServerPlayer;

import java.util.function.Consumer;

public final class PlayerVariablesApi {
    public static PlayerVariables get(ServerPlayer player) { return player.getData(PlayerVariablesAttachments.PLAYER_VARIABLES); }

    public static void mutate(ServerPlayer player, Consumer<PlayerVariables> action) {
        PlayerVariables variables = get(player);
        action.accept(variables);
        player.setData(PlayerVariablesAttachments.PLAYER_VARIABLES, variables);
    }

    public static void addResearch(ServerPlayer player, int amount) {
        mutate(player, variables -> variables.setResearch(variables.research() + amount));
    }
}
