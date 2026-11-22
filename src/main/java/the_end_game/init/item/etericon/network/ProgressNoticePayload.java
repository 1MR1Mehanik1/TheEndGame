package the_end_game.init.item.etericon.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import the_end_game.Main;

import java.util.ArrayList;
import java.util.List;

public record ProgressNoticePayload(List<ResourceLocation> entries) implements CustomPacketPayload {
    public static final Type<ProgressNoticePayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Main.MOD_ID, "progress_notice"));

    public static final StreamCodec<FriendlyByteBuf, ProgressNoticePayload> STREAM_CODEC = StreamCodec.of(
            (buffer, payload) -> {
                buffer.writeVarInt(payload.entries.size());
                for (ResourceLocation entry : payload.entries) { buffer.writeResourceLocation(entry); }
            },
            buffer -> {
                int size = buffer.readVarInt();
                List<ResourceLocation> entries = new ArrayList<>();
                for (int index = 0; index < size; index++) { entries.add(buffer.readResourceLocation()); }
                return new ProgressNoticePayload(entries);
            }
    );

    @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
