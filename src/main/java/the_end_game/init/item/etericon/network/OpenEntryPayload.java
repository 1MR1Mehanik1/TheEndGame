package the_end_game.init.item.etericon.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import the_end_game.Main;

public record OpenEntryPayload(ResourceLocation entry, String state) implements CustomPacketPayload {
    public static final Type<OpenEntryPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Main.MOD_ID, "open_entry"));

    public static final StreamCodec<FriendlyByteBuf, OpenEntryPayload> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC,
            OpenEntryPayload::entry,
            ByteBufCodecs.STRING_UTF8,
            OpenEntryPayload::state,
            OpenEntryPayload::new
    );

    @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
