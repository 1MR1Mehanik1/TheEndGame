package gcapi_model_animation.api.loader;

import gcapi_model_animation.api.geometry.ModelDisplay;
import net.minecraft.resources.ResourceLocation;

public class ModelDisplayLoader {
    public static ModelDisplay load(ResourceLocation location) {
        return ModelDisplay.load(location);
    }
}