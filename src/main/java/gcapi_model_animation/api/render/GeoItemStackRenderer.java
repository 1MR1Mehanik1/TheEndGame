package gcapi_model_animation.api.render;

import com.mojang.blaze3d.vertex.PoseStack;
import gcapi_model_animation.api.ModelBinder;
import gcapi_model_animation.api.geo.render.built.GeoModel;
import gcapi_model_animation.api.loader.ResourceCache;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.ItemDisplayContext;

public class GeoItemStackRenderer extends BlockEntityWithoutLevelRenderer {
    public static final GeoItemStackRenderer INSTANCE = new GeoItemStackRenderer();

    public GeoItemStackRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    @Override public void renderByItem(ItemStack stack, ItemDisplayContext ctx, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        if (stack.isEmpty()) return;
        Item item = stack.getItem();
        Block block = Block.byItem(item);

        ResourceLocation keyItem  = BuiltInRegistries.ITEM.getKey(item);
        ResourceLocation keyBlock = block != null ? BuiltInRegistries.BLOCK.getKey(block) : null;

        if (keyItem != null) {
            ResourceLocation modelLoc = ModelBinder.getModel(keyItem);
            if (modelLoc != null) {
                GeoModel model = ResourceCache.getGeometry(modelLoc);
                ResourceLocation tex = ModelBinder.getTexture(keyItem);
                if (model != null && tex != null) {
                    poseStack.pushPose();
                    poseStack.translate(0.5, 0.5, 0.5);
                    GeometryRenderer.render(model, tex, poseStack, buffer, packedLight, packedOverlay);
                    poseStack.popPose();
                }
            }
        }
        if (keyBlock != null) {
            ResourceLocation modelLoc = ModelBinder.getModel(keyBlock);
            if (modelLoc != null) {
                GeoModel model = ResourceCache.getGeometry(modelLoc);
                ResourceLocation tex = ModelBinder.getTexture(keyBlock);
                if (model != null && tex != null) {
                    poseStack.pushPose();
                    poseStack.translate(0.5, 0.5, 0.5);
                    GeometryRenderer.render(model, tex, poseStack, buffer, packedLight, packedOverlay);
                    poseStack.popPose();
                }
            }
        }
    }
}