package nibbyy.slowcraft.init;

import com.mojang.brigadier.Command;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import nibbyy.slowcraft.items.SlowItems;
import nibbyy.slowcraft.items.SlowTool;
import nibbyy.slowcraft.registry.SlowToolDefinitions;
import nibbyy.slowcraft.registry.SlowToolRecord;

import java.util.List;

public final class SlowcraftCommands {
	private SlowcraftCommands() {}

	public static void initialize() {
		CommandRegistrationCallback.EVENT.register(
				((dispatcher, buildContext, selection) ->
						dispatcher.register(
								Commands.literal("slowcraft")
										.then(
												Commands.literal("list")
														.executes(context ->
																listSlowTools(context.getSource()))
										) .then(
												Commands.literal("give")
														.requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
														.then(
																Commands.argument(
																		"definition",
																		ResourceArgument.resource(buildContext, SlowToolDefinitions.REGISTRY_KEY)
																)
																		.executes(context ->
																				giveSlowTool(
																						context.getSource(),
																						ResourceArgument.getResource(
																								context,
																								"definition",
																								SlowToolDefinitions.REGISTRY_KEY
																						),
																						context.getSource().getPlayerOrException()
																				))
																		.then(
																				Commands.argument(
																						"target",
																						EntityArgument.player()
																				)
																						.executes(context ->
																								giveSlowTool(
																										context.getSource(),
																										ResourceArgument.getResource(
																												context,
																												"definition",
																												SlowToolDefinitions.REGISTRY_KEY
																										),
																										EntityArgument.getPlayer(context, "target")
																								))
																		)
														)
										)
						)
				)
		);
	}

	private static int listSlowTools(CommandSourceStack source) {
		Registry<SlowToolRecord> definitions = source.registryAccess().lookupOrThrow(SlowToolDefinitions.REGISTRY_KEY);

		List<String> dataDrivenIds = definitions.keySet()
				.stream()
				.map(Identifier::toString)
				.sorted()
				.toList();

		List<String> javaDrivenIds = BuiltInRegistries.ITEM.stream()
				.filter(SlowTool.class::isInstance)
				.map(BuiltInRegistries.ITEM::getKey)
				.map(Identifier::toString)
				.sorted()
				.toList();

		sendSection(
				source,
				"commands.slowcraft.list.data_driven",
				dataDrivenIds
		);

		sendSection(
				source,
				"commands.slowcraft.list.java_driven",
				javaDrivenIds
		);

		return Command.SINGLE_SUCCESS;
	}

	private static void sendSection(CommandSourceStack source, String headingTranslationKey, List<String> ids) {
		source.sendSuccess(
				() -> Component.translatable(headingTranslationKey, ids.size()), false
		);

		for (String id : ids) {
			source.sendSuccess(
					() -> Component.literal("  " + id),
					false
			);
		}
	}

	private static int giveSlowTool(CommandSourceStack source, Holder.Reference<SlowToolRecord> definition, ServerPlayer target) {
		ItemStack stack = SlowItems.createDataDrivenStack(definition);
		Component toolName = stack.getDisplayName();

		String definitionId = definition.key().identifier().toString();

		target.getInventory().placeItemBackInInventory(stack);

		source.sendSuccess(
				() -> Component.translatable(
						"commands.slowcraft.give.success",
						toolName,
						definitionId,
						target.getDisplayName()
				),
				true
		);

		return Command.SINGLE_SUCCESS;
	}
}