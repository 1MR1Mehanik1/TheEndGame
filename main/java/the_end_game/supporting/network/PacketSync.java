package the_end_game.supporting.network;

import io.netty.buffer.*;
import net.minecraft.client.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import net.minecraft.world.*;
import net.minecraftforge.fml.common.network.*;
import net.minecraftforge.fml.common.network.simpleimpl.*;
import the_end_game.*;
import the_end_game.trinket_inventory.*;
import the_end_game.trinket_inventory.cap.*;

public class PacketSync implements IMessage {
	int playerId;
	byte slot=0;
	ItemStack bauble;

	public PacketSync() {}

	public PacketSync(EntityPlayer p, int slot, ItemStack bauble) {
		this.slot = (byte) slot;
		this.bauble = bauble;
		this.playerId = p.getEntityId();
	}

	@Override
	public void toBytes(ByteBuf buffer) {
		buffer.writeInt(playerId);
		buffer.writeByte(slot);
		ByteBufUtils.writeItemStack(buffer, bauble);
	}

	@Override
	public void fromBytes(ByteBuf buffer) {
		playerId = buffer.readInt();
		slot = buffer.readByte();
		bauble = ByteBufUtils.readItemStack(buffer);
	}

	public static class Handler implements IMessageHandler<PacketSync, IMessage> {
		@Override
		public IMessage onMessage(PacketSync message, MessageContext ctx) {
			Minecraft.getMinecraft().addScheduledTask(new Runnable(){ public void run() {
				World world = Main.proxy.getClientWorld();
				if (world == null) return;
				Entity p = world.getEntityByID(message.playerId);
				if (p !=null && p instanceof EntityPlayer) {
					ITrinketItemHandler baubles = TrinketAPI.getTrinketHandler((EntityPlayer) p);
					baubles.setStackInSlot(message.slot, message.bauble);
				}
			}});
			return null;
		}
	}
}
