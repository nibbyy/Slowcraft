package nibbyy.slowcraft.client.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.api.registration.IVanillaCategoryExtensionRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import nibbyy.slowcraft.Slowcraft;
import nibbyy.slowcraft.items.SlowComponents;
import nibbyy.slowcraft.items.SlowItems;
import nibbyy.slowcraft.items.SlowTool;
import nibbyy.slowcraft.recipes.DamageToolRecipe;
import nibbyy.slowcraft.registry.SlowToolDefinitions;
import nibbyy.slowcraft.registry.SlowToolRecord;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public class JEISlowcraftPlugin implements IModPlugin {
	@Override
	public @NonNull Identifier getPluginUid() {
		return Identifier.fromNamespaceAndPath(Slowcraft.MOD_ID, "jei_plugin");
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registration) {
		IGuiHelper guiHelper = registration.getJeiHelpers().getGuiHelper();

		registration.addRecipeCategories(new JEISlowtoolCategory(guiHelper));
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		List<JEISlowtoolRecipe> recipes = new ArrayList<>(
				BuiltInRegistries.ITEM.stream()
						.filter(SlowTool.class::isInstance)
						.map(SlowTool.class::cast)
						.filter(tool -> !tool.isExcludeFromJEI())
						.map(JEISlowcraftPlugin::createSlowToolRecipe)
						.toList()
		);

		recipes.addAll(dataDrivenRecipes());
		List<JEISlowtoolRecipe> visibleRecipes = List.copyOf(recipes);

		registration.addRecipes(
				JEISlowtoolRecipeTypes.SLOWTOOL,
				visibleRecipes
		);

		for (JEISlowtoolRecipe recipe : visibleRecipes) {
			registration.addItemStackInfo(
					recipe.tool().copy(),
					Component.translatable("jei.slowcraft.info.slowtool.1"),
					Component.translatable("jei.slowcraft.info.slowtool.2")
			);
		}
	}

	private static JEISlowtoolRecipe createSlowToolRecipe(SlowTool tool) {
		Identifier itemId = BuiltInRegistries.ITEM.getKey(tool);

		return new JEISlowtoolRecipe(
				itemId,
				new ItemStack(tool),
				tool.getOutputStacks(),
				tool.getUseTime()
		);
	}

	private static JEISlowtoolRecipe createSlowToolRecipe(Holder.Reference<SlowToolRecord> definition) {
		SlowToolRecord slowTool = definition.value();

		Identifier definitionId = definition.key().identifier();

		List<ItemStack> outputs = slowTool.outputs()
				.stream()
				.map(output -> output.create())
				.filter(output -> !output.isEmpty())
				.toList();

		return new JEISlowtoolRecipe(
				definitionId,
				SlowItems.createDataDrivenStack(definition),
				outputs,
				slowTool.useTime()
		);
	}

	@Override
	public void registerVanillaCategoryExtensions(IVanillaCategoryExtensionRegistration registration) {
		registration.getCraftingCategory().addExtension(DamageToolRecipe.class, new JEISlowtoolExtension());
	}

	@Override
	public void registerItemSubtypes(ISubtypeRegistration registration) {
		registration.registerFromDataComponentTypes(
				SlowItems.SLOW_TOOL,
				SlowComponents.SLOW_TOOL
		);
	}

	private static List<JEISlowtoolRecipe> dataDrivenRecipes() {
		ClientPacketListener connection = Minecraft.getInstance().getConnection();

		if (connection == null) {
			return List.of();
		}

		return connection.registryAccess()
				.lookup(SlowToolDefinitions.REGISTRY_KEY)
				.map(registry -> registry.listElements()
						.map(JEISlowcraftPlugin::createSlowToolRecipe)
						.toList())
				.orElseGet(List::of);
	}
}
