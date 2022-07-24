package the_end_game.supporting.network;

import io.netty.buffer.*;
import net.minecraft.util.*;
import net.minecraft.world.*;
import net.minecraftforge.fml.common.network.simpleimpl.*;
import the_end_game.*;
import the_end_game.trinket_inventory.gui.*;

public class PacketOpenTrinketInventory implements IMessage, IMessageHandler<PacketOpenTrinketInventory, IMessage> {

	public PacketOpenTrinketInventory() {}

	@Override
	public void toBytes(ByteBuf buffer) {}

	@Override
	public void fromBytes(ByteBuf buffer) {}

	@Override
	public IMessage onMessage(PacketOpenTrinketInventory message, MessageContext ctx) {
		IThreadListener mainThread = (WorldServer) ctx.getServerHandler().player.world;
		mainThread.addScheduledTask(new Runnable(){ public void run() {
			ctx.getServerHandler().player.openContainer.onContainerClosed(ctx.getServerHandler().player);
			ctx.getServerHandler().player.openGui(Main.instance, TrinketGui.ID, ctx.getServerHandler().player.world, 0, 0, 0);
		}});
		return null;
	}
}
