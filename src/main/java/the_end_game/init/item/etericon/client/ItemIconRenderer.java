package the_end_game.init.item.etericon.client;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public final class ItemIconRenderer {
    private ItemIconRenderer() {
    }

    public static void draw(GuiGraphics graphics, ItemStack stack, int x, int y, int width, int height) {
        if (stack.isEmpty()) { return; }
        Minecraft minecraft = Minecraft.getInstance();
        BakedModel model = minecraft.getItemRenderer().getModel(stack, minecraft.level, minecraft.player, 0);
        PoseStack pose = graphics.pose();
        pose.pushPose();
        pose.translate(x + width / 2.0F, y + height / 2.0F, 50.0F);
        pose.scale(width, -height, Math.max(width, height));
        boolean flat = !model.usesBlockLight();
        if (flat) { Lighting.setupForFlatItems(); }
        minecraft.getItemRenderer().render(stack, ItemDisplayContext.GUI, false, pose, graphics.bufferSource(), 15728880, OverlayTexture.NO_OVERLAY, model);
        graphics.flush();
        if (flat) { Lighting.setupFor3DItems(); }
        pose.popPose();
    }
}
