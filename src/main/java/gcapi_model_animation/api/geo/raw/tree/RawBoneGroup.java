package gcapi_model_animation.api.geo.raw.tree;

import gcapi_model_animation.api.geo.raw.pojo.Bone;
import java.util.HashMap;

public class RawBoneGroup {
	public HashMap<String, RawBoneGroup> children = new HashMap<>();
	public Bone selfBone;

	public RawBoneGroup(Bone bone)
	{
		this.selfBone = bone;
	}
}
