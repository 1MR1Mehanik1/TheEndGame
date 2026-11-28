package the_end_game.init.block.table_theories;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import gcapi_model_animation.api.geo.render.built.GeoBone;
import gcapi_model_animation.api.geo.render.built.GeoModel;
import gcapi_model_animation.api.render.GeoBlockRenderer;
import gcapi_model_animation.init.basic.GCmaBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import the_end_game.init.InitItem;

public class TableTheoriesRenderer extends GeoBlockRenderer {
    private static final int DISPLAY_SLOT_START = 4;
    private static final int DISPLAY_COLUMNS = 4;
    private static final int DISPLAY_ROWS = 2;
    private static final float DISPLAY_X = -5;
    private static final float DISPLAY_Z = 6F;
    private static final float DISPLAY_STEP_X = 3.5F;
    private static final float DISPLAY_STEP_Y = 4.0F;
    private static final float DISPLAY_Y = 16.75F;
    private static final float DISPLAY_SCALE = 0.45F;
    private static final float DISPLAY_YAW = 180;
    private static final ItemDisplayContext DISPLAY_CONTEXT = ItemDisplayContext.GROUND;

    public TableTheoriesRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override protected void preRender(GeoModel model, GCmaBlockEntity blockEntity, float partialTick, PoseStack poseStack) {
        boolean paper = false;
        int paperCount = 0;
        boolean inkwell = false;
        boolean feather = false;
        boolean theory = false;
        if (blockEntity instanceof TableTheoriesEntity table) {
            for (int slot = 0; slot < TableTheoriesEntity.SLOTS; slot++) {
                ItemStack stack = table.getItem(slot);
                if (slot == 0 && stack.is(Items.PAPER)) {
                    paper = true;
                    paperCount += stack.getCount();
                }
                if (slot == 1 && stack.is(InitItem.INKWELL.get())) inkwell = true;
                if (slot == 2 && stack.is(InitItem.WRITING_FEATHER.get())) feather = true;
                if (slot == 3 && stack.is(InitItem.THEORY.get())) theory = true;
            }
        }

        setBone(model, "paper", paper);
        setBone(model, "leaf_1", paperCount >= 1);
        setBone(model, "leaf_4", paperCount >= 4);
        setBone(model, "leaf_8", paperCount >= 8);
        setBone(model, "leaf_16", paperCount >= 16);
        setBone(model, "paper_researches", theory);
        setBone(model, "inkwell", inkwell);
        setBone(model, "ink", inkwell);
        setBone(model, "stopper", inkwell && !feather);
        setBone(model, "featherontable", feather && !inkwell);
        setBone(model, "featherininkwell", inkwell && feather);
        if (blockEntity instanceof TableTheoriesEntity table) applyPoses(model, table);
    }

    @Override public void render(GCmaBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        Direction facing = blockEntity.getBlockState().getValue(TableTheories.FACING);
        poseStack.pushPose();
        poseStack.translate(0.5D, 0.0D, 0.5D);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - facing.toYRot()));
        poseStack.translate(-0.5D, 0.0D, -0.5D);
        super.render(blockEntity, partialTick, poseStack, buffer, packedLight, packedOverlay);
        if (blockEntity instanceof TableTheoriesEntity table) {
            Level level = table.getLevel();
            if (level == null) {
                poseStack.popPose();
                return;
            }

            for (int index = 0; index < DISPLAY_COLUMNS * DISPLAY_ROWS; index++) {
                ItemStack stack = table.getItem(DISPLAY_SLOT_START + index);
                if (stack.isEmpty()) continue;
                int column = index % DISPLAY_COLUMNS;
                int row = index / DISPLAY_COLUMNS;
                int slotColumn = DISPLAY_COLUMNS - 1 - column;
                poseStack.pushPose();
                poseStack.translate(0.5D + (DISPLAY_X + slotColumn * DISPLAY_STEP_X) / 16.0D, (DISPLAY_Y + (DISPLAY_ROWS - 1 - row) * DISPLAY_STEP_Y) / 16.0D, 0.5D + DISPLAY_Z / 16.0D);
                poseStack.mulPose(Axis.YP.rotationDegrees(DISPLAY_YAW));
                poseStack.scale(DISPLAY_SCALE, DISPLAY_SCALE, DISPLAY_SCALE);
                Minecraft.getInstance().getItemRenderer().renderStatic(stack, DISPLAY_CONTEXT, packedLight, packedOverlay, poseStack, buffer, level, index);
                poseStack.popPose();
            }
        }
        poseStack.popPose();
    }

    private static void applyPoses(GeoModel model, TableTheoriesEntity table) {
        GeoBone inkwell = model.getBone("inkwell").orElse(null);
        if (inkwell != null) {
            inkwell.setPositionX(table.inkwellX() - 8.5F);
            inkwell.setPositionZ(table.inkwellZ() - 3.0F);
            inkwell.setRotationY((float) Math.toRadians(table.inkwellYaw()));
        }
        GeoBone featherOnTable = model.getBone("featherontable").orElse(null);
        if (featherOnTable != null) {
            float featherX = Mth.clamp(Math.abs(table.featherX()), 8.5F, 11.5F);
            float featherZ = Mth.clamp(table.featherZ(), -6.0F, 3.0F);
            featherOnTable.setPositionX(10.0F - featherX);
            featherOnTable.setPositionZ(featherZ - 3.25F);
            featherOnTable.setRotationY((float) Math.toRadians(table.featherYaw()));
        }
        GeoBone featherInInkwell = model.getBone("featherininkwell").orElse(null);
        if (featherInInkwell != null) {
            featherInInkwell.setPositionX(0.0F);
            featherInInkwell.setPositionY(0.0F);
            featherInInkwell.setPositionZ(0.0F);
            featherInInkwell.setRotationY((float) Math.toRadians(table.featherInkwellYaw()));
        }
    }

    private static void setBone(GeoModel model, String name, boolean visible) {
        GeoBone bone = model.getBone(name).orElse(null);
        if (bone == null) return;
        bone.isHidden = !visible;
        bone.hideChildBonesToo = !visible;
    }
}
