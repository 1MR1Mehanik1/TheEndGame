package the_end_game.init.item.etericon;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import the_end_game.Main;
import the_end_game.init.item.etericon.book.BookProgress;

public final class EtericonAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, Main.MOD_ID);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<BookProgress>> PROGRESS = ATTACHMENTS.register("progress",
            () -> AttachmentType.builder(BookProgress::new).serialize(BookProgress.CODEC).copyOnDeath().build());

    public static void register(IEventBus bus) { ATTACHMENTS.register(bus); }
}
