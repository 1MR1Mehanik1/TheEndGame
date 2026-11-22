package gcapi_model_animation.api.loader;

import gcapi_model_animation.api.geo.render.built.GeoModel;
import gcapi_model_animation.api.geometry.ModelDisplay;
import gcapi_model_animation.api.animation.AnimationData;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class ResourceCache {
    private static final Map<ResourceLocation, GeoModel> geometryCache = new HashMap<>();
    private static final Map<ResourceLocation, ModelDisplay> displayCache = new HashMap<>();
    private static final Map<ResourceLocation, Map<String, AnimationData>> animationCache = new HashMap<>();

    public static GeoModel getGeometry(ResourceLocation loc) { return geometryCache.computeIfAbsent(loc, GeometryLoader::load); }
    public static ModelDisplay getDisplay(ResourceLocation loc) { return displayCache.computeIfAbsent(loc, ModelDisplayLoader::load); }
    public static Map<String, AnimationData> getAnimations(ResourceLocation loc) { return animationCache.computeIfAbsent(loc, AnimationLoader::load); }

    /** Очистка кэша (при перезагрузке ресурсов) */
    public static void clear() {
        geometryCache.clear();
        displayCache.clear();
        animationCache.clear();
    }

    /** Инициализация на клиенте (можно вызвать принудительную загрузку) */
    public static void initClient() {} // Можно предварительно прогреть кэш, но оставим пустым
}