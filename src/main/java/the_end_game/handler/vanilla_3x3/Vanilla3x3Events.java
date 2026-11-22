package the_end_game.handler.vanilla_3x3;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;

public final class Vanilla3x3Events {
    public static void register(IEventBus bus) {
        bus.addListener(Vanilla3x3Network::register);
        NeoForge.EVENT_BUS.addListener(Vanilla3x3Events::addReloadListener);
    }

    public static void addReloadListener(AddReloadListenerEvent event) {
        event.addListener(new BelowRecipeListener(event.getRegistryAccess()));
    }
}
