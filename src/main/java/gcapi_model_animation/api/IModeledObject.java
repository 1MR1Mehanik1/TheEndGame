package gcapi_model_animation.api;

import net.minecraft.resources.ResourceLocation;

public interface IModeledObject {
    ResourceLocation getGeometryLocation();
    ResourceLocation getTextureLocation();
}
