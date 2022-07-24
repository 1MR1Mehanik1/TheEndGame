package the_end_game.trinket_inventory.cap;

import net.minecraft.nbt.*;
import net.minecraft.util.*;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.capabilities.Capability.*;
import the_end_game.trinket_inventory.*;

public class CAPTrinket {
	@CapabilityInject(ITrinketItemHandler.class)
	public static final Capability<ITrinketItemHandler> INV_CAP_TRINKET = null;

	@CapabilityInject(ITrinket.class)
	public static final Capability<ITrinket> INV_CAP_TRINKET_ITEM = null;

	public static class CapabilityTrinket<T extends ITrinketItemHandler> implements IStorage<ITrinketItemHandler> {
		@Override public NBTBase writeNBT(Capability<ITrinketItemHandler> capability, ITrinketItemHandler instance, EnumFacing side) { return null; }
		@Override public void readNBT(Capability<ITrinketItemHandler> capability, ITrinketItemHandler instance, EnumFacing side, NBTBase nbt) {}
	}
	
	public static class CapabilityItemTrinketStorage implements IStorage<ITrinket> {
		@Override public NBTBase writeNBT(Capability<ITrinket> capability, ITrinket instance, EnumFacing side) { return null; }
		@Override public void readNBT(Capability<ITrinket> capability, ITrinket instance, EnumFacing side, NBTBase nbt) {}
	}
}
