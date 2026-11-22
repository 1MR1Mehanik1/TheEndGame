package gcapi_model_animation.api.geo.render.built;

import java.util.ArrayList;
import java.util.List;

public class GeoBone {
    public GeoBone parent;
    public List<GeoBone> childBones = new ArrayList<>();
    public List<GeoCube> childCubes = new ArrayList<>();
    public String name;

    public Boolean mirror;
    public Double inflate;
    public Boolean dontRender;
    public boolean isHidden;
    public boolean areCubesHidden = false;
    public boolean hideChildBonesToo;
    public Boolean reset;

    public float rotationPointX;
    public float rotationPointY;
    public float rotationPointZ;

    private float rotateX;
    private float rotateY;
    private float rotateZ;

    private float scaleX = 1;
    private float scaleY = 1;
    private float scaleZ = 1;

    private float positionX;
    private float positionY;
    private float positionZ;

    // Геттеры/сеттеры как в GeckoLib GeoBone
    public float getRotationX() { return rotateX; }
    public float getRotationY() { return rotateY; }
    public float getRotationZ() { return rotateZ; }
    public void setRotationX(float v) { this.rotateX = v; }
    public void setRotationY(float v) { this.rotateY = v; }
    public void setRotationZ(float v) { this.rotateZ = v; }

    public float getPositionX() { return positionX; }
    public float getPositionY() { return positionY; }
    public float getPositionZ() { return positionZ; }
    public void setPositionX(float v) { this.positionX = v; }
    public void setPositionY(float v) { this.positionY = v; }
    public void setPositionZ(float v) { this.positionZ = v; }

    public float getScaleX() { return scaleX; }
    public float getScaleY() { return scaleY; }
    public float getScaleZ() { return scaleZ; }
    public void setScaleX(float v) { this.scaleX = v; }
    public void setScaleY(float v) { this.scaleY = v; }
    public void setScaleZ(float v) { this.scaleZ = v; }

    public void setPivotX(float v) { this.rotationPointX = v; }
    public void setPivotY(float v) { this.rotationPointY = v; }
    public void setPivotZ(float v) { this.rotationPointZ = v; }
    public float getPivotX() { return rotationPointX; }
    public float getPivotY() { return rotationPointY; }
    public float getPivotZ() { return rotationPointZ; }

    public GeoBone copy() {
        GeoBone copy = new GeoBone();
        copy.name = this.name;
        copy.rotationPointX = this.rotationPointX;
        copy.rotationPointY = this.rotationPointY;
        copy.rotationPointZ = this.rotationPointZ;
        copy.setRotationX(this.getRotationX());
        copy.setRotationY(this.getRotationY());
        copy.setRotationZ(this.getRotationZ());
        copy.setPositionX(this.getPositionX());
        copy.setPositionY(this.getPositionY());
        copy.setPositionZ(this.getPositionZ());
        copy.setScaleX(this.getScaleX());
        copy.setScaleY(this.getScaleY());
        copy.setScaleZ(this.getScaleZ());
        copy.mirror = this.mirror;
        copy.inflate = this.inflate;
        copy.dontRender = this.dontRender;
        copy.isHidden = this.isHidden;
        copy.areCubesHidden = this.areCubesHidden;
        copy.hideChildBonesToo = this.hideChildBonesToo;
        copy.reset = this.reset;
        // Кубы общие
        copy.childCubes = new ArrayList<>(this.childCubes);
        // Дети рекурсивно
        copy.childBones = new ArrayList<>();
        for (GeoBone child : this.childBones) {
            GeoBone childCopy = child.copy();
            childCopy.parent = copy;
            copy.childBones.add(childCopy);
        }
        return copy;
    }
}