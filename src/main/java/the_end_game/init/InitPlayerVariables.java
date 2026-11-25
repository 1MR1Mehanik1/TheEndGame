package the_end_game.init;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import the_end_game.player.PlayerVariablesAttachments;
import the_end_game.player.PlayerVariablesCommands;

public final class InitPlayerVariables {
    public static void register(IEventBus bus) {
        PlayerVariablesAttachments.register(bus);
        NeoForge.EVENT_BUS.addListener(PlayerVariablesCommands::onRegisterCommands);
    }
}
