package gcapi_model_animation.init.basic;

import gcapi_model_animation.api.IModeledObject;
import gcapi_model_animation.api.render.GeoItemStackRenderer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

import java.util.function.Consumer;

public class GCmaItem extends Item implements IModeledObject {
    private final ResourceLocation model;
    private final ResourceLocation texture;

    public GCmaItem(Properties props, ResourceLocation model, ResourceLocation texture) {
        super(props);
        this.model = model;
        this.texture = texture;
    }

    @Override public ResourceLocation getGeometryLocation() { return model; }
    @Override public ResourceLocation getTextureLocation() { return texture; }

    @Override public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override public BlockEntityWithoutLevelRenderer getCustomRenderer() { return GeoItemStackRenderer.INSTANCE; }
        });
    }
}
