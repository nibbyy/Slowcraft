package nibbyy.slowcraft.items;

import com.mojang.serialization.Codec;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;

import java.util.function.Consumer;

public record SlowTooltipComponent(int useTimeSeconds) implements TooltipProvider {
	public static final Codec<SlowTooltipComponent> CODEC =
			Codec.INT.xmap(SlowTooltipComponent::new, SlowTooltipComponent::useTimeSeconds);

	@Override
	public void addToTooltip(Item.TooltipContext context, Consumer<Component> consumer, TooltipFlag flag, DataComponentGetter components) {
		int damage = components.getOrDefault(DataComponents.DAMAGE, 0);
		int secondsRemaining = this.useTimeSeconds;

		if (damage > 0) {
			secondsRemaining = Math.max(1, (int) Math.ceil(damage / (double) 4));
		}

		consumer.accept(Component.translatable(
				"item.slowcraft.slow_tool.crafting_time",
				secondsRemaining
		).withStyle(ChatFormatting.GRAY));
	}
}