package nibbyy.slowcraft.client.compat.jei;

import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.display.ShapelessCraftingRecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import nibbyy.slowcraft.recipes.DamageToolRecipe;

import java.util.List;

public class JEISlowtoolExtension implements ICraftingCategoryExtension<DamageToolRecipe> {
	@Override
	public List<SlotDisplay> getIngredients(RecipeHolder<DamageToolRecipe> recipeHolder) {
		DamageToolRecipe recipe = recipeHolder.value();

		ShapelessCraftingRecipeDisplay display = (ShapelessCraftingRecipeDisplay) recipe.display().getFirst();

		return display.ingredients();
	}
}
