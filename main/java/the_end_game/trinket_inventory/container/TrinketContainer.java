package the_end_game.trinket_inventory.container;

import net.minecraft.enchantment.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.inventory.*;
import net.minecraft.item.*;
import the_end_game.*;
import the_end_game.trinket_inventory.*;
import the_end_game.trinket_inventory.cap.*;

public class TrinketContainer extends Container {
	public final InventoryCrafting craftMatrix = new InventoryCrafting(this, 2, 2);
	public final InventoryCraftResult craftResult = new InventoryCraftResult();
	public ITrinketItemHandler trinkets_handler;
	
	public boolean isLocalWorld;
	private final EntityPlayer thePlayer;
	private static final EntityEquipmentSlot[] equipmentSlots = new EntityEquipmentSlot[] {EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET};

	public TrinketContainer(InventoryPlayer playerInv, boolean par2, EntityPlayer player) {
		this.isLocalWorld = par2;
		this.thePlayer = player;
		trinkets_handler = player.getCapability(CAPTrinket.INV_CAP_TRINKET, null);
		
		//Craft
		this.addSlotToContainer(new SlotCrafting(playerInv.player, this.craftMatrix, this.craftResult, 0, 154, 28));
		for (int i = 0; i < 2; ++i) for (int j = 0; j < 2; ++j) this.addSlotToContainer(new Slot(this.craftMatrix, j + i * 2, 98 + j * 18, 18 + i * 18));
        //Armor
        for (int i = 0; i < 4; ++i) {
            final EntityEquipmentSlot slot = equipmentSlots[i];
            this.addSlotToContainer(new Slot(playerInv, 36 + (3 - i), 8, 8 + i * 18) {
            	@Override public int getSlotStackLimit() { return 1; }
                @Override public boolean isItemValid(ItemStack stack) { return stack.getItem().isValidArmor(stack, slot, player); }
                @Override public boolean canTakeStack(EntityPlayer player) {
					ItemStack itemstack = this.getStack();
					return !itemstack.isEmpty() && !player.isCreative() && EnchantmentHelper.hasBindingCurse(itemstack) ? false : super.canTakeStack(player);
				}
				@Override public String getSlotTexture() { return ItemArmor.EMPTY_SLOT_NAMES[slot.getIndex()]; }
            });
        }
        //Custom
		this.addSlotToContainer(new TrinketSlot(player, trinkets_handler, 0, -45, 8) { @Override public String getSlotTexture() { return Main.MODID + ":othes/gui/empty_slot_amulet"; }});
		this.addSlotToContainer(new TrinketSlot(player, trinkets_handler, 1, -26, 8) { @Override public String getSlotTexture() { return Main.MODID + ":othes/gui/empty_slot_amulet"; }});
		
		this.addSlotToContainer(new TrinketSlot(player, trinkets_handler, 2, -45, 26) { @Override public String getSlotTexture() { return Main.MODID + ":othes/gui/empty_slot_bracer"; }});
		this.addSlotToContainer(new TrinketSlot(player, trinkets_handler, 3, -26, 26) { @Override public String getSlotTexture() { return Main.MODID + ":othes/gui/empty_slot_bracer"; }});
		
		this.addSlotToContainer(new TrinketSlot(player, trinkets_handler, 4, -45, 44) { @Override public String getSlotTexture() { return Main.MODID + ":othes/gui/empty_slot_ring"; }});
		this.addSlotToContainer(new TrinketSlot(player, trinkets_handler, 5, -26, 44) { @Override public String getSlotTexture() { return Main.MODID + ":othes/gui/empty_slot_ring"; }});
		this.addSlotToContainer(new TrinketSlot(player, trinkets_handler, 6, -45, 62) { @Override public String getSlotTexture() { return Main.MODID + ":othes/gui/empty_slot_ring"; }});
		this.addSlotToContainer(new TrinketSlot(player, trinkets_handler, 7, -26, 62) { @Override public String getSlotTexture() { return Main.MODID + ":othes/gui/empty_slot_ring"; }});
		
		this.addSlotToContainer(new TrinketSlot(player, trinkets_handler, 8, -45, 80) { @Override public String getSlotTexture() { return Main.MODID + ":othes/gui/empty_slot_charm_health"; }});
		this.addSlotToContainer(new TrinketSlot(player, trinkets_handler, 9, -26, 80) { @Override public String getSlotTexture() { return Main.MODID + ":othes/gui/empty_slot_charm_armor"; }});
		//Inventory
		for (int i = 0; i < 3; ++i) for (int j = 0; j < 9; ++j) this.addSlotToContainer(new Slot(playerInv, j + (i + 1) * 9, 8 + j * 18, 84 + i * 18));
		//HotBar
		for (int i = 0; i < 9; ++i) this.addSlotToContainer(new Slot(playerInv, i, 8 + i * 18, 142));
		//Shield
		this.addSlotToContainer(new Slot(playerInv, 40, 77, 62) {
        	@Override public boolean isItemValid(ItemStack stack) { return super.isItemValid(stack); }
            @Override public String getSlotTexture() { return "minecraft:items/empty_armor_slot_shield"; }
        });
		this.onCraftMatrixChanged(this.craftMatrix);
	}
	
