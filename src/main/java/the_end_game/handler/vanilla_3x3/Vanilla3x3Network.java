package the_end_game.handler.vanilla_3x3;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public final class Vanilla3x3Network {
    public static void register(RegisterPayloadHandlersEvent event) {
        event.registrar("2").playToClient(CraftBlockedPayload.TYPE, CraftBlockedPayload.STREAM_CODEC, Vanilla3x3Network::handle);
    }

    private static void handle(CraftBlockedPayload payload, IPayloadContext context) {
        if (FMLEnvironment.dist == Dist.CLIENT) { context.enqueueWork(() -> ClientCraftBlocked.handle(payload)); }
    }
}
