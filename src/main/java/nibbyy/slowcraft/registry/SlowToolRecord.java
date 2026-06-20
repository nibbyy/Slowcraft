package nibbyy.slowcraft.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.world.item.ItemStackTemplate;

import java.util.List;
import java.util.Optional;

public record SlowToolRecord(
		ItemStackTemplate display,
		List<ItemStackTemplate> outputs,
		int useTime,
		Optional<Component> name,
		boolean excludeFromCreative,
		boolean showOverlay
) {
	public static final int PROGRESS_STEPS_PER_SECOND = 4;

	public static final Codec<SlowToolRecord> CODEC =
			RecordCodecBuilder.create(instance -> instance.group(
					ItemStackTemplate.CODEC
							.fieldOf("display")
							.forGetter(SlowToolRecord::display),

					ItemStackTemplate.CODEC
							.listOf()
							.fieldOf("outputs")
							.forGetter(SlowToolRecord::outputs),

					Codec.intRange(1, Integer.MAX_VALUE / PROGRESS_STEPS_PER_SECOND)
							.fieldOf("use_time")
							.forGetter(SlowToolRecord::useTime),

					ComponentSerialization.CODEC
							.optionalFieldOf("name")
							.forGetter(SlowToolRecord::name),

					Codec.BOOL
							.optionalFieldOf("exclude_from_creative", false)
							.forGetter(SlowToolRecord::excludeFromCreative),

					Codec.BOOL
							.optionalFieldOf("show_overlay", true)
							.forGetter(SlowToolRecord::showOverlay)

			).apply(instance, SlowToolRecord::new));

	public SlowToolRecord {
		if (outputs.isEmpty()) {
			throw new IllegalArgumentException(
					"A Slowtool definition requires at least one output"
			);
		}

		if (outputs.size() > 4) {
			throw new IllegalArgumentException(
					"A Slowtool can have at most 4 outputs"
			);
		}

		outputs = List.copyOf(outputs);
	}

	public int maxProgress() {
		return useTime * PROGRESS_STEPS_PER_SECOND;
	}
}