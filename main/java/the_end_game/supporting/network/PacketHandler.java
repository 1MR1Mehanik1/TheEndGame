package the_end_game.supporting.network;

import net.minecraft.world.*;
import net.minecraftforge.fml.common.network.*;
import net.minecraftforge.fml.common.network.simpleimpl.*;
import net.minecraftforge.fml.relauncher.*;
import the_end_game.*;

public class PacketHandler {
	private static short id;
	public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(Main.MODID.toLowerCase());

	public static void init() {
		INSTANCE.registerMessage(PacketOpenTrinketInventory.class, PacketOpenTrinketInventory.class, 0, Side.SERVER);
		INSTANCE.registerMessage(PacketOpenNormalInventory.class, PacketOpenNormalInventory.class, 1, Side.SERVER);
		INSTANCE.registerMessage(PacketSync.Handler.class, PacketSync.class, 2, Side.CLIENT);
	}
	
	public static void sendToAllAround(SimplePacket packet, World world, double x, double y, double z, double distance) {
		INSTANCE.sendToAllAround(packet, new NetworkRegistry.TargetPoint(world.provider.getDimension(), x, y, z, distance));
    }
	public static void register(Class<? extends SimplePacket> packet, Side side) {
        try {
        	INSTANCE.registerMessage(packet.newInstance(), packet, id++, side);
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
