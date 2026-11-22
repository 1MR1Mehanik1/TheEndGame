package gcapi_model_animation;

import gcapi_model_animation.api.ModelBinder;
import gcapi_model_animation.api.loader.ResourceCache;
import the_end_game.init.InitBlock;
import the_end_game.init.InitItem;

public class GCApiInitClient {
    public static void init() {
        ResourceCache.clear();
        ModelBinder.bindAll(InitItem.getItems());
        ModelBinder.bindAll(InitBlock.getBlocks());
    }
}
