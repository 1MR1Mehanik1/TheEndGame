package gcapi_model_animation.api.animation;

import java.util.Map;

public class AnimationData {
    public final double lengthInSeconds;
    public final boolean loop;
    public final Map<String, BoneAnimation> boneAnimations;

    public AnimationData(double lengthInSeconds, boolean loop, Map<String, BoneAnimation> boneAnimations) {
        this.lengthInSeconds = lengthInSeconds;
        this.loop = loop;
        this.boneAnimations = boneAnimations;
    }
}