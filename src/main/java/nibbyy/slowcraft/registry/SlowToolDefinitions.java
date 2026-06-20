package nibbyy.slowcraft.registry;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import nibbyy.slowcraft.Slowcraft;
import nibbyy.slowcraft.items.SlowTool;

import java.util.List;

public final class SlowToolDefinitions {
	public static final ResourceKey<Registry<SlowToolRecord>> REGISTRY_KEY =
			ResourceKey.createRegistryKey(
					Identifier.fromNamespaceAndPath(
							Slowcraft.MOD_ID,
							"slow_tool"
					)
			);

	private SlowToolDefinitions() {}

	public static void initialize() {
		DynamicRegistries.registerSynced(
				REGISTRY_KEY,
				SlowToolRecord.CODEC
		);

		ServerLifecycleEvents.SERVER_STARTED.register(
				SlowToolDefinitions::logSlowTools
		);
	}

	private static void logSlowTools(MinecraftServer server) {
		logDefinitions(server);
		logJavaSlowTools();
	}

	private static void logDefinitions(MinecraftServer server) {
		server.registryAccess()
				.lookup(REGISTRY_KEY)
				.ifPresentOrElse(
						registry -> {
							List<String> ids = registry.keySet()
									.stream()
									.map(Identifier::toString)
									.sorted()
									.toList();

							logIds(
									"data-driven Slowtool definition(s)",
									ids
							);
						},
						() -> Slowcraft.LOGGER.warn(
								"[Slowcraft] Could not find the Slowtool definition registry: {}",
								REGISTRY_KEY.identifier()
						)
				);
	}

	private static void logJavaSlowTools() {
		List<String> ids = BuiltInRegistries.ITEM.stream()
				.filter(SlowTool.class::isInstance)
				.map(BuiltInRegistries.ITEM::getKey)
				.map(Identifier::toString)
				.sorted()
				.toList();

		logIds("Java-driven Slowtool(s)", ids);
	}

	private static void logIds(String description, List<String> ids) {
		if (ids.isEmpty()) {
			Slowcraft.LOGGER.info(
					"[Slowcraft] Loaded 0 {}",
					description
			);
			return;
		}

		Slowcraft.LOGGER.info(
				"[Slowcraft] Loaded {} {}:\n{}",
				ids.size(),
				description,
				String.join("\n", ids)
		);
	}
}