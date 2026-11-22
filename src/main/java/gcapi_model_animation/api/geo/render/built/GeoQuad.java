package gcapi_model_animation.api.geo.render.built;

import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;

public class GeoQuad {
    public GeoVertex[] vertices;
    public final Vec3i normal;
    public Direction direction;
    public float uvRotation;

    public GeoQuad(GeoVertex[] verticesIn, float u1, float v1, float uSize, float vSize, float texWidth, float texHeight, Boolean mirror, Direction directionIn) {
        this(verticesIn, u1, v1, uSize, vSize, texWidth, texHeight, mirror, directionIn, 0f);
    }

    public GeoQuad(GeoVertex[] verticesIn, float u1, float v1, float uSize, float vSize, float texWidth, float texHeight, Boolean mirror, Direction directionIn, float uvRotation) {
        this.direction = directionIn;
        this.uvRotation = uvRotation;
        this.vertices = new GeoVertex[4];

        float u2 = u1 + uSize;
        float v2 = v1 + vSize;

        u1 /= texWidth;  u2 /= texWidth;
        v1 /= texHeight; v2 /= texHeight;

        float a0, b0, a1, b1, a2, b2, a3, b3;
        int rot = (int) ((uvRotation % 360 + 360) % 360);
        switch (rot) {
            case 90 -> {
                a0 = u1; b0 = v1;   // old TL -> new TR
                a1 = u1; b1 = v2;   // old BL -> new TL
                a2 = u2; b2 = v2;   // old BR -> new BL
                a3 = u2; b3 = v1;   // old TR -> new BR
            }
            case 180 -> {
                a0 = u1; b0 = v2;
                a1 = u2; b1 = v2;
                a2 = u2; b2 = v1;
                a3 = u1; b3 = v1;
            }
            case 270 -> {
                a0 = u2; b0 = v2;
                a1 = u2; b1 = v1;
                a2 = u1; b2 = v1;
                a3 = u1; b3 = v2;
            }
            default -> {
                a0 = u2; b0 = v1;
                a1 = u1; b1 = v1;
                a2 = u1; b2 = v2;
                a3 = u2; b3 = v2;
            }
        }

        // Зеркало: меняем UV у v0<->v1 и v2<->v3
        if (mirror != null && mirror) {
            float tu = a0, tv = b0; a0 = a1; b0 = b1; a1 = tu; b1 = tv;
            tu = a2; tv = b2; a2 = a3; b2 = b3; a3 = tu; b3 = tv;
        }

        vertices[0] = verticesIn[0].setTextureUV(a0, b0);
        vertices[1] = verticesIn[1].setTextureUV(a1, b1);
        vertices[2] = verticesIn[2].setTextureUV(a2, b2);
        vertices[3] = verticesIn[3].setTextureUV(a3, b3);

        this.normal = directionIn.getNormal();
    }

    public GeoQuad(GeoVertex[] verticesIn, double[] uvCoords, double[] uvSize, float texWidth, float texHeight, Boolean mirror, Direction directionIn) {
        this(verticesIn, (float) uvCoords[0], (float) uvCoords[1], (float) uvSize[0], (float) uvSize[1], texWidth, texHeight, mirror, directionIn, 0f);
    }

    public GeoQuad(GeoVertex[] verticesIn, double[] uvCoords, double[] uvSize, float texWidth, float texHeight, Boolean mirror, Direction directionIn, float uvRotation) {
        this(verticesIn, (float) uvCoords[0], (float) uvCoords[1], (float) uvSize[0], (float) uvSize[1], texWidth, texHeight, mirror, directionIn, uvRotation);
    }
}