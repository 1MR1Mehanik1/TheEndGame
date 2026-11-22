package the_end_game.handler.vanilla_3x3.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import the_end_game.handler.vanilla_3x3.BelowData;
import the_end_game.handler.vanilla_3x3.CraftBlockState;
import the_end_game.handler.vanilla_3x3.CraftBlockedPayload;
import the_end_game.handler.vanilla_3x3.CraftingContext;

@Mixin(CraftingMenu.class)
public abstract class CraftingMenuMixin implements CraftBlockState {
    @Shadow @Final private ResultContainer resultSlots;
    @Shadow @Final private Player player;
    @Unique private BlockPos tablePos;
    @Unique private Level tableLevel;
    @Unique private boolean blocked;
    @Unique private Component missing;
    @Unique private Component info;
    @Unique private String lastMessage;
    @Unique private String lastInfo;

    @Inject(method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/inventory/ContainerLevelAccess;)V", at = @At("RETURN"))
    private void captureTable(int id, Inventory inventory, ContainerLevelAccess access, CallbackInfo ci) {
        this.lastMessage = "";
        this.lastInfo = "";
        access.execute((level, pos) -> {
            this.tablePos = pos.immutable();
            this.tableLevel = level;
        });
    }

    @Inject(method = "slotChangedCraftingGrid", at = @At("HEAD"))
    private static void enterCraftingContext(AbstractContainerMenu menu, Level level, Player player, CraftingContainer craftSlots, ResultContainer resultSlots, RecipeHolder<CraftingRecipe> recipe, CallbackInfo ci) {
        if (!level.isClientSide && menu instanceof CraftBlockState state && state.hasTable()) {
            CraftingContext.enter();
        }
    }
    @Inject(method = "slotChangedCraftingGrid", at = @At("RETURN"))
    private static void exitCraftingContext(AbstractContainerMenu menu, Level level, Player player, CraftingContainer craftSlots, ResultContainer resultSlots, RecipeHolder<CraftingRecipe> recipe, CallbackInfo ci) {
        if (!level.isClientSide && menu instanceof CraftBlockState state && state.hasTable()) {
            CraftingContext.exit();
        }
    }

    @Inject(method = "slotsChanged", at = @At("RETURN")) private void updateOnSlotsChanged(Container container, CallbackInfo ci) { this.updateBlocked(); }

    @Inject(method = "quickMoveStack", at = @At("HEAD"), cancellable = true) private void blockQuickMove(Player player, int index, CallbackInfoReturnable<ItemStack> cir) {
        if (index == CraftingMenu.RESULT_SLOT && this.blocked) { cir.setReturnValue(ItemStack.EMPTY); }
    }

    @Override public boolean isBlocked() { return this.blocked; }
    @Override public boolean hasTable() { return this.tablePos != null && this.tableLevel != null; }
    @Override public Component missingBlock() { return this.missing; }
    @Override public Component info() { return this.info; }

    @Override public void setBlocked(boolean blocked, Component missing, Component info) {
        this.blocked = blocked;
        this.missing = missing;
        this.info = info;
    }

    @Override public void updateBlocked() {
        if (!(this.player instanceof ServerPlayer serverPlayer) || this.tableLevel == null || this.tablePos == null) { return; }
        RecipeHolder<?> recipe = this.resultSlots.getRecipeUsed();
        BelowData.Requirement requirement = BelowData.requirement(recipe);
        boolean nextBlocked = false;
        Component nextMissing = null;
        Component nextInfo = null;
        if (requirement != null && !this.resultSlots.getItem(0).isEmpty()) {
            nextBlocked = !BelowData.matches(this.tableLevel, this.tablePos, requirement);
            if (nextBlocked) {
                nextMissing = BelowData.missing(requirement);
                nextInfo = requirement.info();
            }
        }
        String message = "";
        String infoText = "";
        if (nextMissing != null) {
            try { message = Component.Serializer.toJson(nextMissing, serverPlayer.level().registryAccess()); }
            catch (Exception exception) { message = ""; }
        }
        if (nextInfo != null) {
            try { infoText = Component.Serializer.toJson(nextInfo, serverPlayer.level().registryAccess()); }
            catch (Exception exception) { infoText = ""; }
        }
        if (nextBlocked == this.blocked && message.equals(this.lastMessage) && infoText.equals(this.lastInfo)) { return; }
        this.blocked = nextBlocked;
        this.missing = nextMissing;
        this.info = nextInfo;
        this.lastMessage = message;
        this.lastInfo = infoText;
        PacketDistributor.sendToPlayer(serverPlayer, new CraftBlockedPayload(((AbstractContainerMenu)(Object)this).containerId, nextBlocked, message, infoText));
    }
}
