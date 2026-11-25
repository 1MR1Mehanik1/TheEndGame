package the_end_game.init.block.table_theories;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import gcapi_model_animation.api.ModelBinder;
import gcapi_model_animation.api.geo.render.built.GeoBone;
import gcapi_model_animation.api.geo.render.built.GeoModel;
import gcapi_model_animation.api.render.GeoBlockRenderer;
import gcapi_model_animation.api.render.GeometryRenderer;
import gcapi_model_animation.init.basic.GCmaBlock;
import gcapi_model_animation.init.basic.GCmaBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import the_end_game.Main;
import the_end_game.init.block.table.Table;
import the_end_game.init.block.table.TableEntity;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public class TableTheoriesRenderer extends GeoBlockRenderer {
    @Nullable private GeoBone paperBone;
    @Nullable private GeoBone inkwellBone;
    @Nullable private GeoBone paperResearchesBone;

    public TableTheoriesRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override protected void preRender(GeoModel model, GCmaBlockEntity blockEntity, float partialTick, PoseStack poseStack) {
        GeoBone paper = model.getBone("paper").orElse(null);
        GeoBone inkwell = model.getBone("inkwell").orElse(null);
        GeoBone paperResearches = model.getBone("paper_researches").orElse(null);
        if (paper != null) {
            paper.isHidden = true;
            paper.hideChildBonesToo = true;
        }
        if (inkwell != null) {
            inkwell.isHidden = true;
            inkwell.hideChildBonesToo = true;
        }
        if (paperResearches != null) {
            paperResearches.isHidden = true;
            paperResearches.hideChildBonesToo = true;
        }
        this.paperBone = paper;
        this.inkwellBone = inkwell;
        this.paperResearchesBone = paperResearches;

//        GeoBone stopper = model.getBone("stopper").orElse(null);
//        if (stopper != null) {
//            stopper.isHidden = true;
//            stopper.hideChildBonesToo = true;
//        }
//        GeoBone ink = model.getBone("ink").orElse(null);
//        if (ink != null) {
//            ink.isHidden = true;
//            ink.hideChildBonesToo = true;
//        }
//
//        GeoBone shelf = model.getBone("shelf").orElse(null);
//        if (shelf != null) {
//            shelf.isHidden = true;
//            shelf.hideChildBonesToo = true;
//        }
    }

    @Override public void render(GCmaBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        this.paperBone = null;
        this.inkwellBone = null;
        this.paperResearchesBone = null;
        super.render(blockEntity, partialTick, poseStack, buffer, packedLight, packedOverlay);

        if (!(blockEntity instanceof TableTheoriesEntity tableTheories)) return;

        Block block = blockEntity.getBlockState().getBlock();
        ResourceLocation texture = ModelBinder.getTexture(BuiltInRegistries.BLOCK.getKey(block));
        if (texture == null && block instanceof GCmaBlock modeledBlock) { texture = modeledBlock.getTextureLocation(); }
        if (texture == null) return;

        if (this.paperBone != null) {
            VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutoutNoCull(texture));
            this.paperBone.isHidden = false;
            this.paperBone.hideChildBonesToo = false;
            poseStack.pushPose();
            poseStack.translate(0.5D, 0.001D, 0.5D);
            GeometryRenderer.renderBone(this.paperBone, consumer, poseStack, packedLight, packedOverlay);
            poseStack.popPose();
            this.paperBone.isHidden = true;
            this.paperBone.hideChildBonesToo = true;
        }
        if (this.paperResearchesBone != null) {
            VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutoutNoCull(texture));
            this.paperResearchesBone.isHidden = false;
            this.paperResearchesBone.hideChildBonesToo = false;
            poseStack.pushPose();
            poseStack.translate(0.5D, 0.001D, 0.5D);
            GeometryRenderer.renderBone(this.paperResearchesBone, consumer, poseStack, packedLight, packedOverlay);
            poseStack.popPose();
            this.paperResearchesBone.isHidden = true;
            this.paperResearchesBone.hideChildBonesToo = true;
        }
        if (this.inkwellBone != null) {
            VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutoutNoCull(texture));
            this.inkwellBone.isHidden = false;
            this.inkwellBone.hideChildBonesToo = false;
            poseStack.pushPose();
            poseStack.translate(0.5D, 0.001D, 0.5D);
            GeometryRenderer.renderBone(this.inkwellBone, consumer, poseStack, packedLight, packedOverlay);
            poseStack.popPose();
            this.inkwellBone.isHidden = true;
            this.inkwellBone.hideChildBonesToo = true;
        }
    }
}
