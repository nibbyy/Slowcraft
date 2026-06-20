package nibbyy.slowcraft.items;

import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import nibbyy.slowcraft.Slowcraft;
import nibbyy.slowcraft.registry.SlowToolDefinitions;
import nibbyy.slowcraft.registry.SlowToolRecord;

import java.util.Comparator;
import java.util.function.Function;

public class SlowItems {
	private static <T extends Item> T register(String name, Function<Item.Properties, T> itemFactory, Item.Properties settings) {
		// Create the item key.
		ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Slowcraft.MOD_ID, name));

		// Create the item instance.
		T item = itemFactory.apply(settings.setId(itemKey));

		// Register the item.
		Registry.register(BuiltInRegistries.ITEM, itemKey, item);

		return item;
	}

	public static final ResourceKey<CreativeModeTab> SLOW_TOOLS_KEY = ResourceKey.create(
			Registries.CREATIVE_MODE_TAB, Identifier.fromNamespaceAndPath(Slowcraft.MOD_ID, "slowtools")
	);
	public static final CreativeModeTab SLOWTOOLS = Registry.register(
			BuiltInRegistries.CREATIVE_MODE_TAB,
			SLOW_TOOLS_KEY,
			FabricCreativeModeTab.builder()
					.title(
							Component.translatable("itemGroup.slowcraft.slowtools")
					)
					.icon(
							() -> new ItemStack(SlowItems.SLOW_TOOL)
					)
					.displayItems(
							SlowItems::populate
					)
					.build()
	);

	private SlowItems() {}

	public static final DataDrivenSlowTool SLOW_TOOL = register(
			"slowtool",
			DataDrivenSlowTool::new,
			new Item.Properties()
					.stacksTo(1)
	);

	public static void initialize() {}

	private static void populate(CreativeModeTab.ItemDisplayParameters displayParameters, CreativeModeTab.Output output) {
		addDataDrivenSlowTools(displayParameters, output);
		addJavaSlowTools(output);
	}

	private static void addDataDrivenSlowTools(CreativeModeTab.ItemDisplayParameters displayParameters, CreativeModeTab.Output output) {
		displayParameters.holders()
				.lookup(SlowToolDefinitions.REGISTRY_KEY)
				.ifPresent(registry -> registry.listElements()
						.sorted(Comparator.comparing(
								holder -> holder
										.key()
										.identifier()
										.toString()
								)
						)
						.filter(
								holder -> !holder.value().excludeFromCreative()
						)
						.map(
								SlowItems::createDataDrivenStack
						)
						.forEach(output::accept)
				);
	}

	public static ItemStack createDataDrivenStack(Holder<SlowToolRecord> definition) {
		ItemStack stack = new ItemStack(SlowItems.SLOW_TOOL);

		stack.set(SlowComponents.SLOW_TOOL, new SlowToolComponent(definition));

		DataDrivenSlowTool.initializeProgress(
				stack,
				definition.value()
		);

		return stack;
	}

	private static void addJavaSlowTools(CreativeModeTab.Output output) {
		BuiltInRegistries.ITEM.stream()
				.filter(SlowTool.class::isInstance)
				.map(SlowTool.class::cast)
				.filter(SlowTool::shouldAddToCreative)
				.forEach(output::accept);
	}
}