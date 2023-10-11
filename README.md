# TheEndGame

Addition of custom recipes, although two additional mods are required for that: CraftTweaker and MTLib

import mods.the_end_game.AlchemicalPedestal;

//AlchemicalPedestal.removeRecipe(IItemStack output)
AlchemicalPedestal.removeRecipe(<minecraft:lapis_block>);

AlchemicalPedestal.TIME_DAY_AND_NIGHT
AlchemicalPedestal.TIME_DAY
AlchemicalPedestal.TIME_NIGHT

//AlchemicalPedestal.addRecipeNoFluid(IItemStack output, int time, int stability, int timeOfDay, Integer dimension, String biome, IIngredient[] inputs);
AlchemicalPedestal.addRecipeNoFluid(<minecraft:lapis_block>, 120, 30, AlchemicalPedestal.TIME_DAY_AND_NIGHT, null, null, [<minecraft:diamond>, <minecraft:diamond>]);

//AlchemicalPedestal.addRecipeFluid(IItemStack output, int time, int stability, int timeOfDay, Integer dimension, String biome, ILiquidStack fluid_1, int amountF_1, IIngredient[] inputs);
AlchemicalPedestal.addRecipeFluid(<minecraft:lapis_block>, 120, 30, AlchemicalPedestal.TIME_DAY, 1, null, <liquid:lava>, 500, [<minecraft:apple>, <minecraft:apple>, <minecraft:apple>]);

//AlchemicalPedestal.addRecipeFluids(IItemStack output, int time, int stability, int timeOfDay, Integer dimension, String biome, ILiquidStack fluid_1, int amountF_1, ILiquidStack fluid_2, int amountF_2, IIngredient[] inputs);
AlchemicalPedestal.addRecipeFluids(<minecraft:lapis_block>, 120, 30, AlchemicalPedestal.TIME_NIGHT, 0, "minecraft:river", <liquid:lava>, 1000, <liquid:water>, 1500, [<minecraft:diamond>, <minecraft:diamond>, <minecraft:apple>, <minecraft:apple>, <minecraft:apple>]);



import mods.the_end_game.Explosion;

//Explosion.removeRecipe(IItemStack output);
Explosion.removeRecipe(<the_end_game:dust_asterium>);

//Explosion.addRecipe(IItemStack output, IIngredient input, int surviveChance);
Explosion.addRecipe(<the_end_game:dust_asterium>, <minecraft:lapis_block>*4, 100);
