package the_end_game.trinket_inventory.gui;

import java.io.*;

import net.minecraft.client.gui.inventory.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.resources.*;
import net.minecraft.entity.player.*;
import net.minecraft.util.*;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import the_end_game.*;
import the_end_game.supporting.*;
import the_end_game.trinket_inventory.container.*;

public class TrinketGui extends InventoryEffectRenderer {
	public static final int ID = 0;
	public ResourceLocation IMG_GUI = new ResourceLocation(Main.MODID + ":textures/othes/gui/trinkets_inventory.png");
	private float oldMouseX;
    private float oldMouseY;
    
    public TrinketGui(EntityPlayer player) {
		super(new TrinketContainer(player.inventory, !player.getEntityWorld().isRemote, player));
		this.allowUserInput = true;
	}

	private void resetGuiLeft() { this.guiLeft = (this.width - this.xSize) / 2; }

	@Override 
	public void updateScreen() {
		((TrinketContainer) inventorySlots).trinkets_handler.setEventBlock(false);
		updateActivePotionEffects();
		resetGuiLeft();
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		this.fontRenderer.drawString(I18n.format("container.crafting"), 97, 8, 4210752);
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground(); //dark backdrop
		this.hasActivePotionEffects = false; //rendering active effects
        this.oldMouseX = (float)mouseX;
        this.oldMouseY = (float)mouseY;
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);//drawing labels(item)
    }

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.pushMatrix();
		GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
		
		this.mc.getTextureManager().bindTexture(GuiInventory.INVENTORY_BACKGROUND);
		this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
		
		this.mc.getTextureManager().bindTexture(IMG_GUI);
		this.drawTexturedModalRect(this.guiLeft +- 53, this.guiTop, 0, 0, 51, 104);
		
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GuiInventory.drawEntityOnScreen(this.guiLeft + 51, this.guiTop + 75, 30, (float) (this.guiLeft + 51) - this.oldMouseX, (float) (this.guiTop + 75 - 50) - this.oldMouseY, this.mc.player);
		GlStateManager.popMatrix();
	}

	@Override
	protected void keyTyped(char par1, int par2) throws IOException {
		if (par2 == KeyHandler.openKey.getKeyCode()) this.mc.player.closeScreen(); else super.keyTyped(par1, par2);
	}

	public void displayNormalInventory() {
		GuiInventory gui = new GuiInventory(this.mc.player);
		ReflectionHelper.setPrivateValue(GuiInventory.class, gui, this.oldMouseX, "oldMouseX", "field_147048_u");
		ReflectionHelper.setPrivateValue(GuiInventory.class, gui, this.oldMouseY, "oldMouseY", "field_147047_v");
		this.mc.displayGuiScreen(gui);
	}
}
