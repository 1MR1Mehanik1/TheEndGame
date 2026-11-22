package gcapi_model_animation.util;

import gcapi_model_animation.api.geo.render.built.GeoBone;
import gcapi_model_animation.api.geo.render.built.GeoCube;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.Stack;

public class MatrixStack {
	private final Stack<Matrix4f> model  = new Stack<>();
	private final Stack<Matrix3f> normal = new Stack<>();
	private final Matrix4f tempModelMatrix = new Matrix4f();
	private final Matrix3f tempNormalMatrix = new Matrix3f();

	public MatrixStack() {
		model.push(new Matrix4f());
		normal.push(new Matrix3f());
	}

	public Matrix4f getModelMatrix()  { return model.peek(); }
	public Matrix3f getNormalMatrix() { return normal.peek(); }

	public void push() { model.push(new Matrix4f(model.peek())); normal.push(new Matrix3f(normal.peek())); }
	public void pop() {
		if (model.size() == 1) throw new IllegalStateException("A one level stack can't be popped!");
		model.pop(); normal.pop();
	}

	public void translate(float x, float y, float z) { translate(new Vector3f(x, y, z)); }
	public void translate(Vector3f v) {
		tempModelMatrix.identity().setTranslation(v);
		model.peek().mul(tempModelMatrix);
	}

	public void moveToPivot(GeoCube cube) {
		translate(cube.pivot.x / 16f, cube.pivot.y / 16f, cube.pivot.z / 16f);
	}
	public void moveBackFromPivot(GeoCube cube) {
		translate(-cube.pivot.x / 16f, -cube.pivot.y / 16f, -cube.pivot.z / 16f);
	}
	public void moveToPivot(GeoBone bone) {
		translate(bone.rotationPointX / 16f, bone.rotationPointY / 16f, bone.rotationPointZ / 16f);
	}
	public void moveBackFromPivot(GeoBone bone) {
		translate(-bone.rotationPointX / 16f, -bone.rotationPointY / 16f, -bone.rotationPointZ / 16f);
	}
	public void translate(GeoBone bone) {
		translate(-bone.getPositionX() / 16f, bone.getPositionY() / 16f, bone.getPositionZ() / 16f);
	}

	public void scale(float x, float y, float z) {
		tempModelMatrix.identity().scale(x, y, z);
		model.peek().mul(tempModelMatrix);
		if (x < 0 || y < 0 || z < 0) {
			tempNormalMatrix.identity().scale(x < 0 ? -1 : 1, y < 0 ? -1 : 1, z < 0 ? -1 : 1);
			normal.peek().mul(tempNormalMatrix);
		}
	}
	public void scale(GeoBone bone) { scale(bone.getScaleX(), bone.getScaleY(), bone.getScaleZ()); }

	public void rotateX(float rad) { tempModelMatrix.identity().rotationX(rad); tempNormalMatrix.identity().rotationX(rad); model.peek().mul(tempModelMatrix); normal.peek().mul(tempNormalMatrix); }
	public void rotateY(float rad) { tempModelMatrix.identity().rotationY(rad); tempNormalMatrix.identity().rotationY(rad); model.peek().mul(tempModelMatrix); normal.peek().mul(tempNormalMatrix); }
	public void rotateZ(float rad) { tempModelMatrix.identity().rotationZ(rad); tempNormalMatrix.identity().rotationZ(rad); model.peek().mul(tempModelMatrix); normal.peek().mul(tempNormalMatrix); }

	public void rotate(GeoBone bone) {
		if (bone.getRotationZ() != 0f) rotateZ(bone.getRotationZ());
		if (bone.getRotationY() != 0f) rotateY(bone.getRotationY());
		if (bone.getRotationX() != 0f) rotateX(bone.getRotationX());
	}

	public void rotate(GeoCube cube) {
		Vector3f r = cube.rotation;
		tempModelMatrix.identity().rotationZ(r.z).rotateY(r.y).rotateX(r.x);
		tempNormalMatrix.identity().rotationZ(r.z).rotateY(r.y).rotateX(r.x);
		model.peek().mul(tempModelMatrix);
		normal.peek().mul(tempNormalMatrix);
	}
}
