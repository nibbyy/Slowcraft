package nibbyy.slowcraft.items;

import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import nibbyy.slowcraft.Slowcraft;

public class SlowComponents {
	public static final DataComponentType<SlowToolComponent> SLOW_TOOL =
			Registry.register(
					BuiltInRegistries.DATA_COMPONENT_TYPE,
					Identifier.fromNamespaceAndPath(Slowcraft.MOD_ID, "slow_tool"),
					DataComponentType
							.<SlowToolComponent>builder()
							.persistent(SlowToolComponent.CODEC)
							.networkSynchronized(SlowToolComponent.STREAM_CODEC)
							.build()
			);

	public static final DataComponentType<SlowTooltipComponent> SLOW_TOOL_TOOLTIP = Registry.register(
			BuiltInRegistries.DATA_COMPONENT_TYPE,
			Identifier.fromNamespaceAndPath(Slowcraft.MOD_ID, "slow_tool_tooltip"),
			DataComponentType.<SlowTooltipComponent>builder()
					.persistent(SlowTooltipComponent.CODEC)
					.build()
	);

	public static void initialize() {}
}
