package nibbyy.slowcraft.init;

import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import nibbyy.slowcraft.Slowcraft;

public class ModComponents {
	public static final DataComponentType<SlowTooltipComponent> SLOW_TOOL_TOOLTIP = Registry.register(
			BuiltInRegistries.DATA_COMPONENT_TYPE,
			Identifier.fromNamespaceAndPath(Slowcraft.MOD_ID, "slow_tool_tooltip"),
			DataComponentType.<SlowTooltipComponent>builder()
					.persistent(SlowTooltipComponent.CODEC)
					.build()
	);

	public static void initialize() {
		Slowcraft.LOGGER.info("[Slowcraft] Registered components");
	}
}
