package the_end_game.init.item.etericon.network;

import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import the_end_game.Main;

public record ClickEntryPayload(ResourceLocation entry) implements CustomPacketPayload {
    public static final Type<ClickEntryPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Main.MOD_ID, "click_entry"));

    public static final StreamCodec<net.minecraft.network.FriendlyByteBuf, ClickEntryPayload> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC,
            ClickEntryPayload::entry,
            ClickEntryPayload::new
    );

    @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
