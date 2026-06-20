package nibbyy.slowcraft.client.compat.jei;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public record JEISlowtoolRecipe(
		Identifier id,
		ItemStack tool,
		List<ItemStack> outputs,
		int seconds
) {}