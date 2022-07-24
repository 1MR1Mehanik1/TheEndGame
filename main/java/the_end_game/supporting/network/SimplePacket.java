package the_end_game.supporting.network;

import io.netty.buffer.*;
import net.minecraft.client.*;
import net.minecraft.entity.player.*;
import net.minecraftforge.fml.common.network.simpleimpl.*;
import net.minecraftforge.fml.relauncher.*;

public class SimplePacket implements IMessage, IMessageHandler<SimplePacket, SimplePacket> {
    private ByteBuf buf;

    @Override
    public SimplePacket onMessage(SimplePacket sp, MessageContext ctx) {
        if (ctx.side.isServer()) sp.server(ctx.getServerHandler().player); else sp.client(clientPlayer());
        return null;
    }

    protected ByteBuf buf() { return buf != null ? buf : (buf = Unpooled.buffer()); }

    public void client(EntityPlayer player) {}
    public void server(EntityPlayerMP player) {}

    @Override public final void fromBytes(ByteBuf buf) { this.buf = buf; }

    @Override public final void toBytes(ByteBuf buf) { if (buf != null) buf.writeBytes(this.buf); }

    @SideOnly(Side.CLIENT) private EntityPlayer clientPlayer() { return Minecraft.getMinecraft().player; }
}
