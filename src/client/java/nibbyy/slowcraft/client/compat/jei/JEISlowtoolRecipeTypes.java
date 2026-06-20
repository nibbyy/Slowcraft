package nibbyy.slowcraft.client.compat.jei;

import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.resources.Identifier;
import nibbyy.slowcraft.Slowcraft;

public final class JEISlowtoolRecipeTypes {
	public static final Identifier SLOWTOOL_ID = Identifier.fromNamespaceAndPath(Slowcraft.MOD_ID, "slowtool");

	public static final IRecipeType<JEISlowtoolRecipe> SLOWTOOL = IRecipeType.create(SLOWTOOL_ID, JEISlowtoolRecipe.class);

	private JEISlowtoolRecipeTypes() {}
}