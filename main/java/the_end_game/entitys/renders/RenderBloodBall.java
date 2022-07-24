package the_end_game.entitys.renders;

import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.vertex.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import the_end_game.*;
import the_end_game.entitys.*;

public class RenderBloodBall extends Render<EntityBloodBall> {
public static final ResourceLocation TEXTURES = new ResourceLocation(Main.MODID, "textures/entity/blood_ball.png");
	
	public RenderBloodBall(RenderManager renderManager) {
        super(renderManager);
        this.shadowSize = 0.15F;
        this.shadowOpaque = 0.75F;
    }
	
	public void doRender(EntityBloodBall entity, double x, double y, double z, float entityYaw, float partialTicks) {
        if (!this.renderOutlines) {
            GlStateManager.pushMatrix();
            GlStateManager.translate((float) x, (float) y, (float) z);
            this.bindEntityTexture(entity);
            RenderHelper.enableStandardItemLighting();
            int i = entity.getTextureByXP();
            float f = (float) (i % 16 + 0) / 16.0F;
            float f1 = (float) (i % 16 + 16) / 16.0F;
            float f2 = (float) (i / 16 + 0) / 16.0F;
            float f3 = (float) (i / 16 + 16) / 16.0F;
            float f4 = 1.0F;
            float f5 = 0.5F;
            float f6 = 0.25F;
            int j = entity.getBrightnessForRender(partialTicks);
            int k = j % 65536;
            int l = j / 65536;

            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) k, (float) l);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            float f8 = 255.0F;
            float f9 = ((float) entity.xpColor + partialTicks) / 2.0F;

            l = (int) ((MathHelper.sin(f9 + 0.0F) + 1.0F) * 0.001F * 255.0F);
            boolean i1 = true;
            int j1 = (int) ((MathHelper.sin(f9 + 4.1887903F) + 1.0F) * 0.08F * 255.0F);

            GlStateManager.translate(0.0F, 0.1F, 0.0F);
            GlStateManager.rotate(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate((float) (this.renderManager.options.thirdPersonView == 2 ? -1 : 1) * -this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
            float f7 = 0.3F;

            GlStateManager.scale(0.3F, 0.3F, 0.3F);
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferBuilder = tessellator.getBuffer();

            bufferBuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
            short red = 255;

            bufferBuilder.pos(-0.5D, -0.25D, 0.0D).tex((double) f, (double) f3).color(red, j1, l, 128).normal(0.0F, 1.0F, 0.0F).endVertex();
            bufferBuilder.pos(0.5D, -0.25D, 0.0D).tex((double) f1, (double) f3).color(red, j1, l, 128).normal(0.0F, 1.0F, 0.0F).endVertex();
            bufferBuilder.pos(0.5D, 0.75D, 0.0D).tex((double) f1, (double) f2).color(red, j1, l, 128).normal(0.0F, 1.0F, 0.0F).endVertex();
            bufferBuilder.pos(-0.5D, 0.75D, 0.0D).tex((double) f, (double) f2).color(red, j1, l, 128).normal(0.0F, 1.0F, 0.0F).endVertex();
            tessellator.draw();
            GlStateManager.disableBlend();
            GlStateManager.disableRescaleNormal();
            GlStateManager.popMatrix();
            super.doRender(entity, x, y, z, entityYaw, partialTicks);
        }
    }
	
	@Override
	protected ResourceLocation getEntityTexture(EntityBloodBall entity) { return TEXTURES; }
}
