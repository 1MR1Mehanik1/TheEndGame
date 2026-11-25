package the_end_game.player;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import the_end_game.Main;

public final class PlayerVariablesAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, Main.MOD_ID);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<PlayerVariables>> PLAYER_VARIABLES = ATTACHMENTS.register("player_variables",
            () -> AttachmentType.builder(PlayerVariables::new).serialize(PlayerVariables.CODEC).copyOnDeath().build());

    public static void register(IEventBus bus) { ATTACHMENTS.register(bus); }
}
