package the_end_game.trinket_inventory.gui;

import org.lwjgl.opengl.*;

import net.minecraft.client.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.inventory.*;
import net.minecraft.client.renderer.*;
import net.minecraft.util.*;
import the_end_game.*;
import the_end_game.supporting.network.*;

public class TrinketGuiButton extends GuiButton {
	private final GuiContainer parentGui;
	public ResourceLocation IMG_GUI = new ResourceLocation(Main.MODID + ":textures/othes/gui/trinkets_inventory.png");
	
	public TrinketGuiButton(int buttonId, GuiContainer gui, int x, int y, int width, int height, String buttonText) {
		super(buttonId, x, gui.getGuiTop() + y, width, height, buttonText);
		this.parentGui = gui;
	}
	
	@Override
	public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
		boolean pressed = super.mousePressed(mc, mouseX - this.parentGui.getGuiLeft(), mouseY);
		if (pressed) if (parentGui instanceof GuiInventory) PacketHandler.INSTANCE.sendToServer(new PacketOpenTrinketInventory());
		else {
			((TrinketGui) parentGui).displayNormalInventory();
			PacketHandler.INSTANCE.sendToServer(new PacketOpenNormalInventory());
		}
		return pressed;
	}
	
	public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        if (this.visible) {
        	int x = this.x + this.parentGui.getGuiLeft();
            FontRenderer fontrenderer = mc.fontRenderer;
            
            mc.getTextureManager().bindTexture(IMG_GUI);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            
            this.hovered = mouseX >= x && mouseY >= this.y && mouseX < x + this.width && mouseY < this.y + this.height;
            int k = this.getHoverState(this.hovered);

            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
			GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			
			GlStateManager.pushMatrix();
			GlStateManager.translate(0, 0, 200);
            if (k == 1) this.drawTexturedModalRect(x, y, 72, 0, 20, 20); else this.drawTexturedModalRect(x, y, 72, 20, 20, 20);
            
            GlStateManager.popMatrix();
			this.mouseDragged(mc, mouseX, mouseY);
        }
    }
}
