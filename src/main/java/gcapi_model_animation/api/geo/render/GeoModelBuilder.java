package gcapi_model_animation.api.geo.render;

import org.joml.Vector3f;
import org.apache.commons.lang3.ArrayUtils;
import gcapi_model_animation.api.geo.raw.pojo.*;
import gcapi_model_animation.api.geo.raw.tree.RawBoneGroup;
import gcapi_model_animation.api.geo.raw.tree.RawGeometryTree;
import gcapi_model_animation.api.geo.render.built.GeoBone;
import gcapi_model_animation.api.geo.render.built.GeoCube;
import gcapi_model_animation.api.geo.render.built.GeoModel;
import gcapi_model_animation.util.VectorUtils;

public class GeoModelBuilder {
    public static GeoModel constructGeoModel(RawGeometryTree geometryTree) {
        GeoModel model = new GeoModel();
        Double tw = geometryTree.properties.getTextureWidth();
        Double th = geometryTree.properties.getTextureHeight();
        model.textureWidth = (tw != null) ? tw.intValue() : 64;
        model.textureHeight = (th != null) ? th.intValue() : 64;

        for (RawBoneGroup rawBone : geometryTree.topLevelBones.values()) { model.topLevelBones.add(constructBone(rawBone, geometryTree.properties, null)); }
        return model;
    }

    public static GeoBone constructBone(RawBoneGroup bone, ModelProperties properties, GeoBone parent) {
        GeoBone geoBone = new GeoBone();
        Bone rawBone = bone.selfBone;
        Vector3f rotation = VectorUtils.convertDoubleToFloat(VectorUtils.fromArray(rawBone.getRotation()));
        Vector3f pivot = VectorUtils.convertDoubleToFloat(VectorUtils.fromArray(rawBone.getPivot()));
        rotation.x *= -1;
        rotation.y *= -1;

        geoBone.mirror = rawBone.getMirror();
        geoBone.dontRender = rawBone.getNeverRender();
        geoBone.reset = rawBone.getReset();
        geoBone.inflate = rawBone.getInflate();
        geoBone.parent = parent;
        geoBone.name = rawBone.getName();

        geoBone.setRotationX((float) Math.toRadians(rotation.x));
        geoBone.setRotationY((float) Math.toRadians(rotation.y));
        geoBone.setRotationZ((float) Math.toRadians(rotation.z));

        geoBone.rotationPointX = -pivot.x;
        geoBone.rotationPointY =  pivot.y;
        geoBone.rotationPointZ =  pivot.z;

        if (!ArrayUtils.isEmpty(rawBone.getCubes())) {
            for (Cube cube : rawBone.getCubes()) {
                geoBone.childCubes.add(GeoCube.createFromPojoCube(cube, properties, geoBone.inflate == null ? null : geoBone.inflate / 16.0, geoBone.mirror));
            }
        }

        for (RawBoneGroup child : bone.children.values()) { geoBone.childBones.add(constructBone(child, properties, geoBone)); }
        return geoBone;
    }
}