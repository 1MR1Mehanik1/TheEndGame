package gcapi_model_animation.api.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import gcapi_model_animation.api.geo.render.built.*;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.joml.*;

public class GeometryRenderer {
    public static void render(GeoModel model, VertexConsumer buffer, PoseStack poseStack, int packedLight) {
        render(model, buffer, poseStack, packedLight, OverlayTexture.NO_OVERLAY);
    }

    public static void render(GeoModel model, VertexConsumer buffer, PoseStack poseStack, int packedLight, int packedOverlay) {
        if (model == null) return;
        for (GeoBone bone : model.topLevelBones) {
            renderRecursively(buffer, poseStack, bone, 1f, 1f, 1f, 1f, packedLight, packedOverlay);
        }
    }

    public static void render(GeoModel model, ResourceLocation texture, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        render(model, texture, poseStack, bufferSource, packedLight, OverlayTexture.NO_OVERLAY);
    }

    public static void render(GeoModel model, ResourceLocation texture, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        if (model == null || texture == null) return;
        RenderType type = RenderType.entityCutoutNoCull(texture);
        VertexConsumer buffer = bufferSource.getBuffer(type);
        for (GeoBone bone : model.topLevelBones) {
            renderRecursively(buffer, poseStack, bone, 1f, 1f, 1f, 1f, packedLight, packedOverlay);
        }
    }

    public static void renderBone(GeoBone bone, VertexConsumer buffer, PoseStack poseStack, int packedLight) {
        renderBone(bone, buffer, poseStack, packedLight, OverlayTexture.NO_OVERLAY);
    }

    public static void renderBone(GeoBone bone, VertexConsumer buffer, PoseStack poseStack, int packedLight, int packedOverlay) {
        if (bone == null) return;
        renderRecursively(buffer, poseStack, bone, 1f, 1f, 1f, 1f, packedLight, packedOverlay);
    }

    private static void renderRecursively(VertexConsumer buffer, PoseStack poseStack, GeoBone bone, float r, float g, float b, float a, int packedLight, int packedOverlay) {
        poseStack.pushPose();

        // 1) position — в системе родителя (НЕ поворачивается поворотом кости)
        poseStack.translate(bone.getPositionX() / 16f, bone.getPositionY() / 16f, bone.getPositionZ() / 16f);

        // 2) к пивоту
        poseStack.translate(bone.rotationPointX / 16f, bone.rotationPointY / 16f, bone.rotationPointZ / 16f);

        // 3) вращения (Z→Y→X, как в GeckoLib rotationZYX)
        if (bone.getRotationZ() != 0f) poseStack.mulPose(Axis.ZP.rotation(bone.getRotationZ()));
        if (bone.getRotationY() != 0f) poseStack.mulPose(Axis.YP.rotation(bone.getRotationY()));
        if (bone.getRotationX() != 0f) poseStack.mulPose(Axis.XP.rotation(bone.getRotationX()));

        poseStack.scale(bone.getScaleX(), bone.getScaleY(), bone.getScaleZ());

        // 4) обратно от пивота
        poseStack.translate(-bone.rotationPointX / 16f, -bone.rotationPointY / 16f, -bone.rotationPointZ / 16f);

        // --- Кубы ---
        if (!bone.isHidden && !bone.areCubesHidden) {
            for (GeoCube cube : bone.childCubes) {
                poseStack.pushPose();
                renderCube(buffer, poseStack, cube, r, g, b, a, packedLight, packedOverlay);
                poseStack.popPose();
            }
        }
        // --- Дочерние кости ---
        if (!bone.hideChildBonesToo) {
            for (GeoBone child : bone.childBones) {
                renderRecursively(buffer, poseStack, child, r, g, b, a, packedLight, packedOverlay);
            }
        }

        poseStack.popPose();
    }

    private static void renderCube(VertexConsumer buffer, PoseStack poseStack, GeoCube cube, float red, float green, float blue, float alpha, int packedLight, int packedOverlay) {
        poseStack.translate(cube.pivot.x / 16f, cube.pivot.y / 16f, cube.pivot.z / 16f);
        if (cube.rotation.z != 0f) poseStack.mulPose(Axis.ZP.rotation(cube.rotation.z));
        if (cube.rotation.y != 0f) poseStack.mulPose(Axis.YP.rotation(cube.rotation.y));
        if (cube.rotation.x != 0f) poseStack.mulPose(Axis.XP.rotation(cube.rotation.x));
        poseStack.translate(-cube.pivot.x / 16f, -cube.pivot.y / 16f, -cube.pivot.z / 16f);

        PoseStack.Pose pose = poseStack.last();
        Matrix4f modelMat = pose.pose();

        for (GeoQuad quad : cube.quads) {
            if (quad == null) continue;

            Vector3f normal = new Vector3f(quad.normal.getX(), quad.normal.getY(), quad.normal.getZ());
            pose.normal().transform(normal);

            if ((cube.size.y == 0 || cube.size.z == 0) && normal.x < 0) normal.x *= -1;
            if ((cube.size.x == 0 || cube.size.z == 0) && normal.y < 0) normal.y *= -1;
            if ((cube.size.x == 0 || cube.size.y == 0) && normal.z < 0) normal.z *= -1;

            for (GeoVertex v : quad.vertices) {
                Vector4f vec = new Vector4f(v.position.x, v.position.y, v.position.z, 1f);
                modelMat.transform(vec);
                buffer.addVertex(vec.x, vec.y, vec.z)
                        .setColor(red, green, blue, alpha)
                        .setUv(v.textureU, v.textureV)
                        .setOverlay(packedOverlay)
                        .setLight(packedLight)
                        .setNormal(normal.x, normal.y, normal.z);
            }
        }
    }
}