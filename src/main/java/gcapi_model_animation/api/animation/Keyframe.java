package gcapi_model_animation.api.animation;

public class Keyframe {
    public final float time; // секунды
    public final float x, y, z;

    public Keyframe(float time, float x, float y, float z) {
        this.time = time;
        this.x = x;
        this.y = y;
        this.z = z;
    }
}