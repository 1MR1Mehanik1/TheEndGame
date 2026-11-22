package gcapi_model_animation.api.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import gcapi_model_animation.api.geo.render.built.GeoBone;
import gcapi_model_animation.api.geo.render.built.GeoModel;
import gcapi_model_animation.api.loader.ResourceCache;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import javax.annotation.Nullable;

public class GeoArmorModel extends HumanoidModel<LivingEntity> {
    private final GeoModel geoModel;
    private final ResourceLocation texture;
    private final EquipmentSlot currentSlot;

    @Nullable private GeoBone boneHead, boneBody,
            boneRightArm, boneLeftArm,
            boneRightLeg, boneLeftLeg,
            boneRightBoot, boneLeftBoot;

    private boolean hideHeadBone = false;
    private boolean hideBodyBone = false;
    private boolean hideRightArmBone = false;
    private boolean hideLeftArmBone = false;
    private boolean hideRightLegBone = false;
    private boolean hideLeftLegBone = false;
    private boolean hideRightBootBone = false;
    private boolean hideLeftBootBone = false;

    public GeoArmorModel(ModelPart root, ResourceLocation modelLoc, ResourceLocation texture, EquipmentSlot slot) {
        super(root);
        GeoModel cached = ResourceCache.getGeometry(modelLoc);
        this.geoModel = (cached != null) ? cached.makeCopy() : null;
        this.texture  = texture;
        this.currentSlot = slot;
        cacheBones();
    }

    private void cacheBones() {
        if (geoModel == null) return;

        boneHead = geoModel.getBone("armorHead").orElse(null);
        boneBody = geoModel.getBone("armorBody").orElse(null);
        boneRightArm = geoModel.getBone("armorRightArm").orElse(null);
        boneLeftArm = geoModel.getBone("armorLeftArm").orElse(null);
        boneRightLeg = geoModel.getBone("armorRightLeg").orElse(null);
        boneLeftLeg = geoModel.getBone("armorLeftLeg").orElse(null);
        boneRightBoot = geoModel.getBone("armorRightBoot").orElse(null);
        boneLeftBoot = geoModel.getBone("armorLeftBoot").orElse(null);
    }

    public GeoArmorModel setHideHeadBone(boolean hide) { this.hideHeadBone = hide; return this; }
    public GeoArmorModel setHideBodyBone(boolean hide) { this.hideBodyBone = hide; return this; }
    public GeoArmorModel setHideRightArmBone(boolean hide) { this.hideRightArmBone = hide; return this; }
    public GeoArmorModel setHideLeftArmBone(boolean hide) { this.hideLeftArmBone = hide; return this; }
    public GeoArmorModel setHideRightLegBone(boolean hide) { this.hideRightLegBone = hide; return this; }
    public GeoArmorModel setHideLeftLegBone(boolean hide) { this.hideLeftLegBone = hide; return this; }
    public GeoArmorModel setHideRightBootBone(boolean hide) { this.hideRightBootBone = hide; return this; }
    public GeoArmorModel setHideBone(boolean hide) { this.hideLeftBootBone = hide; return this; }

    @Override public void renderToBuffer(PoseStack poseStack, VertexConsumer ignored, int packedLight, int packedOverlay, int color) {
        if (geoModel == null || texture == null) return;

        MultiBufferSource.BufferSource buffers = Minecraft.getInstance().renderBuffers().bufferSource();
        VertexConsumer buffer = buffers.getBuffer(RenderType.entityCutoutNoCull(texture));

        applyBoneVisibilityBySlot(currentSlot);

        if (hideHeadBone) hideWithChildren(boneHead, true);
        if (hideBodyBone) hideWithChildren(boneBody, true);
        if (hideRightArmBone) hideWithChildren(boneRightArm, true);
        if (hideLeftArmBone) hideWithChildren(boneLeftArm, true);
        if (hideRightLegBone) hideWithChildren(boneRightLeg, true);
        if (hideLeftLegBone) hideWithChildren(boneLeftLeg, true);
        if (hideRightBootBone) hideWithChildren(boneRightBoot, true);
        if (hideLeftBootBone) hideWithChildren(boneLeftBoot, true);

        applyBaseTransforms();

        poseStack.pushPose();
        poseStack.translate(0f, 24f / 16f, 0f);
        poseStack.scale(-1f, -1f, 1f);
        for (GeoBone bone : geoModel.topLevelBones) { GeometryRenderer.renderBone(bone, buffer, poseStack, packedLight, packedOverlay); }
        poseStack.popPose();
    }

