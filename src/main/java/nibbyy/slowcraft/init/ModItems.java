package nibbyy.slowcraft.init;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import nibbyy.slowcraft.Slowcraft;

import java.util.function.Function;

public class ModItems {
	public static <T extends Item> T register(String name, Function<Item.Properties, T> itemFactory, Item.Properties settings) {
		// Create the item key.
		ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Slowcraft.MOD_ID, name));

		// Create the item instance.
		T item = itemFactory.apply(settings.setId(itemKey));

		// Register the item.
		Registry.register(BuiltInRegistries.ITEM, itemKey, item);

		return item;
	}

	// Example SlowTool item
	public static final Item RUBBING_STICKS = register(
			"rubbing_sticks",
			properties -> new SlowTool(
					SlowTool.config()
							.itemOutput(Items.DIRT, 1)
							.useTime(10),
					properties
			),
			new Item.Properties()
	);

	public static void initialize() {}
}