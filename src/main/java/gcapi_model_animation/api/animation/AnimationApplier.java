package gcapi_model_animation.api.animation;

import gcapi_model_animation.api.geo.render.built.GeoBone;
import gcapi_model_animation.api.geo.render.built.GeoModel;

import java.util.List;

public class AnimationApplier {
    public static GeoModel applyAnimation(GeoModel model, AnimationData animData, float time) {
        GeoModel animated = model.makeCopy();
        applyToBoneList(animated.topLevelBones, animData, time);
        return animated;
    }

    private static void applyToBoneList(List<GeoBone> bones, AnimationData animData, float time) {
        for (GeoBone bone : bones) {
            BoneAnimation boneAnim = animData.boneAnimations.get(bone.name);
            if (boneAnim != null) {
                float[] pos = AnimationHelper.getValueAt(boneAnim.position, time);
                float[] rot = AnimationHelper.getValueAt(boneAnim.rotation, time);
                float[] scl = AnimationHelper.getValueAt(boneAnim.scale, time);
                if (pos != null) {
                    bone.setPositionX(pos[0]);
                    bone.setPositionY(pos[1]);
                    bone.setPositionZ(pos[2]);
                }
                if (rot != null) {
                    // градусы -> радианы
                    bone.setRotationX((float) Math.toRadians(rot[0]));
                    bone.setRotationY((float) Math.toRadians(rot[1]));
                    bone.setRotationZ((float) Math.toRadians(rot[2]));
                }
                if (scl != null) {
                    bone.setScaleX(scl[0]);
                    bone.setScaleY(scl[1]);
                    bone.setScaleZ(scl[2]);
                }
            }
            applyToBoneList(bone.childBones, animData, time);
        }
    }
}