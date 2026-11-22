package the_end_game.init.item.etericon.book;

import net.minecraft.core.HolderLookup;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import the_end_game.init.item.etericon.network.EtericonNetwork;

public final class EtericonReloadListener extends SimplePreparableReloadListener<BookCatalog> {
    private final HolderLookup.Provider provider;

    public EtericonReloadListener(HolderLookup.Provider provider) { this.provider = provider; }

    @Override protected BookCatalog prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
        return BookJson.load(resourceManager, this.provider);
    }

    @Override protected void apply(BookCatalog catalog, ResourceManager resourceManager, ProfilerFiller profiler) {
        BookManager.set(catalog);
        EtericonNetwork.broadcastCatalog();
    }
}
