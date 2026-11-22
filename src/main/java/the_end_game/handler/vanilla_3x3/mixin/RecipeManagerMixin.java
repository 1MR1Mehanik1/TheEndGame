package the_end_game.handler.vanilla_3x3.mixin;

import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import the_end_game.handler.vanilla_3x3.BelowData;
import the_end_game.handler.vanilla_3x3.CraftingContext;

import java.util.List;
import java.util.Optional;

@Mixin(RecipeManager.class)
public abstract class RecipeManagerMixin {
    @Inject(
            method = "getRecipeFor(Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/item/crafting/RecipeInput;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/crafting/RecipeHolder;)Ljava/util/Optional;",
            at = @At("RETURN"),
            cancellable = true
    )
    private void gateRecipe(RecipeType<?> recipeType, RecipeInput input, Level level, RecipeHolder<?> lastRecipe, CallbackInfoReturnable<Optional<RecipeHolder<?>>> cir) {
        if (level.isClientSide || CraftingContext.active()) { return; }
        Optional<RecipeHolder<?>> result = cir.getReturnValue();
        if (result == null || result.isEmpty()) { return; }
        if (BelowData.requirement(result.get()) != null) { cir.setReturnValue(Optional.empty()); }
    }

    @Inject(
            method = "getRecipesFor(Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/item/crafting/RecipeInput;Lnet/minecraft/world/level/Level;)Ljava/util/List;",
            at = @At("RETURN"),
            cancellable = true
    )
    private void gateRecipes(RecipeType<?> recipeType, RecipeInput input, Level level, CallbackInfoReturnable<List<RecipeHolder<?>>> cir) {
        if (level.isClientSide || CraftingContext.active()) { return; }
        List<RecipeHolder<?>> result = cir.getReturnValue();
        if (result == null || result.isEmpty()) { return; }
        cir.setReturnValue(result.stream().filter(holder -> BelowData.requirement(holder) == null).toList());
    }
}
