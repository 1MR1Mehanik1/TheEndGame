package the_end_game.handler.vanilla_3x3;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import the_end_game.Main;

public record CraftBlockedPayload(int containerId, boolean blocked, String message, String info) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<CraftBlockedPayload> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Main.MOD_ID, "craft_blocked"));

    public static final StreamCodec<RegistryFriendlyByteBuf, CraftBlockedPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, CraftBlockedPayload::containerId,
            ByteBufCodecs.BOOL, CraftBlockedPayload::blocked,
            ByteBufCodecs.STRING_UTF8, CraftBlockedPayload::message,
            ByteBufCodecs.STRING_UTF8, CraftBlockedPayload::info,
            CraftBlockedPayload::new);

    @Override public CustomPacketPayload.Type<? extends CustomPacketPayload> type() { return TYPE; }
}
