package gcapi_model_animation.api.animation;

import java.util.List;

public class BoneAnimation {
    public final List<Keyframe> position;
    public final List<Keyframe> rotation;
    public final List<Keyframe> scale;

    public BoneAnimation(List<Keyframe> position, List<Keyframe> rotation, List<Keyframe> scale) {
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
    }
}