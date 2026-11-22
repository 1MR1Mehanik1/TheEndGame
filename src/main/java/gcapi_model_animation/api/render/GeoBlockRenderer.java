package gcapi_model_animation.api.render;

import gcapi_model_animation.init.basic.*;
import com.mojang.blaze3d.vertex.PoseStack;
import gcapi_model_animation.api.ModelBinder;
import gcapi_model_animation.api.animation.AnimationApplier;
import gcapi_model_animation.api.animation.AnimationData;
import gcapi_model_animation.api.geo.render.built.GeoModel;
import gcapi_model_animation.api.loader.ResourceCache;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import java.util.Map;

public class GeoBlockRenderer implements BlockEntityRenderer<GCmaBlockEntity> {
    public GeoBlockRenderer(BlockEntityRendererProvider.Context ctx) {}

    @Override public void render(GCmaBlockEntity be, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        Block block = be.getBlockState().getBlock();
        ResourceLocation key = BuiltInRegistries.BLOCK.getKey(block);

        ResourceLocation modelLoc = ModelBinder.getModel(key);
        if (modelLoc == null) return;

        GeoModel model = ResourceCache.getGeometry(modelLoc);
        ResourceLocation tex = ModelBinder.getTexture(key);
        if (model == null || tex == null) return;

        // Анимация
        GCmaBlockEntity.AnimationState state = be.animState;
        if (state != null && state.isPlaying && state.currentAnimation != null && block instanceof GCmaBlock bBlock) {
            ResourceLocation animFile = bBlock.getAnimationFile();
            if (animFile != null) {
                Map<String, AnimationData> animMap = ResourceCache.getAnimations(animFile);
                if (animMap != null) {
                    AnimationData animData = animMap.get(state.currentAnimation);
                    if (animData != null) {
                        long now = System.currentTimeMillis();
                        float elapsed = (now - state.startTime) / 1000f * state.speed;
                        double len = animData.lengthInSeconds;

                        if (!state.loop && elapsed >= len) {
                            state.isPlaying = false;
                        } else {
                            if (state.loop && len > 0) elapsed %= (float) len;
                            model = AnimationApplier.applyAnimation(model, animData, elapsed);
                        }
                    }
                }
            }
        }

        poseStack.pushPose();
        poseStack.translate(0.5, 0.0, 0.5);
        preRender(model, be, partialTick, poseStack);
        GeometryRenderer.render(model, tex, poseStack, buffer, packedLight, packedOverlay);
        poseStack.popPose();
    }

    /**
     * Вызывается после применения анимации, но до GeometryRenderer.render.
     * Переопределяй, чтобы вручную манипулировать костями/кубами модели.
     */
    protected void preRender(GeoModel model, GCmaBlockEntity be, float partialTick, PoseStack poseStack) {}
}