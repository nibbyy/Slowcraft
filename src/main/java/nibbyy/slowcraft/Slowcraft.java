package nibbyy.slowcraft;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.item.v1.ItemComponentTooltipProviderRegistry;
import net.minecraft.core.component.DataComponents;
import nibbyy.slowcraft.init.SlowcraftCommands;
import nibbyy.slowcraft.items.SlowComponents;
import nibbyy.slowcraft.items.SlowItems;
import nibbyy.slowcraft.recipes.SlowRecipeSerializer;
import nibbyy.slowcraft.registry.SlowToolDefinitions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Slowcraft implements ModInitializer {
	public static final String MOD_ID = "slowcraft";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		SlowToolDefinitions.initialize();
		SlowComponents.initialize();
		SlowItems.initialize();

		ItemComponentTooltipProviderRegistry.addAfter(
				DataComponents.DAMAGE,
				SlowComponents.SLOW_TOOL_TOOLTIP
		);
		ItemComponentTooltipProviderRegistry.addAfter(
				SlowComponents.SLOW_TOOL_TOOLTIP,
				SlowComponents.SLOW_TOOL
		);

		SlowRecipeSerializer.initialize();
		SlowcraftCommands.initialize();
	}
}