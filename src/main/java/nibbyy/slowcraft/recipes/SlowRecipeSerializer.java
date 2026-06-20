package nibbyy.slowcraft.recipes;

import net.fabricmc.fabric.api.recipe.v1.sync.RecipeSynchronization;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.RecipeSerializer;
import nibbyy.slowcraft.Slowcraft;

public class SlowRecipeSerializer {
	public static final RecipeSerializer<DamageToolRecipe> DAMAGE_TOOL_SHAPELESS = Registry.register(
			BuiltInRegistries.RECIPE_SERIALIZER,
			Identifier.fromNamespaceAndPath(Slowcraft.MOD_ID, "damage_tool_shapeless"),
			DamageToolRecipe.SERIALIZER
	);

	public static void initialize() {
		RecipeSynchronization.synchronizeRecipeSerializer(
				DAMAGE_TOOL_SHAPELESS
		);
	}

	private SlowRecipeSerializer() {}
}