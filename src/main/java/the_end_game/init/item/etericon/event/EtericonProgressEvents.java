package the_end_game.init.item.etericon.event;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import the_end_game.init.item.etericon.api.EtericonApi;

public final class EtericonProgressEvents {
    public static void onCrafted(PlayerEvent.ItemCraftedEvent event) {
        if (event.getEntity() instanceof ServerPlayer player && !event.getCrafting().isEmpty()) {
            EtericonApi.markCrafted(player, event.getCrafting());
        }
    }

    public static void onDeath(LivingDeathEvent event) {
        if (event.getEntity().getKillCredit() instanceof ServerPlayer player) {
            EtericonApi.addKill(player, event.getEntity().getType(), 1);
        } else if (event.getSource().getEntity() instanceof ServerPlayer player) {
            EtericonApi.addKill(player, event.getEntity().getType(), 1);
        }
    }
}
