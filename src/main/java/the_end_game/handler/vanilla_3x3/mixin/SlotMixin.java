package the_end_game.handler.vanilla_3x3.mixin;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import the_end_game.handler.vanilla_3x3.CraftBlockState;

@Mixin(Slot.class)
public abstract class SlotMixin {
    @Inject(method = "mayPickup", at = @At("HEAD"), cancellable = true) private void blockResultPickup(Player player, CallbackInfoReturnable<Boolean> cir) {
        if ((Object)this instanceof ResultSlot && player.containerMenu instanceof CraftBlockState state && state.isBlocked()) { cir.setReturnValue(false); }
    }
}
