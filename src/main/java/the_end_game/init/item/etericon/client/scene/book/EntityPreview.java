package the_end_game.init.item.etericon.client.scene.book;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;

public final class EntityPreview {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Map<ResourceLocation, Entity> CACHE = new HashMap<>();

    public static void clear() { CACHE.clear(); }

    public static void draw(GuiGraphics graphics, ResourceLocation id, int x, int y, int width, int height, float partialTick, boolean rotate) {
        EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.getOptional(id).orElse(null);
        Level level = Minecraft.getInstance().level;
        if (type == null || level == null) {
            drawFallback(graphics, x, y, width, height);
            return;
        }
        Entity entity = CACHE.computeIfAbsent(id, key -> {
            Entity created = type.create(level);
            if (created instanceof Mob mob) { mob.setNoAi(true); }
            if (created != null) {
                created.setPos(0.0, 0.0, 0.0);
                created.setYRot(180.0F);
            }
            return created;
        });
        if (entity == null) {
            drawFallback(graphics, x, y, width, height);
            return;
        }
        try {
            EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
            float scale = (float) (height / Math.max(1.0, entity.getBbHeight())) * 0.82F;
            float yaw = rotate && level != null ? (level.getGameTime() + partialTick) * 2.5F : 180.0F;
            graphics.pose().pushPose();
            graphics.pose().translate(x + width / 2.0F, y + height / 2.0F, 50.0F);
            graphics.pose().scale(-scale, scale, scale);
            graphics.pose().translate(0.0F, -entity.getBbHeight() / 2.0F, 0.0F);
            dispatcher.render(entity, 0.0, 0.0, 0.0, yaw, partialTick, graphics.pose(), graphics.bufferSource(), 0xF000F0);
            graphics.pose().popPose();
            graphics.flush();
        } catch (Throwable throwable) {
            LOGGER.warn("Unable to render entity {} in Etericon", id, throwable);
            drawFallback(graphics, x, y, width, height);
        }
    }

    private static void drawFallback(GuiGraphics graphics, int x, int y, int width, int height) {
        graphics.renderItem(new ItemStack(Items.BARRIER), x + width / 2 - 8, y + height / 2 - 8);
        graphics.flush();
    }
}
