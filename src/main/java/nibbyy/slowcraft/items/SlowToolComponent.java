package nibbyy.slowcraft.items;

import com.mojang.serialization.Codec;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;
import nibbyy.slowcraft.registry.SlowToolDefinitions;
import nibbyy.slowcraft.registry.SlowToolRecord;

import java.util.function.Consumer;

public record SlowToolComponent(Holder<SlowToolRecord> definition) implements TooltipProvider {
	public static final Codec<SlowToolComponent> CODEC =
			RegistryFixedCodec
					.<SlowToolRecord>create(
							SlowToolDefinitions.REGISTRY_KEY
					)
					.xmap(
							SlowToolComponent::new,
							SlowToolComponent::definition
					);

	public static final StreamCodec<
			RegistryFriendlyByteBuf,
			SlowToolComponent
			> STREAM_CODEC =
			ByteBufCodecs
					.<SlowToolRecord>holderRegistry(
							SlowToolDefinitions.REGISTRY_KEY
					)
					.map(
							SlowToolComponent::new,
							SlowToolComponent::definition
					);

	public SlowToolRecord value() {
		return definition.value();
	}


	@Override
	public void addToTooltip(Item.TooltipContext context, Consumer<Component> consumer, TooltipFlag flag, DataComponentGetter components) {
		SlowToolRecord slowTool = value();

		int remainingProgress = components.getOrDefault(DataComponents.DAMAGE, slowTool.maxProgress());

		int remainingSeconds = Math.max(1, (int) Math.ceil(remainingProgress / (double) SlowToolRecord.PROGRESS_STEPS_PER_SECOND));

		consumer.accept(
				Component.translatable(
						"item.slowcraft.slow_tool.crafting_time",
						remainingSeconds
				).withStyle(ChatFormatting.GRAY)
		);
	}
}