	@Override public void onCraftMatrixChanged(IInventory par1IInventory) { this.slotChangedCraftingGrid(this.thePlayer.getEntityWorld(), this.thePlayer, this.craftMatrix, this.craftResult); }
	
	@Override
	public void onContainerClosed(EntityPlayer player) {
		super.onContainerClosed(player);
		this.craftResult.clear();
		
		if (!player.world.isRemote) this.clearContainer(player, player.world, this.craftMatrix);
	}

	@Override public boolean canInteractWith(EntityPlayer par1EntityPlayer) { return true; }
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = (Slot)this.inventorySlots.get(index);

		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			EntityEquipmentSlot entityequipmentslot = EntityLiving.getSlotForItemStack(itemstack);
			
			int slotShift = trinkets_handler.getSlots();

			if (index == 0) {
				if (!this.mergeItemStack(itemstack1, 9 + slotShift, 45 + slotShift, true)) {
					return ItemStack.EMPTY;
				}
				slot.onSlotChange(itemstack1, itemstack);
			}
			else if (index >= 1 && index < 5) {
				if (!this.mergeItemStack(itemstack1, 9 + slotShift, 45 + slotShift, false)) {
					return ItemStack.EMPTY;
				}
			}
			else if (index >= 5 && index < 9) {
				if (!this.mergeItemStack(itemstack1, 9 + slotShift, 45 + slotShift, false)) {
					return ItemStack.EMPTY;
				}
			}

			// baubles -> inv
			else if (index >= 9 && index < 9+slotShift) {
				if (!this.mergeItemStack(itemstack1, 9 + slotShift, 45 + slotShift, false)) {
					return ItemStack.EMPTY;
				}
			}

			// inv -> armor
			else if (entityequipmentslot.getSlotType() == EntityEquipmentSlot.Type.ARMOR && !((Slot)this.inventorySlots.get(8 - entityequipmentslot.getIndex())).getHasStack()) {
				int i = 8 - entityequipmentslot.getIndex();

				if (!this.mergeItemStack(itemstack1, i, i + 1, false)) {
					return ItemStack.EMPTY;
				}
			}

			// inv -> offhand
			else if (entityequipmentslot == EntityEquipmentSlot.OFFHAND && !((Slot)this.inventorySlots.get(45 + slotShift)).getHasStack()) {
				if (!this.mergeItemStack(itemstack1, 45 + slotShift, 46 + slotShift, false)) {
					return ItemStack.EMPTY;
				}
			}
			// inv -> bauble
			else if (itemstack.hasCapability(CAPTrinket.INV_CAP_TRINKET_ITEM, null)) {
				ITrinket bauble = itemstack.getCapability(CAPTrinket.INV_CAP_TRINKET_ITEM, null);
				for (int baubleSlot : bauble.getTrinketType(itemstack).getValidSlots()) {
					if ( bauble.canEquip(itemstack1, thePlayer) && !((Slot)this.inventorySlots.get(baubleSlot + 9)).getHasStack() &&
							!this.mergeItemStack(itemstack1, baubleSlot + 9, baubleSlot + 10, false)) {
						return ItemStack.EMPTY;
					} 
					if (itemstack1.getCount() == 0) break;
				}
			}
			else if (index >= 9 + slotShift && index < 36 + slotShift) {
				if (!this.mergeItemStack(itemstack1, 36 + slotShift, 45 + slotShift, false)) {
					return ItemStack.EMPTY;
				}
			}
			else if (index >= 36 + slotShift && index < 45 + slotShift) {
				if (!this.mergeItemStack(itemstack1, 9 + slotShift, 36 + slotShift, false)) {
					return ItemStack.EMPTY;
				}
			}
			else if (!this.mergeItemStack(itemstack1, 9 + slotShift, 45 + slotShift, false)) {
				return ItemStack.EMPTY;
			}

			if (itemstack1.isEmpty()) {
				slot.putStack(ItemStack.EMPTY);
			} else {
				slot.onSlotChanged();
			}

			if (itemstack1.getCount() == itemstack.getCount()) {
				return ItemStack.EMPTY;
			}

			if (itemstack1.isEmpty() && !trinkets_handler.isEventBlocked() && slot instanceof TrinketSlot &&
					itemstack.hasCapability(CAPTrinket.INV_CAP_TRINKET_ITEM, null)) {
				itemstack.getCapability(CAPTrinket.INV_CAP_TRINKET_ITEM, null).onUnequipped(itemstack, playerIn);
			}

			ItemStack itemstack2 = slot.onTake(playerIn, itemstack1);

			if (index == 0) {
				playerIn.dropItem(itemstack2, false);
			}
		}

		return itemstack;
	}

	//private void unequipBauble(ItemStack stack) { }

	@Override
	public boolean canMergeSlot(ItemStack stack, Slot slot) {
		return slot.inventory != this.craftResult && super.canMergeSlot(stack, slot);
	}
}
