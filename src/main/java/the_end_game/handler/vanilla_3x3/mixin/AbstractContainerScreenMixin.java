package the_end_game.handler.vanilla_3x3.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import the_end_game.handler.vanilla_3x3.CraftBlockState;

import java.util.ArrayList;
import java.util.List;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin {
    @Shadow protected Slot hoveredSlot;
    @Shadow protected AbstractContainerMenu menu;

    @Inject(method = "renderTooltip", at = @At("HEAD"), cancellable = true) private void showMissingBlock(GuiGraphics graphics, int x, int y, CallbackInfo ci) {
        if (this.hoveredSlot == null || this.hoveredSlot.index != CraftingMenu.RESULT_SLOT) { return; }
        if (!(this.menu instanceof CraftBlockState state) || !state.isBlocked() || state.missingBlock() == null) { return; }

        Component line = state.info() != null ? state.info() : state.missingBlock();
        if (line == null) { return; }

        graphics.renderComponentTooltip(Minecraft.getInstance().font, List.of(line), x, y);
        ci.cancel();
    }
}
