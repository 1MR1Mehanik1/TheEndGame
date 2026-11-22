package the_end_game.handler.vanilla_3x3.mixin;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import the_end_game.handler.vanilla_3x3.CraftBlockState;
import the_end_game.handler.vanilla_3x3.CraftingContext;

@Mixin(ResultSlot.class)
public abstract class ResultSlotMixin {
    @Shadow @Final private Player player;

    @Inject(method = "remove", at = @At("HEAD"), cancellable = true) private void blockRemove(int amount, CallbackInfoReturnable<ItemStack> cir) {
        if (this.player.containerMenu instanceof CraftBlockState state && state.isBlocked()) { cir.setReturnValue(ItemStack.EMPTY); }
    }
    @Inject(method = "onTake", at = @At("HEAD")) private void enterTakeContext(Player player, ItemStack stack, CallbackInfo ci) {
        if (player.containerMenu instanceof CraftBlockState state && state.hasTable()) { CraftingContext.enter(); }
    }
    @Inject(method = "onTake", at = @At("RETURN")) private void exitTakeContext(Player player, ItemStack stack, CallbackInfo ci) {
        if (player.containerMenu instanceof CraftBlockState state && state.hasTable()) { CraftingContext.exit(); }
    }
}
