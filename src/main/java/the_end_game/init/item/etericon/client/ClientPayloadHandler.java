package the_end_game.init.item.etericon.client;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.client.gui.screens.Screen;
import org.slf4j.Logger;
import the_end_game.init.item.etericon.book.BookCatalog;
import the_end_game.init.item.etericon.book.BookJson;
import the_end_game.init.item.etericon.book.BookProgress;
import the_end_game.init.item.etericon.book.EntryState;
import the_end_game.init.item.etericon.network.OpenBookPayload;
import the_end_game.init.item.etericon.network.OpenEntryPayload;
import the_end_game.init.item.etericon.network.CatalogPayload;
import the_end_game.init.item.etericon.network.ProgressNoticePayload;
import the_end_game.init.item.etericon.network.ProgressPayload;

public final class ClientPayloadHandler {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static void handleOpen(OpenBookPayload payload) {
        try {
            HolderLookup.Provider provider = Minecraft.getInstance().level == null ? RegistryAccess.EMPTY : Minecraft.getInstance().level.registryAccess();
            BookCatalog catalog = payload.catalog().isBlank() ? ClientEtericonData.catalog() : BookJson.catalogFromJson(payload.catalog(), provider);
            if (payload.catalog().isBlank() && catalog.entries().isEmpty() && catalog.chapters().isEmpty()) {
                LOGGER.warn("Etericon catalog cache miss for version {}", payload.version());
            }
            JsonObject json = JsonParser.parseString(payload.progress()).getAsJsonObject();
            ClientEtericonData.set(payload.version(), catalog, BookProgress.fromJson(json));
            Minecraft.getInstance().setScreen(new EtericonScreen());
        } catch (Exception exception) { LOGGER.error("Unable to open Etericon", exception); }
    }

    public static void handleCatalog(CatalogPayload payload) {
        try {
            HolderLookup.Provider provider = Minecraft.getInstance().level == null ? RegistryAccess.EMPTY : Minecraft.getInstance().level.registryAccess();
            BookCatalog catalog = BookJson.catalogFromJson(payload.catalog(), provider);
            ClientEtericonData.setCatalog(payload.version(), catalog);
        } catch (Exception exception) { LOGGER.error("Unable to update Etericon catalog", exception); }
    }

    public static void handleProgress(ProgressPayload payload) {
        try {
            BookProgress progress = BookProgress.fromJson(JsonParser.parseString(payload.progress()).getAsJsonObject());
            ClientEtericonData.setProgress(progress);
            Screen screen = Minecraft.getInstance().screen;
            if (screen instanceof EtericonScreen etericonScreen) { etericonScreen.onProgress(progress); }
        } catch (Exception exception) { LOGGER.error("Unable to update Etericon progress", exception); }
    }

    public static void handleNotice(ProgressNoticePayload payload) {
        for (var entry : payload.entries()) { EtericonToast.show(entry); }
    }

    public static void handleOpenEntry(OpenEntryPayload payload) {
        Screen screen = Minecraft.getInstance().screen;
        if (screen instanceof EtericonScreen etericonScreen) { etericonScreen.openEntry(payload.entry(), EntryState.byName(payload.state())); }
    }
}
