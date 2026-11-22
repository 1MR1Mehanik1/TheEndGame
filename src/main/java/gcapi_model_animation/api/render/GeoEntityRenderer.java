package gcapi_model_animation.api.render;

import com.mojang.blaze3d.vertex.PoseStack;
import gcapi_model_animation.api.ModelBinder;
import gcapi_model_animation.api.animation.*;
import gcapi_model_animation.api.geo.render.built.GeoModel;
import gcapi_model_animation.api.loader.ResourceCache;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.HashMap;
import java.util.Map;

public class GeoEntityRenderer<T extends Entity> extends EntityRenderer<T> {

    private final Class<T> entityClass;
    private final String defaultAnimation = "default";

    protected GeoEntityRenderer(EntityRendererProvider.Context ctx, Class<T> entityClass) {
        super(ctx);
        this.entityClass = entityClass;
    }

    @Override public void render(T entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        ResourceLocation modelLoc = ModelBinder.getModel(entityClass);
        if (modelLoc == null) return;

        GeoModel model = ResourceCache.getGeometry(modelLoc);
        ResourceLocation tex = ModelBinder.getTexture(modelLoc);
        if (model == null || tex == null) return;

        // Анимация
        ResourceLocation animLoc = ResourceLocation.fromNamespaceAndPath(
                modelLoc.getNamespace(),
                modelLoc.getPath().replace(".geo.json", ".animation.json"));
        Map<String, AnimationData> animMap = ResourceCache.getAnimations(animLoc);
        if (animMap != null) {
            AnimationData data = animMap.get(defaultAnimation);
            if (data != null) {
                float time = (entity.tickCount + partialTick) / 20f;
                if (data.loop) time %= (float) data.lengthInSeconds;
                model = AnimationApplier.applyAnimation(model, data, time);
            }
        }

        poseStack.pushPose();
        poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(180f - entityYaw));
        poseStack.scale(-1f, -1f, 1f);
        poseStack.scale(0.0625f, 0.0625f, 0.0625f);
        GeometryRenderer.render(model, tex, poseStack, buffer, packedLight);
        poseStack.popPose();

        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
    }

    @Override public ResourceLocation getTextureLocation(T entity) { return ModelBinder.getTexture(ModelBinder.getModel(entityClass)); }
}