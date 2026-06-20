package nibbyy.slowcraft.items;

import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

public class SlowTool extends Item {
	private final int useTime;
	private final List<SlowToolStack> itemOutputs;
	private final SoundEvent usageSound;
	private final SoundEvent finishSound;
	private final boolean excludeFromJEI;
	private final boolean addToCreative;

	public SlowTool(SlowToolConfig config, Item.Properties properties) {
		final int maxDurability = config.useTime * 4;

		super(properties
				.durability(maxDurability) // Set the durability to 4 * .useTime, because we increment every 5 ticks (4 times per second at 20tps)
				.component(SlowComponents.SLOW_TOOL_TOOLTIP, new SlowTooltipComponent(config.useTime)) // Adds our tooltip to the item
				.component(DataComponents.DAMAGE, maxDurability) // Apply damage to the item (so its 0 durability)
		);

		this.useTime = config.useTime;
		this.itemOutputs = List.copyOf(config.itemOutputs);
		this.usageSound = config.usageSound;
		this.finishSound = config.finishSound;
		this.excludeFromJEI = config.excludeFromJEI;
		this.addToCreative = config.addToCreative;

		if (config.useTime <= 0) {
			throw new IllegalArgumentException("SlowTool useTime must be greater than 0");
		}

		if (config.itemOutputs.isEmpty()) {
			throw new IllegalArgumentException("SlowTool requires at least one itemOutput");
		}

		if (config.itemOutputs.size() > 4) {
			throw new IllegalArgumentException("SlowTool cannot have more than 4 itemOutput's");
		}
	}

	public static SlowToolConfig config() {
		return new SlowToolConfig();
	}

	public int getUseTime() {
		return useTime;
	}

	public List<ItemStack> getOutputStacks() {
		List<ItemStack> stacks = new ArrayList<>();

		for (SlowToolStack output : itemOutputs) {
			ItemStack stack = output.createStack();

			if (!stack.isEmpty()) {
				stacks.add(stack);
			}
		}

		return List.copyOf(stacks);
	}

	public boolean isExcludeFromJEI() {
		return excludeFromJEI;
	}

	public boolean shouldAddToCreative() {
		return addToCreative;
	}

	// Config makes constructing the item simpler
	public static class SlowToolConfig {
		private final List<SlowToolStack> itemOutputs = new ArrayList<>();
		private int useTime;
		private SoundEvent usageSound = SoundEvents.BRUSH_GENERIC;
		private SoundEvent finishSound = SoundEvents.BUBBLE_POP;
		private boolean excludeFromJEI = false;
		private boolean addToCreative = false;

		// If only one field is provided, only give one item on finish
		public SlowToolConfig itemOutput(Item itemOutput) {
			return itemOutput(itemOutput, 1);
		}

		// If two fields provided, define amount to give
		public SlowToolConfig itemOutput(Item itemOutput, int count) {
			this.itemOutputs.add(new SlowToolStack(itemOutput, count));
			return this;
		}

		public SlowToolConfig useTime(int seconds) {
			this.useTime = seconds;
			return this;
		}

		public SlowToolConfig usageSound(SoundEvent usageSound) {
			this.usageSound = usageSound;
			return this;
		}

		public SlowToolConfig finishSound(SoundEvent finishSound) {
			this.finishSound = finishSound;
			return this;
		}

		public SlowToolConfig excludeFromJEI() {
			this.excludeFromJEI = true;
			return this;
		}

		public SlowToolConfig addToCreative() {
			this.addToCreative = true;
			return this;
		}
	}

	// Registers ItemStacks on craft finish to avoid 'NullPointerException: Components not bound yet'
	// Registering the itemOutput as a direct ItemStack caused the above error
	private record SlowToolStack(Item item, int count) {
		private boolean isEmpty() {
			return this.item == Items.AIR || this.count <= 0;
		}

		private ItemStack createStack() {
			return this.isEmpty() ? ItemStack.EMPTY : new ItemStack(this.item, this.count);
		}
	}

	@Override
	public @NonNull InteractionResult use(@NonNull Level level, Player player, @NonNull InteractionHand hand) {
		player.startUsingItem(hand);
		return InteractionResult.CONSUME;
	}

	// Sets the use animation to the Brush animation
	@Override
	public @NonNull ItemUseAnimation getUseAnimation(@NonNull ItemStack stack) {
		return ItemUseAnimation.BRUSH;
	}

	// Allows holding right click for 60 minutes
	@Override
	public int getUseDuration(@NonNull ItemStack stack, @NonNull LivingEntity user) {
		return 72000;
	}

	// Item use logic
	@Override
	public void onUseTick(@NonNull Level level, LivingEntity user, ItemStack stack, int remainingUseTicks) {
		int currentDamage = stack.getDamageValue();
		int timeUsing = user.getTicksUsingItem();

		boolean isProgressTick = timeUsing > 1 && timeUsing % 5 == 0;
		boolean isFinishingTick = isProgressTick && currentDamage <= 1;

		if (timeUsing % 8 == 0 || timeUsing == 1) {
			playCraftingSound(user, usageSound);
		}

		if (level.isClientSide() && isFinishingTick) {
			playCraftingSound(user, finishSound);
		}

		if (!level.isClientSide() && isProgressTick) {
			if (currentDamage > 1) {
				// Crafting incrementation
				stack.setDamageValue(currentDamage - 1);
			} else {
				// Finished crafting
				InteractionHand usedHand = user.getUsedItemHand();

				if (user instanceof Player player) {
					player.setItemInHand(usedHand, ItemStack.EMPTY);
					for (SlowToolStack output : itemOutputs) {
						player.getInventory().placeItemBackInInventory(output.createStack());
					}
				}

				user.stopUsingItem();
			}
		}
	}

	// Called by onUseTick
	protected void playCraftingSound(LivingEntity user, SoundEvent sound) {
		user.playSound(
				sound,
				0.25F + 0.25F * user.getRandom().nextInt(2),
				(user.getRandom().nextFloat() - user.getRandom().nextFloat()) * 0.25f + 1.75f
		);
	}
}
