package gcapi_model_animation.api.geo.render.built;

import gcapi_model_animation.api.geo.raw.pojo.*;
import gcapi_model_animation.util.VectorUtils;
import net.minecraft.core.Direction;
import org.joml.Vector3d;
import org.joml.Vector3f;

public class GeoCube {
    public GeoQuad[] quads = new GeoQuad[6];
    public Vector3f pivot;
    public Vector3f rotation;
    public Vector3f size = new Vector3f();
    public double inflate;
    public Boolean mirror;

    private GeoCube(double[] size) {
        if (size.length >= 3) this.size.set((float) size[0], (float) size[1], (float) size[2]);
    }

    public static GeoCube createFromPojoCube(Cube cubeIn, ModelProperties properties, Double boneInflate, Boolean mirror) {
        GeoCube cube = new GeoCube(cubeIn.getSize());

        UvUnion uvUnion = cubeIn.getUv();
        UvFaces faces = uvUnion.faceUV;
        boolean isBoxUV = uvUnion.isBoxUV;
        cube.mirror = cubeIn.getMirror();
        cube.inflate = cubeIn.getInflate() == null
                ? (boneInflate == null ? 0 : boneInflate)
                : cubeIn.getInflate() / 16.0;

        float textureHeight = properties.getTextureHeight().floatValue();
        float textureWidth  = properties.getTextureWidth().floatValue();

        Vector3d size = VectorUtils.fromArray(cubeIn.getSize());
        Vector3d origin = VectorUtils.fromArray(cubeIn.getOrigin());
        origin = new Vector3d(-(origin.x + size.x) / 16.0, origin.y / 16.0, origin.z / 16.0);

        size.x *= 0.0625; size.y *= 0.0625; size.z *= 0.0625;

        Vector3f rotation = VectorUtils.convertDoubleToFloat(VectorUtils.fromArray(cubeIn.getRotation()));
        rotation.x *= -1; rotation.y *= -1;
        rotation.set((float) Math.toRadians(rotation.x),
                (float) Math.toRadians(rotation.y),
                (float) Math.toRadians(rotation.z));

        Vector3f pivot = VectorUtils.convertDoubleToFloat(VectorUtils.fromArray(cubeIn.getPivot()));
        pivot.x *= -1;
        cube.pivot = pivot;
        cube.rotation = rotation;

        GeoVertex P1 = new GeoVertex((float)(origin.x - cube.inflate), (float)(origin.y - cube.inflate), (float)(origin.z - cube.inflate));
        GeoVertex P2 = new GeoVertex((float)(origin.x - cube.inflate), (float)(origin.y - cube.inflate), (float)(origin.z + size.z + cube.inflate));
        GeoVertex P3 = new GeoVertex((float)(origin.x - cube.inflate), (float)(origin.y + size.y + cube.inflate), (float)(origin.z - cube.inflate));
        GeoVertex P4 = new GeoVertex((float)(origin.x - cube.inflate), (float)(origin.y + size.y + cube.inflate), (float)(origin.z + size.z + cube.inflate));
        GeoVertex P5 = new GeoVertex((float)(origin.x + size.x + cube.inflate), (float)(origin.y - cube.inflate), (float)(origin.z - cube.inflate));
        GeoVertex P6 = new GeoVertex((float)(origin.x + size.x + cube.inflate), (float)(origin.y - cube.inflate), (float)(origin.z + size.z + cube.inflate));
        GeoVertex P7 = new GeoVertex((float)(origin.x + size.x + cube.inflate), (float)(origin.y + size.y + cube.inflate), (float)(origin.z - cube.inflate));
        GeoVertex P8 = new GeoVertex((float)(origin.x + size.x + cube.inflate), (float)(origin.y + size.y + cube.inflate), (float)(origin.z + size.z + cube.inflate));

        GeoQuad quadWest, quadEast, quadNorth, quadSouth, quadUp, quadDown;

        if (!isBoxUV) {
            FaceUv west = faces.getWest(), east = faces.getEast(), north = faces.getNorth(), south = faces.getSouth(), up = faces.getUp(), down = faces.getDown();

            float rW = west  == null ? 0f : (float) west.getUvRotationDegrees();
            float rE = east  == null ? 0f : (float) east.getUvRotationDegrees();
            float rN = north == null ? 0f : (float) north.getUvRotationDegrees();
            float rS = south == null ? 0f : (float) south.getUvRotationDegrees();
            float rU = up == null ? 0f : (float) up.getUvRotationDegrees();
            float rD = down  == null ? 0f : (float) down.getUvRotationDegrees();

            quadWest = west == null ? null : new GeoQuad(new GeoVertex[]{P4, P3, P1, P2}, west.getUv(), west.getUvSize(), textureWidth, textureHeight, cubeIn.getMirror(), Direction.WEST, rW);
            quadEast = east == null ? null : new GeoQuad(new GeoVertex[]{P7, P8, P6, P5}, east.getUv(), east.getUvSize(), textureWidth, textureHeight, cubeIn.getMirror(), Direction.EAST, rE);
            quadNorth = north == null ? null : new GeoQuad(new GeoVertex[]{P3, P7, P5, P1}, north.getUv(), north.getUvSize(), textureWidth, textureHeight, cubeIn.getMirror(), Direction.NORTH, rN);
            quadSouth = south == null ? null : new GeoQuad(new GeoVertex[]{P8, P4, P2, P6}, south.getUv(), south.getUvSize(), textureWidth, textureHeight, cubeIn.getMirror(), Direction.SOUTH, rS);
            quadUp = up == null ? null : new GeoQuad(new GeoVertex[]{P4, P8, P7, P3}, up.getUv(), up.getUvSize(), textureWidth, textureHeight, cubeIn.getMirror(), Direction.UP, rU);
            quadDown = down == null ? null : new GeoQuad(new GeoVertex[]{P1, P5, P6, P2}, down.getUv(), down.getUvSize(), textureWidth, textureHeight, cubeIn.getMirror(), Direction.DOWN, rD);

            if (Boolean.TRUE.equals(cubeIn.getMirror()) || Boolean.TRUE.equals(mirror)) {
                quadWest = west == null ? null : new GeoQuad(new GeoVertex[]{P7, P8, P6, P5}, west.getUv(), west.getUvSize(), textureWidth, textureHeight, cubeIn.getMirror(), Direction.WEST, rW);
                quadEast = east == null ? null : new GeoQuad(new GeoVertex[]{P4, P3, P1, P2}, east.getUv(), east.getUvSize(), textureWidth, textureHeight, cubeIn.getMirror(), Direction.EAST, rE);
                quadUp = up == null ? null : new GeoQuad(new GeoVertex[]{P1, P5, P6, P2}, up.getUv(), up.getUvSize(), textureWidth, textureHeight, cubeIn.getMirror(), Direction.UP, rU);
                quadDown = down == null ? null : new GeoQuad(new GeoVertex[]{P4, P8, P7, P3}, down.getUv(), down.getUvSize(), textureWidth, textureHeight, cubeIn.getMirror(), Direction.DOWN, rD);
            }
        } else {
            double[] UV = cubeIn.getUv().boxUVCoords;
            Vector3d UVSize = VectorUtils.fromArray(cubeIn.getSize());
            UVSize = new Vector3d(Math.floor(UVSize.x), Math.floor(UVSize.y), Math.floor(UVSize.z));
            quadWest = new GeoQuad(new GeoVertex[]{P4, P3, P1, P2}, new double[]{UV[0] + UVSize.z + UVSize.x, UV[1] + UVSize.z}, new double[]{UVSize.z, UVSize.y}, textureWidth, textureHeight, cubeIn.getMirror(), Direction.WEST);
            quadEast = new GeoQuad(new GeoVertex[]{P7, P8, P6, P5}, new double[]{UV[0], UV[1] + UVSize.z}, new double[]{UVSize.z, UVSize.y}, textureWidth, textureHeight, cubeIn.getMirror(), Direction.EAST);
            quadNorth = new GeoQuad(new GeoVertex[]{P3, P7, P5, P1}, new double[]{UV[0] + UVSize.z, UV[1] + UVSize.z}, new double[]{UVSize.x, UVSize.y}, textureWidth, textureHeight, cubeIn.getMirror(), Direction.NORTH);
            quadSouth = new GeoQuad(new GeoVertex[]{P8, P4, P2, P6}, new double[]{UV[0] + UVSize.z + UVSize.x + UVSize.z, UV[1] + UVSize.z}, new double[]{UVSize.x, UVSize.y}, textureWidth, textureHeight, cubeIn.getMirror(), Direction.SOUTH);
            quadUp = new GeoQuad(new GeoVertex[]{P4, P8, P7, P3}, new double[]{UV[0] + UVSize.z, UV[1]}, new double[]{UVSize.x, UVSize.z}, textureWidth, textureHeight, cubeIn.getMirror(), Direction.UP);
            quadDown = new GeoQuad(new GeoVertex[]{P2, P6, P5, P1}, new double[]{UV[0] + UVSize.z + UVSize.x, UV[1]}, new double[]{UVSize.x, UVSize.z}, textureWidth, textureHeight, cubeIn.getMirror(), Direction.DOWN);
        }

        cube.quads[0] = quadWest;  cube.quads[1] = quadEast;
        cube.quads[2] = quadNorth; cube.quads[3] = quadSouth;
        cube.quads[4] = quadUp;    cube.quads[5] = quadDown;
        return cube;
    }
}