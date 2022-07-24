package the_end_game.supporting.events;

import javax.annotation.*;

import net.minecraft.item.*;
import net.minecraft.util.*;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.event.*;
import net.minecraftforge.fml.common.eventhandler.*;
import the_end_game.*;
import the_end_game.trinket_inventory.*;
import the_end_game.trinket_inventory.cap.*;

public class ItemHandEvent {
	private static ResourceLocation capabilityResourceLocation = new ResourceLocation(Main.MODID, "trinket_cap");
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
 	public void itemCapabilityAttach(AttachCapabilitiesEvent<ItemStack> event) {
		ItemStack stack = event.getObject();
		if (stack.isEmpty() || !(stack.getItem() instanceof ITrinket) || stack.hasCapability(CAPTrinket.INV_CAP_TRINKET_ITEM, null) || event.getCapabilities().values().stream().anyMatch(c -> c.hasCapability(CAPTrinket.INV_CAP_TRINKET_ITEM, null))) return;
		event.addCapability(capabilityResourceLocation, new ICapabilityProvider() {
			@Override public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) { return capability == CAPTrinket.INV_CAP_TRINKET_ITEM; }

			@Nullable
			@Override
			public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
				return capability == CAPTrinket.INV_CAP_TRINKET_ITEM ? CAPTrinket.INV_CAP_TRINKET_ITEM.cast((ITrinket) stack.getItem()) : null;
			}
		});
	}
}
