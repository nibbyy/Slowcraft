package nibbyy.slowcraft;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.item.v1.ItemComponentTooltipProviderRegistry;
import net.minecraft.core.component.DataComponents;
import nibbyy.slowcraft.init.ModComponents;
import nibbyy.slowcraft.init.ModItems;
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
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		ModComponents.initialize();

		ItemComponentTooltipProviderRegistry.addAfter(
				DataComponents.DAMAGE,
				ModComponents.SLOW_TOOL_TOOLTIP
		);

		LOGGER.info("[Slowcraft] Loaded!");
		//ModItems.initialize();
	}
}