package the_end_game.trinket_inventory.cap;

import net.minecraft.nbt.*;
import net.minecraft.util.*;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.*;

public class CAPTrinketProvider implements INBTSerializable<NBTTagCompound>, ICapabilityProvider {
	private final CAPTrinketStorage container;

	public CAPTrinketProvider(CAPTrinketStorage container) { this.container = container; }

	@Override public boolean hasCapability (Capability<?> capability, EnumFacing facing) { return capability == CAPTrinket.INV_CAP_TRINKET; }

	@Override
	@SuppressWarnings("unchecked")
	public <T> T getCapability (Capability<T> capability, EnumFacing facing) {
		if (capability == CAPTrinket.INV_CAP_TRINKET) return (T) this.container;
		return null;
	}

	@Override public NBTTagCompound serializeNBT() { return this.container.serializeNBT(); }

	@Override public void deserializeNBT(NBTTagCompound nbt) { this.container.deserializeNBT(nbt); }
}
