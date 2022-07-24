package the_end_game.trinket_inventory;

public enum TrinketType {
	AMULET(0,1),
	BRACER(2,3),
	RING(4,5,6,7),
	CHARM_HEALTH(8),
	CHARM_ARMOR(9),
	
	AAMULET_CHARM(0,1,8,9),
	NORMAL(0,1,2,3,4,5,6,7,8,9);
	
	int[] validSlots;

	private TrinketType(int ... validSlots) { this.validSlots = validSlots; }

	public boolean hasSlot(int slot) { for (int s:validSlots) if (s == slot) return true; return false; }

	public int[] getValidSlots() { return validSlots; }
}
