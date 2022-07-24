package the_end_game.supporting.events;

import java.util.*;

import net.minecraft.entity.*;
import net.minecraft.entity.item.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import net.minecraftforge.event.*;
import net.minecraftforge.event.entity.*;
import net.minecraftforge.event.entity.player.*;
import net.minecraftforge.fml.common.eventhandler.*;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.*;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import the_end_game.*;
import the_end_game.supporting.network.*;
import the_end_game.trinket_inventory.*;
import the_end_game.trinket_inventory.cap.*;

public class EntityHandEvent {
	private HashMap<UUID,ItemStack[]> trinketSync = new HashMap<UUID,ItemStack[]>();

	@SubscribeEvent
	public void cloneCapabilitiesEvent(PlayerEvent.Clone event) {
		try {
			CAPTrinketStorage tso = (CAPTrinketStorage) TrinketAPI.getTrinketHandler(event.getOriginal());
			NBTTagCompound nbt = tso.serializeNBT();
			CAPTrinketStorage tsn = (CAPTrinketStorage) TrinketAPI.getTrinketHandler(event.getEntityPlayer());
			tsn.deserializeNBT(nbt);
		} catch (Exception e) {}
	}

	@SubscribeEvent
	public void attachCapabilitiesPlayer(AttachCapabilitiesEvent<Entity> event) {
		if (event.getObject() instanceof EntityPlayer) event.addCapability(new ResourceLocation(Main.MODID, "trinket_container"), new CAPTrinketProvider(new CAPTrinketStorage()));
	}

	@SubscribeEvent
	public void playerJoin(EntityJoinWorldEvent event) {
		Entity entity = event.getEntity();
		if (entity instanceof EntityPlayerMP) {
			EntityPlayerMP player = (EntityPlayerMP) entity;
			syncSlots(player, Collections.singletonList(player));
		}
	}

	@SubscribeEvent
	public void onStartTracking(PlayerEvent.StartTracking event) {
		Entity target = event.getTarget();
		if (target instanceof EntityPlayerMP) syncSlots((EntityPlayer) target, Collections.singletonList(event.getEntityPlayer()));
	}

	@SubscribeEvent
	public void onPlayerLoggedOut(PlayerLoggedOutEvent event) {
		trinketSync.remove(event.player.getUniqueID());
	}

	@SubscribeEvent
	public void playerTick(TickEvent.PlayerTickEvent event) {
		// player events
		if (event.phase == TickEvent.Phase.END) {
			EntityPlayer player = event.player;
			ITrinketItemHandler trinkets = TrinketAPI.getTrinketHandler(player);
			for (int i = 0; i < trinkets.getSlots(); i++) {
				ItemStack stack = trinkets.getStackInSlot(i);
				ITrinket trinket = stack.getCapability(CAPTrinket.INV_CAP_TRINKET_ITEM, null);
				if (trinket != null) trinket.onWornTick(stack, player);
			}
			if (!player.world.isRemote) syncTrinkets(player, trinkets);
		}
	}

	private void syncTrinkets(EntityPlayer player, ITrinketItemHandler trinkets) {
		ItemStack[] items = trinketSync.get(player.getUniqueID());
		if (items == null) {
			items = new ItemStack[trinkets.getSlots()];
			Arrays.fill(items, ItemStack.EMPTY);
			trinketSync.put(player.getUniqueID(), items);
		}
		if (items.length != trinkets.getSlots()) {
			ItemStack[] old = items;
			items = new ItemStack[trinkets.getSlots()];
			System.arraycopy(old, 0, items, 0, Math.min(old.length, items.length));
			trinketSync.put(player.getUniqueID(), items);
		}
		Set<EntityPlayer> receivers = null;
		for (int i = 0; i < trinkets.getSlots(); i++) {
			ItemStack stack = trinkets.getStackInSlot(i);
			ITrinket trinket = stack.getCapability(CAPTrinket.INV_CAP_TRINKET_ITEM, null);
			if (trinkets.isChanged(i) || trinket != null && trinket.willAutoSync(stack, player) && !ItemStack.areItemStacksEqual(stack, items[i])) {
				if (receivers == null) {
					receivers = new HashSet<>(((WorldServer) player.world).getEntityTracker().getTrackingPlayers(player));
					receivers.add(player);
				}
				syncSlot(player, i, stack, receivers);
				trinkets.setChanged(i,false);
				items[i] = stack == null ? ItemStack.EMPTY : stack.copy();
			}
		}
	}

	private void syncSlots(EntityPlayer player, Collection<? extends EntityPlayer> receivers) {
		ITrinketItemHandler trinkets = TrinketAPI.getTrinketHandler(player);
		for (int i = 0; i < trinkets.getSlots(); i++) syncSlot(player, i, trinkets.getStackInSlot(i), receivers);
	}

	private void syncSlot(EntityPlayer player, int slot, ItemStack stack, Collection<? extends EntityPlayer> receivers) {
		PacketSync pkt = new PacketSync(player, slot, stack);
		for (EntityPlayer receiver : receivers) PacketHandler.INSTANCE.sendTo(pkt, (EntityPlayerMP) receiver);
	}

	@SubscribeEvent
	public void playerDeath(PlayerDropsEvent event) {
		if (event.getEntity() instanceof EntityPlayer && !event.getEntity().world.isRemote && !event.getEntity().world.getGameRules().getBoolean("keepInventory")) {
			dropItemsAt(event.getEntityPlayer(),event.getDrops(),event.getEntityPlayer());
		}
	}

	public void dropItemsAt(EntityPlayer player, List<EntityItem> drops, Entity e) {
		ITrinketItemHandler trinkets = TrinketAPI.getTrinketHandler(player);
		for (int i = 0; i < trinkets.getSlots(); ++i) {
			if (trinkets.getStackInSlot(i) != null && !trinkets.getStackInSlot(i).isEmpty()) {
				EntityItem ei = new EntityItem(e.world, e.posX, e.posY + e.getEyeHeight(), e.posZ, trinkets.getStackInSlot(i).copy());
				ei.setPickupDelay(40);
				float f1 = e.world.rand.nextFloat() * 0.5F;
				float f2 = e.world.rand.nextFloat() * (float) Math.PI * 2.0F;
				ei.motionX = (double) (-MathHelper.sin(f2) * f1);
				ei.motionZ = (double) (MathHelper.cos(f2) * f1);
				ei.motionY = 0.20000000298023224D;
				drops.add(ei);
				trinkets.setStackInSlot(i, ItemStack.EMPTY);
			}
		}
	}
}
