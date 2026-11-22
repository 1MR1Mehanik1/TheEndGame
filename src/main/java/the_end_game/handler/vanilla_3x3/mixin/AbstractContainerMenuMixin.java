package the_end_game.handler.vanilla_3x3.mixin;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.CraftingMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import the_end_game.handler.vanilla_3x3.CraftBlockState;

@Mixin(AbstractContainerMenu.class)
public abstract class AbstractContainerMenuMixin {
    @Inject(method = "broadcastChanges", at = @At("HEAD")) private void updateCraftBlock(CallbackInfo ci) {
        if ((Object)this instanceof CraftBlockState state) { state.updateBlocked(); }
    }
    @Inject(method = "clicked", at = @At("HEAD"), cancellable = true) private void blockResultClone(int slotId, int button, ClickType clickType, Player player, CallbackInfo ci) {
        if (clickType == ClickType.CLONE && slotId == CraftingMenu.RESULT_SLOT && (Object)this instanceof CraftBlockState state && state.isBlocked()) { ci.cancel(); }
    }
}
