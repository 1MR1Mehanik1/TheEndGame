package the_end_game.init;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import the_end_game.init.item.etericon.EtericonAttachments;
import the_end_game.init.item.etericon.book.EtericonReloadListener;
import the_end_game.init.item.etericon.command.EtericonCommands;
import the_end_game.init.item.etericon.demo.EtericonDemo;
import the_end_game.init.item.etericon.event.EtericonProgressEvents;
import the_end_game.init.item.etericon.network.EtericonNetwork;

public final class InitEtericon {
    public static void register(IEventBus bus) {
        EtericonAttachments.register(bus);
        bus.addListener(EtericonNetwork::onRegisterPayloads);
        NeoForge.EVENT_BUS.addListener(InitEtericon::onAddReloadListener);
        NeoForge.EVENT_BUS.addListener(EtericonCommands::onRegisterCommands);
        NeoForge.EVENT_BUS.addListener(EtericonProgressEvents::onCrafted);
        NeoForge.EVENT_BUS.addListener(EtericonProgressEvents::onDeath);
        NeoForge.EVENT_BUS.addListener(EtericonNetwork::onLogout);
        NeoForge.EVENT_BUS.addListener(EtericonNetwork::onServerStopped);
        EtericonDemo.register();
    }

    private static void onAddReloadListener(AddReloadListenerEvent event) {
        event.addListener(new EtericonReloadListener(event.getRegistryAccess()));
    }
}
