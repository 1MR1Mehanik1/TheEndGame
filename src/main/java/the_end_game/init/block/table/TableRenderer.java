package the_end_game.init.block.table;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import gcapi_model_animation.api.geo.render.built.GeoBone;
import gcapi_model_animation.api.geo.render.built.GeoModel;
import gcapi_model_animation.api.render.GeoBlockRenderer;
import gcapi_model_animation.api.render.GeometryRenderer;
import gcapi_model_animation.init.basic.GCmaBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import the_end_game.Main;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public class TableRenderer extends GeoBlockRenderer {
    private static final Map<BakedModel, Float> MODEL_MIN_Y = new IdentityHashMap<>();
    @Nullable private GeoBone clothBone;

    public TableRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override protected void preRender(GeoModel model, GCmaBlockEntity blockEntity, float partialTick, PoseStack poseStack) {
        GeoBone cloth = model.getBone("Cloth").orElse(null);
        if (cloth != null) {
            cloth.isHidden = true;
            cloth.hideChildBonesToo = true;
        }
        this.clothBone = cloth;
        if (blockEntity instanceof TableEntity table) applyConnections(model, table);
    }

    @Override public void render(GCmaBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        this.clothBone = null;
        super.render(blockEntity, partialTick, poseStack, buffer, packedLight, packedOverlay);

        if (!(blockEntity instanceof TableEntity table)) return;

        if (table.getClothColor() != null && this.clothBone != null) {
            ResourceLocation texture = clothTexture(table.getClothColor());
            VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutoutNoCull(texture));
            this.clothBone.isHidden = false;
            this.clothBone.hideChildBonesToo = false;
            poseStack.pushPose();
            poseStack.translate(0.5D, 0.001D, 0.5D);
            GeometryRenderer.renderBone(this.clothBone, consumer, poseStack, packedLight, packedOverlay);
            poseStack.popPose();
            this.clothBone.isHidden = true;
            this.clothBone.hideChildBonesToo = true;
        }

        renderItems(table, poseStack, buffer, packedLight, packedOverlay);
    }

    private static void renderItems(TableEntity table, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        Level level = table.getLevel();
        if (level == null) return;
        for (int i = 0; i < table.getEntryCount(); i++) {
            ItemStack stack = table.getEntryStack(i);
            if (stack.isEmpty()) continue;
            float scale = table.getEntryScale(i);
            float x = table.getVisualX(i);
            float z = table.getVisualZ(i);
            float yaw = table.getEntryYaw(i);
            BakedModel model = stack.getItem() instanceof BlockItem ? Minecraft.getInstance().getItemRenderer().getModel(stack, level, null, i) : null;
            boolean lying = shouldLie(table, i, stack, model);
            boolean custom = model != null && model.isCustomRenderer();
            poseStack.pushPose();
            if (stack.is(Items.SHIELD)) {
                poseStack.translate(x, 1.037D, z);
                poseStack.mulPose(Axis.YP.rotationDegrees(yaw));
                poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));
                poseStack.translate(0.25D, 0.25D, 0.25D);
                poseStack.scale(scale, scale, scale);
            } else if (stack.is(Items.TRIDENT)) {
                poseStack.translate(x, 1.037D, z);
                poseStack.mulPose(Axis.YP.rotationDegrees(yaw));
                poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));
                poseStack.translate(0.25D, 0.6D, 0.25D);
                poseStack.scale(scale, scale, scale);
            } else if (stack.is(Items.SPYGLASS)) {
                poseStack.translate(x, 1.037D, z);
                poseStack.mulPose(Axis.YP.rotationDegrees(yaw));
                poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));
                poseStack.scale(scale, scale, scale);
            } else if (lying) {
                poseStack.translate(x, 1.002D, z);
                poseStack.mulPose(Axis.YP.rotationDegrees(yaw));
                poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));
                poseStack.scale(scale, scale, scale);
            } else {
                double y;
                if (custom) { y = stack.is(Items.CONDUIT) ? 1.002D + (0.5D - blockMinY(stack, level, table.getBlockPos())) * scale : 1.002D + 0.5D * scale; }
                else { y = 1.002D + (0.5D - modelMinY(model)) * scale; }
                poseStack.translate(x, y, z);
                poseStack.mulPose(Axis.YP.rotationDegrees(yaw));
                if (table.isEntryBed(i)) poseStack.translate(0.0D, 0.0D, 0.15D);
                poseStack.scale(scale, scale, scale);
            }
            Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.NONE, packedLight, packedOverlay, poseStack, buffer, level, i);
            poseStack.popPose();
        }
    }

    private static void applyConnections(GeoModel model, TableEntity table) {
        Level level = table.getLevel();
        if (level == null) return;
        BlockPos pos = table.getBlockPos();
        boolean north = level.getBlockState(pos.north()).getBlock() instanceof Table;
        boolean south = level.getBlockState(pos.south()).getBlock() instanceof Table;
        boolean east = level.getBlockState(pos.east()).getBlock() instanceof Table;
        boolean west = level.getBlockState(pos.west()).getBlock() instanceof Table;
        setHidden(model, "leg_lb", east || south);
        setHidden(model, "leg_lf", east || north);
        setHidden(model, "leg_rb", west || south);
        setHidden(model, "leg_rf", west || north);
        setHidden(model, "e_cloth", east);
        setHidden(model, "w_cloth", west);
        setHidden(model, "n_cloth", north);
        setHidden(model, "s_cloth", south);
        GeoBone top = model.getBone("up_cloth").orElse(null);
        if (top != null) {
            top.setScaleX(east || west ? 16.0F / 16.2F : 1.0F);
            top.setScaleZ(north || south ? 16.0F / 16.2F : 1.0F);
        }
    }

    private static void setHidden(GeoModel model, String name, boolean hidden) { model.getBone(name).ifPresent(bone -> bone.isHidden = hidden); }

    private static boolean shouldLie(TableEntity table, int index, ItemStack stack, @Nullable BakedModel model) {
        if (table.isEntryBed(index)) return false;
        if (table.isEntryTyped(index)) return true;
        if (!(stack.getItem() instanceof BlockItem) || model == null) return true;
        return !model.isGui3d() && !model.isCustomRenderer();
    }

    private static float blockMinY(ItemStack stack, Level level, BlockPos pos) {
        if (stack.getItem() instanceof BlockItem blockItem) {
            BlockState state = blockItem.getBlock().defaultBlockState();
            VoxelShape shape = state.getShape(level, pos, CollisionContext.empty());
            if (!shape.isEmpty()) return (float) shape.min(Direction.Axis.Y);
        }
        return 0.0F;
    }

    private static float modelMinY(@Nullable BakedModel model) {
        if (model == null) return 0.0F;
        Float cached = MODEL_MIN_Y.get(model);
        if (cached != null) return cached;
        float min = Float.MAX_VALUE;
        RandomSource random = RandomSource.create(42L);
        min = scanQuads(model.getQuads(null, null, random), min);
        for (Direction direction : Direction.values()) { min = scanQuads(model.getQuads(null, direction, random), min); }
        if (min == Float.MAX_VALUE) min = 0.0F;
        MODEL_MIN_Y.put(model, min);
        return min;
    }

    private static float scanQuads(List<BakedQuad> quads, float min) {
        for (BakedQuad quad : quads) {
            int[] vertices = quad.getVertices();
            for (int i = 0; i < 4; i++) {
                float y = Float.intBitsToFloat(vertices[i * 8 + 1]);
                if (y < min) min = y;
            }
        }
        return min;
    }

    private static ResourceLocation clothTexture(DyeColor color) { return ResourceLocation.fromNamespaceAndPath(Main.MOD_ID, "textures/block/table/table_cloth/table_cloth_" + color.getName() + ".png"); }
}