    // ---------------------------------------------------------------
    //           Перенос позы ванильных ModelPart → GeoBone
    // ---------------------------------------------------------------
    private void applyBaseTransforms() {
        setPart(boneHead, this.head, 0f, 0f, true, true);

        setPart(boneBody, this.body, 0f, 0f, true, true);
        setPart(boneRightArm, this.rightArm, 5f, 2f, true, true);
        setPart(boneLeftArm, this.leftArm, -5f, 2f, true, true);

        setPart(boneRightLeg, this.rightLeg, 2f, 12f, true, true);
        setPart(boneLeftLeg, this.leftLeg, -2f, 12f, true, true);

        setPart(boneRightBoot, this.rightLeg, 2f, 12f, true, true);
        setPart(boneLeftBoot, this.leftLeg, -2f, 12f, true, true);
    }

    /**
     * Портирует позу одного ModelPart в GeoBone.
     *  offsetX / baseY — константы из GeckoLib (позиция плеча/бедра в JSON-пространстве).
     *  Позиция = (part.x + offsetX, baseY - part.y, part.z) — как в GeoArmorRenderer#applyBaseTransformations.
     *  Позиция пишется с инверсией X, чтобы скомпенсировать флип в GeometryRenderer
     *  (там translate(-posX, +posY, +posZ)).
     */
    private void setPart(@Nullable GeoBone bone, ModelPart part, float offsetX, float baseY, boolean invertPitch, boolean invertYaw) {
        if (bone == null) return;

        bone.setPositionX(part.x + offsetX);
        bone.setPositionY(baseY - part.y);
        bone.setPositionZ(part.z);

        bone.setRotationX(invertPitch ? -part.xRot : part.xRot);
        bone.setRotationY(invertYaw ? -part.yRot : part.yRot);
        bone.setRotationZ(part.zRot);

        bone.setScaleX(part.xScale);
        bone.setScaleY(part.yScale);
        bone.setScaleZ(part.zScale);
    }

    // ---------------------------------------------------------------
    //                  Видимость костей по слоту
    // ---------------------------------------------------------------
    private void applyBoneVisibilityBySlot(EquipmentSlot slot) {
        setAllBonesHidden(true);
        switch (slot) {
            case HEAD -> show(boneHead, this.head.visible);
            case CHEST -> {
                show(boneBody, this.body.visible);
                show(boneRightArm, this.rightArm.visible);
                show(boneLeftArm, this.leftArm.visible);
            }
            case LEGS -> {
                show(boneRightLeg, this.rightLeg.visible);
                show(boneLeftLeg, this.leftLeg.visible);
            }
            case FEET -> {
                show(boneRightBoot, this.rightLeg.visible);
                show(boneLeftBoot, this.leftLeg.visible);
            }
            default -> {}
        }
    }

    private void setAllBonesHidden(boolean hidden) {
        if (boneHead != null) boneHead.isHidden = hidden;
        if (boneBody != null) boneBody.isHidden = hidden;
        if (boneRightArm != null) boneRightArm.isHidden = hidden;
        if (boneLeftArm != null) boneLeftArm.isHidden = hidden;
        if (boneRightLeg != null) boneRightLeg.isHidden = hidden;
        if (boneLeftLeg != null) boneLeftLeg.isHidden = hidden;
        if (boneRightBoot != null) boneRightBoot.isHidden = hidden;
        if (boneLeftBoot != null) boneLeftBoot.isHidden = hidden;
    }

    private void show(@Nullable GeoBone bone, boolean visible) {
        if (bone == null) return;
        bone.isHidden = !visible;
    }

    private void hideWithChildren(@Nullable GeoBone bone, boolean hidden) {
        if (bone == null) return;
        bone.isHidden = hidden;
        bone.hideChildBonesToo = hidden;
    }
}