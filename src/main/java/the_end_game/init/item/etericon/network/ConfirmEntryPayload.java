package the_end_game.init.item.etericon.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import the_end_game.Main;

public record ConfirmEntryPayload(ResourceLocation entry) implements CustomPacketPayload {
    public static final Type<ConfirmEntryPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Main.MOD_ID, "confirm_entry"));

    public static final StreamCodec<FriendlyByteBuf, ConfirmEntryPayload> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC,
            ConfirmEntryPayload::entry,
            ConfirmEntryPayload::new
    );

    @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
