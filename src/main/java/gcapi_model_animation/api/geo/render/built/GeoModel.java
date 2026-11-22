package gcapi_model_animation.api.geo.render.built;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GeoModel {
    public List<GeoBone> topLevelBones = new ArrayList<>();
    public int textureWidth;
    public int textureHeight;

    public Optional<GeoBone> getBone(String name) {
        for (GeoBone bone : topLevelBones) {
            GeoBone found = getBoneRecursively(name, bone);
            if (found != null) return Optional.of(found);
        }
        return Optional.empty();
    }

    public GeoModel makeCopy() {
        GeoModel copy = new GeoModel();
        copy.textureWidth = this.textureWidth;
        copy.textureHeight = this.textureHeight;
        for (GeoBone bone : topLevelBones) { copy.topLevelBones.add(bone.copy()); }
        return copy;
    }

    private GeoBone getBoneRecursively(String name, GeoBone bone) {
        if (bone.name.equals(name)) return bone;
        for (GeoBone child : bone.childBones) {
            if (child.name.equals(name)) return child;
            GeoBone found = getBoneRecursively(name, child);
            if (found != null) return found;
        }
        return null;
    }
}