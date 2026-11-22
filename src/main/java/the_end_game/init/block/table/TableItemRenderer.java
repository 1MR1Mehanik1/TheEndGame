package the_end_game.init.block.table;

import com.mojang.blaze3d.vertex.PoseStack;
import gcapi_model_animation.api.ModelBinder;
import gcapi_model_animation.api.geo.render.built.GeoModel;
import gcapi_model_animation.api.loader.ResourceCache;
import gcapi_model_animation.api.render.GeometryRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class TableItemRenderer extends BlockEntityWithoutLevelRenderer {
    public static final TableItemRenderer INSTANCE = new TableItemRenderer();
    private TableItemRenderer() { super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels()); }
    @Override public void renderByItem(ItemStack stack, ItemDisplayContext context, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        if (stack.isEmpty()) return;
        Block block = Block.byItem(stack.getItem());
        ResourceLocation key = BuiltInRegistries.BLOCK.getKey(block);
        ResourceLocation modelLocation = ModelBinder.getModel(key);
        if (modelLocation == null) return;
        GeoModel model = ResourceCache.getGeometry(modelLocation);
        ResourceLocation texture = ModelBinder.getTexture(key);
        if (model == null || texture == null) return;

        GeoModel copy = model.makeCopy();
        for (String name : new String[]{"leg_lb", "leg_lf", "leg_rb", "leg_rf", "e_cloth", "w_cloth", "n_cloth", "s_cloth"}) {
            copy.getBone(name).ifPresent(bone -> bone.isHidden = false);
        }
        copy.getBone("up_cloth").ifPresent(bone -> {
            bone.setScaleX(1.0F);
            bone.setScaleZ(1.0F);
        });
        copy.getBone("Cloth").ifPresent(bone -> {
            bone.isHidden = true;
            bone.hideChildBonesToo = true;
        });

        poseStack.pushPose();
        poseStack.translate(0.5D, 0.0D, 0.5D);
        GeometryRenderer.render(copy, texture, poseStack, buffer, packedLight, packedOverlay);
        poseStack.popPose();
    }
}
