package gcapi_model_animation.api;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.HashMap;
import java.util.Map;

public class ModelBinder {
    private static final Map<ResourceLocation, ResourceLocation> MODEL_MAP = new HashMap<>();
    private static final Map<ResourceLocation, ResourceLocation> TEXTURE_MAP = new HashMap<>();

    // --- Items ---
    public static void bind(Item item, ResourceLocation geoModel) { bind(BuiltInRegistries.ITEM.getKey(item), geoModel, null); }
    public static void bind(Item item, ResourceLocation geoModel, ResourceLocation texture) { bind(BuiltInRegistries.ITEM.getKey(item), geoModel, texture); }

    // --- Blocks ---
    public static void bind(Block block, ResourceLocation geoModel) { bind(BuiltInRegistries.BLOCK.getKey(block), geoModel, null); }
    public static void bind(Block block, ResourceLocation geoModel, ResourceLocation texture) { bind(BuiltInRegistries.BLOCK.getKey(block), geoModel, texture); }

    // --- Entities ---
    public static void bind(Class<? extends Entity> entityClass, ResourceLocation geoModel) { bind(entityClass, geoModel, null); }
    public static void bind(Class<? extends Entity> entityClass, ResourceLocation geoModel, ResourceLocation texture) {
        bindInternal(ResourceLocation.fromNamespaceAndPath("entity", entityClass.getName()), geoModel, texture);
    }

    public static void bind(String entityId, ResourceLocation geoModel) { bind(entityId, geoModel, null); }
    public static void bind(String entityId, ResourceLocation geoModel, ResourceLocation texture) {
        bindInternal(ResourceLocation.fromNamespaceAndPath("entity", entityId), geoModel, texture);
    }

    // --- Universal ---
    public static void bind(ResourceLocation key, ResourceLocation geoModel, ResourceLocation texture) { bindInternal(key, geoModel, texture); }

    // --- IModeledObject ---
    public static void bindAll(Iterable<?> objects) {
        for (Object obj : objects) {
            if (obj instanceof IModeledObject modelled) {
                ResourceLocation model = modelled.getGeometryLocation();
                if (model != null) {
                    ResourceLocation tex = modelled.getTextureLocation();
                    ResourceLocation key = getRegistryName(obj);
                    if (key != null) bindInternal(key, model, tex);
                }
            }
        }
    }

    // --- Getters ---
    public static ResourceLocation getModel(ResourceLocation key)   { return MODEL_MAP.get(key); }

    public static ResourceLocation getTexture(ResourceLocation key) {
        ResourceLocation tex = TEXTURE_MAP.get(key);
        if (tex == null) {
            ResourceLocation model = getModel(key);
            if (model != null) {
                String path = model.getPath();
                if (path.endsWith(".geo.json")) {
                    path = path.substring(0, path.length() - ".geo.json".length()) + ".png";
                }
                tex = ResourceLocation.fromNamespaceAndPath(model.getNamespace(), path);
            }
        }
        return tex;
    }

    public static ResourceLocation getModel(Class<? extends Entity> cls) {
        return MODEL_MAP.get(ResourceLocation.fromNamespaceAndPath("entity", cls.getName()));
    }
    public static ResourceLocation getTexture(Class<? extends Entity> cls) {
        return getTexture(ResourceLocation.fromNamespaceAndPath("entity", cls.getName()));
    }

    public static void clear() { MODEL_MAP.clear(); TEXTURE_MAP.clear(); }
    public static void init()  { }

    private static void bindInternal(ResourceLocation key, ResourceLocation geoModel, ResourceLocation texture) {
        MODEL_MAP.put(key, geoModel);
        if (texture != null) TEXTURE_MAP.put(key, texture);
        else TEXTURE_MAP.remove(key);
    }

    private static ResourceLocation getRegistryName(Object obj) {
        if (obj instanceof Item  item)  return BuiltInRegistries.ITEM.getKey(item);
        if (obj instanceof Block block) return BuiltInRegistries.BLOCK.getKey(block);
        return null;
    }
}