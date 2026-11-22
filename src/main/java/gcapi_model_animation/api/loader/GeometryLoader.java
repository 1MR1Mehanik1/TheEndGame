package gcapi_model_animation.api.loader;

import gcapi_model_animation.api.geo.raw.pojo.Converter;
import gcapi_model_animation.api.geo.raw.pojo.RawGeoModel;
import gcapi_model_animation.api.geo.raw.tree.RawGeometryTree;
import gcapi_model_animation.api.geo.render.GeoModelBuilder;
import gcapi_model_animation.api.geo.render.built.GeoModel;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;

import java.io.InputStream;
import java.util.Optional;
import java.util.Scanner;

public class GeometryLoader {
    public static GeoModel load(ResourceLocation location) {
        try {
            Optional<Resource> res = Minecraft.getInstance().getResourceManager().getResource(location);
            if (res.isPresent()) { try (InputStream in = res.get().open()) { return loadFromStream(in); } }
        } catch (Exception ignored) { }

        // fallback — classpath
        try (InputStream stream = GeometryLoader.class.getResourceAsStream("/assets/" + location.getNamespace() + "/" + location.getPath())) {
            if (stream != null) return loadFromStream(stream);
        } catch (Exception ex) { ex.printStackTrace(); }
        return null;
    }

    private static GeoModel loadFromStream(InputStream stream) {
        try {
            String json = readStringFromStream(stream);
            RawGeoModel rawModel = Converter.fromJsonString(json);
            RawGeometryTree tree = RawGeometryTree.parseHierarchy(rawModel, ResourceLocation.fromNamespaceAndPath("dummy", "dummy"));
            return GeoModelBuilder.constructGeoModel(tree);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String readStringFromStream(InputStream stream) {
        Scanner s = new Scanner(stream, "UTF-8").useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}