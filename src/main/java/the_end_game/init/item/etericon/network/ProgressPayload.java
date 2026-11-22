package the_end_game.init.item.etericon.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import the_end_game.Main;

public record ProgressPayload(String progress) implements CustomPacketPayload {
    public static final Type<ProgressPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Main.MOD_ID, "progress"));

    public static final StreamCodec<FriendlyByteBuf, ProgressPayload> STREAM_CODEC = StreamCodec.of(
            (buffer, payload) -> buffer.writeUtf(payload.progress, 1000000),
            buffer -> new ProgressPayload(buffer.readUtf(1000000))
    );

    @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
