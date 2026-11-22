package gcapi_model_animation.api.geo.render.built;

import org.joml.Vector3f;

public class GeoVertex {
    public final Vector3f position;
    public float textureU;
    public float textureV;

    public GeoVertex(float x, float y, float z) {
        this.position = new Vector3f(x, y, z);
    }

    public GeoVertex(Vector3f posIn, float texU, float texV) {
        this.position = new Vector3f(posIn);
        this.textureU = texU;
        this.textureV = texV;
    }

    public GeoVertex setTextureUV(float texU, float texV) {
        return new GeoVertex(this.position, texU, texV);
    }
}