package the_end_game.init.item.etericon.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import the_end_game.Main;

public record OpenBookPayload(String version, String catalog, String progress) implements CustomPacketPayload {
    public static final Type<OpenBookPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Main.MOD_ID, "open_book"));

    public static final StreamCodec<FriendlyByteBuf, OpenBookPayload> STREAM_CODEC = StreamCodec.of(
            (buffer, payload) -> {
                buffer.writeUtf(payload.version, 128);
                buffer.writeUtf(payload.catalog, 1000000);
                buffer.writeUtf(payload.progress, 1000000);
            },
            buffer -> new OpenBookPayload(buffer.readUtf(128), buffer.readUtf(1000000), buffer.readUtf(1000000))
    );

    @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
