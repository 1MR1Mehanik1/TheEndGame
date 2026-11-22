package the_end_game.init.item.etericon.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import the_end_game.Main;

public record CatalogPayload(String version, String catalog) implements CustomPacketPayload {
    public static final Type<CatalogPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Main.MOD_ID, "catalog"));

    public static final StreamCodec<FriendlyByteBuf, CatalogPayload> STREAM_CODEC = StreamCodec.of(
            (buffer, payload) -> {
                buffer.writeUtf(payload.version, 128);
                buffer.writeUtf(payload.catalog, 1000000);
            },
            buffer -> new CatalogPayload(buffer.readUtf(128), buffer.readUtf(1000000))
    );

    @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
