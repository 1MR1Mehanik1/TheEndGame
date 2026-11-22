package the_end_game.init.item.etericon.network;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import the_end_game.init.InitItem;
import the_end_game.init.item.etericon.EtericonAttachments;
import the_end_game.init.item.etericon.api.EtericonApi;
import the_end_game.init.item.etericon.book.BookCatalog;
import the_end_game.init.item.etericon.book.BookEntry;
import the_end_game.init.item.etericon.book.BookJson;
import the_end_game.init.item.etericon.book.BookManager;
import the_end_game.init.item.etericon.book.BookProgress;
import the_end_game.init.item.etericon.book.EntryAccess;
import the_end_game.init.item.etericon.book.EntryState;
import the_end_game.init.item.etericon.book.UnlockCondition;
import the_end_game.init.item.etericon.client.ClientPayloadHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class EtericonNetwork {
    private static final Map<UUID, String> SENT_VERSIONS = new ConcurrentHashMap<>();
    private static final Map<UUID, Long> LAST_ACTION = new ConcurrentHashMap<>();
    private static final long ACTION_COOLDOWN_MS = 150L;

    public static void onRegisterPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("2");
        registrar.playToClient(OpenBookPayload.TYPE, OpenBookPayload.STREAM_CODEC, EtericonNetwork::handleClientOpen);
        registrar.playToClient(CatalogPayload.TYPE, CatalogPayload.STREAM_CODEC, EtericonNetwork::handleClientCatalog);
        registrar.playToClient(ProgressPayload.TYPE, ProgressPayload.STREAM_CODEC, EtericonNetwork::handleClientProgress);
        registrar.playToClient(ProgressNoticePayload.TYPE, ProgressNoticePayload.STREAM_CODEC, EtericonNetwork::handleClientNotice);
        registrar.playToClient(OpenEntryPayload.TYPE, OpenEntryPayload.STREAM_CODEC, EtericonNetwork::handleClientOpenEntry);
        registrar.playToServer(ClickEntryPayload.TYPE, ClickEntryPayload.STREAM_CODEC, (payload, context) -> context.enqueueWork(() -> handleClick(payload, context)));
        registrar.playToServer(ConfirmEntryPayload.TYPE, ConfirmEntryPayload.STREAM_CODEC, (payload, context) -> context.enqueueWork(() -> handleConfirm(payload, context)));
    }

    private static void handleClientOpen(OpenBookPayload payload, IPayloadContext context) {
        if (FMLEnvironment.dist == Dist.CLIENT) { context.enqueueWork(() -> ClientPayloadHandler.handleOpen(payload)); }
    }

    private static void handleClientCatalog(CatalogPayload payload, IPayloadContext context) {
        if (FMLEnvironment.dist == Dist.CLIENT) { context.enqueueWork(() -> ClientPayloadHandler.handleCatalog(payload)); }
    }

    private static void handleClientProgress(ProgressPayload payload, IPayloadContext context) {
        if (FMLEnvironment.dist == Dist.CLIENT) { context.enqueueWork(() -> ClientPayloadHandler.handleProgress(payload)); }
    }

    private static void handleClientNotice(ProgressNoticePayload payload, IPayloadContext context) {
        if (FMLEnvironment.dist == Dist.CLIENT) { context.enqueueWork(() -> ClientPayloadHandler.handleNotice(payload)); }
    }

    private static void handleClientOpenEntry(OpenEntryPayload payload, IPayloadContext context) {
        if (FMLEnvironment.dist == Dist.CLIENT) { context.enqueueWork(() -> ClientPayloadHandler.handleOpenEntry(payload)); }
    }

    public static void open(ServerPlayer player) {
        BookCatalog catalog = BookManager.getOrJavaOnly();
        BookProgress progress = EtericonApi.getProgress(player);
        progress.recalculate(catalog);
        player.setData(EtericonAttachments.PROGRESS, progress);
        String version = BookManager.version();
        String cached = SENT_VERSIONS.get(player.getUUID());
        String catalogJson = version.equals(cached) ? "" : BookJson.catalogToJson(catalog);
        if (!catalogJson.isEmpty()) { SENT_VERSIONS.put(player.getUUID(), version); }
        PacketDistributor.sendToPlayer(player, new OpenBookPayload(version, catalogJson, progress.toJson().toString()));
    }

    public static void broadcastCatalog() {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) { return; }
        String version = BookManager.version();
        String catalog = BookJson.catalogToJson(BookManager.getOrJavaOnly());
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (!holdsBook(player)) { continue; }
            PacketDistributor.sendToPlayer(player, new CatalogPayload(version, catalog));
            SENT_VERSIONS.put(player.getUUID(), version);
        }
    }

    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() != null) {
            SENT_VERSIONS.remove(event.getEntity().getUUID());
            LAST_ACTION.remove(event.getEntity().getUUID());
        }
    }

    public static void onServerStopped(ServerStoppedEvent event) {
        SENT_VERSIONS.clear();
        LAST_ACTION.clear();
    }

    public static void notifyAvailable(ServerPlayer player, List<ResourceLocation> entries) {
        if (entries.isEmpty()) { return; }
        PacketDistributor.sendToPlayer(player, new ProgressNoticePayload(List.copyOf(entries)));
    }

    private static boolean allowAction(ServerPlayer player) {
        long now = System.currentTimeMillis();
        long last = LAST_ACTION.getOrDefault(player.getUUID(), 0L);
        if (now - last < ACTION_COOLDOWN_MS) { return false; }
        LAST_ACTION.put(player.getUUID(), now);
        return true;
    }

    public static void sync(ServerPlayer player) {
        BookProgress progress = EtericonApi.getProgress(player);
        PacketDistributor.sendToPlayer(player, new ProgressPayload(progress.toJson().toString()));
    }

    private static void handleClick(ClickEntryPayload payload, IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer player) || !holdsBook(player) || !allowAction(player)) { return; }
        BookCatalog catalog = BookManager.getOrJavaOnly();
        BookEntry entry = catalog.entry(payload.entry());
        if (entry == null) { return; }
        BookProgress progress = EtericonApi.getProgress(player);
        progress.recalculate(catalog);
        if (entry.hasStages()) { sync(player); }
        EntryState state = progress.stateOf(entry);
        if (state == EntryState.LOCKED) {
            if (progress.spendResearch(entry)) {
                progress.recalculate(catalog);
                player.setData(EtericonAttachments.PROGRESS, progress);
                sendOpenEntry(player, entry, EntryState.OPENED);
            } else if (progress.requirementsMet(entry) && hasResearch(entry)) {
                player.playNotifySound(SoundEvents.VILLAGER_NO, SoundSource.PLAYERS, 0.8F, 1.0F);
            }
            return;
        }
        if (state == EntryState.AVAILABLE) {
            if (entry.hasPreview() || entry.hasStages()) { PacketDistributor.sendToPlayer(player, new OpenEntryPayload(entry.id(), EntryState.AVAILABLE.getSerializedName())); }
            else {
                progress.open(entry);
                progress.recalculate(catalog);
                player.setData(EtericonAttachments.PROGRESS, progress);
                sendOpenEntry(player, entry, EntryState.OPENED);
            }
            return;
        }
        PacketDistributor.sendToPlayer(player, new OpenEntryPayload(entry.id(), EntryState.OPENED.getSerializedName()));
    }

    private static void handleConfirm(ConfirmEntryPayload payload, IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer player) || !holdsBook(player) || !allowAction(player)) { return; }
        BookCatalog catalog = BookManager.getOrJavaOnly();
        BookEntry entry = catalog.entry(payload.entry());
        if (entry == null) { return; }
        BookProgress progress = EtericonApi.getProgress(player);
        progress.recalculate(catalog);
        if (progress.stateOf(entry) != EntryState.AVAILABLE) { return; }
        if (entry.hasStages()) {
            if (!EntryAccess.canPayStage(entry, progress)) {
                player.playNotifySound(SoundEvents.VILLAGER_NO, SoundSource.PLAYERS, 0.8F, 1.0F);
                return;
            }
            int cost = EntryAccess.researchCost(EntryAccess.currentStage(entry, progress));
            if (!progress.spendResearchPoints(cost)) { return; }
            progress.advanceStage(entry.id());
            progress.recalculate(catalog);
            player.setData(EtericonAttachments.PROGRESS, progress);
            if (progress.stateOf(entry) == EntryState.OPENED) { sendOpenEntry(player, entry, EntryState.OPENED); }
            else { sync(player); }
            return;
        }
        progress.open(entry);
        progress.recalculate(catalog);
        player.setData(EtericonAttachments.PROGRESS, progress);
        sendOpenEntry(player, entry, EntryState.OPENED);
    }

    private static void sendOpenEntry(ServerPlayer player, BookEntry entry, EntryState state) {
        PacketDistributor.sendToPlayer(player, new OpenEntryPayload(entry.id(), state.getSerializedName()));
        sync(player);
    }

    private static boolean holdsBook(ServerPlayer player) {
        Item item = InitItem.ETERICON.get();
        return player.getMainHandItem().is(item) || player.getOffhandItem().is(item);
    }

    private static boolean hasResearch(BookEntry entry) {
        for (UnlockCondition condition : entry.unlocks()) {
            if (condition instanceof UnlockCondition.Research) { return true; }
        }
        return false;
    }
}
