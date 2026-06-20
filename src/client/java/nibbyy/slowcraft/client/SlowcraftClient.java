package nibbyy.slowcraft.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelModifier;
import net.minecraft.resources.Identifier;
import nibbyy.slowcraft.Slowcraft;
import nibbyy.slowcraft.client.items.SlowToolModel;

public class SlowcraftClient implements ClientModInitializer {
	private static final Identifier SLOW_TOOL_ID =
			Identifier.fromNamespaceAndPath(Slowcraft.MOD_ID, "slowtool");

	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		ModelLoadingPlugin.register(pluginContext -> pluginContext
				.modifyItemModelAfterBake()
				.register(
						ModelModifier.WRAP_PHASE,
						(model, context) -> {
							if (context.itemId().equals(SLOW_TOOL_ID)) {
								return new SlowToolModel(model);
							}

							return model;
						}
				)
		);
	}
}