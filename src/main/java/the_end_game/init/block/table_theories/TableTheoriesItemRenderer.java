package the_end_game.init.block.table_theories;

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

public class TableTheoriesItemRenderer extends BlockEntityWithoutLevelRenderer {
    public static final TableTheoriesItemRenderer INSTANCE = new TableTheoriesItemRenderer();
    private TableTheoriesItemRenderer() { super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels()); }
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
        copy.getBone("inkwell").ifPresent(bone -> {
            bone.isHidden = true;
            bone.hideChildBonesToo = true;
        });
        copy.getBone("paper_researches").ifPresent(bone -> {
            bone.isHidden = true;
            bone.hideChildBonesToo = true;
        });
        copy.getBone("paper").ifPresent(bone -> {
            bone.isHidden = true;
            bone.hideChildBonesToo = true;
        });
        copy.getBone("featherontable").ifPresent(bone -> {
            bone.isHidden = true;
            bone.hideChildBonesToo = true;
        });
        copy.getBone("featherininkwell").ifPresent(bone -> {
            bone.isHidden = true;
            bone.hideChildBonesToo = true;
        });

        poseStack.pushPose();
        poseStack.translate(0.5D, 0.0D, 0.5D);
        GeometryRenderer.render(copy, texture, poseStack, buffer, packedLight, packedOverlay);
        poseStack.popPose();
    }
}
