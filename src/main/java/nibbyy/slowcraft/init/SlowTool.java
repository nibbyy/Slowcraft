package nibbyy.slowcraft.init;

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

import java.util.ArrayList;
import java.util.List;

public class SlowTool extends Item {
	private final List<SlowToolStack> itemOutputs;
	private final SoundEvent usageSound;
	private final SoundEvent finishSound;

	public SlowTool(SlowToolConfig config, Item.Properties properties) {
		super(properties
				.durability(config.useTime * 4) // Set the durability to 4 * .useTime, because we increment every 5 ticks (4 times per second at 20tps)
				.component(ModComponents.SLOW_TOOL_TOOLTIP, new SlowTooltipComponent(config.useTime)) // Adds our tooltip to the item
		);

		this.itemOutputs = List.copyOf(config.itemOutputs);
		this.usageSound = config.usageSound;
		this.finishSound = config.finishSound;

		if (config.useTime <= 0) {
			throw new IllegalArgumentException("SlowTool useTime must be greater than 0");
		}

		if (config.itemOutputs.isEmpty()) {
			throw new IllegalArgumentException("SlowTool requires at least one itemOutput");
		}
	}

	public static SlowToolConfig config() {
		return new SlowToolConfig();
	}

	// Config makes constructing the item simpler
	public static class SlowToolConfig {
		private final List<SlowToolStack> itemOutputs = new ArrayList<>();
		private int useTime;
		private SoundEvent usageSound = SoundEvents.BRUSH_GENERIC;
		private SoundEvent finishSound = SoundEvents.BUBBLE_POP;

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
	}

	// Registers ItemStacks on craft finish to avoid 'NullPointerException: Components not bound yet'
	// Registering the itemOutput as a direct ItemStack caused the above error
	private record SlowToolStack(Item item, int count) {
		private static final SlowToolStack EMPTY = new SlowToolStack(Items.AIR, 0);

		private boolean isEmpty() {
			return this.item == Items.AIR || this.count <= 0;
		}

		private ItemStack createStack() {
			return this.isEmpty() ? ItemStack.EMPTY : new ItemStack(this.item, this.count);
		}
	}

	// Sets its durability to 0 after being crafted
	@Override
	public void onCraftedPostProcess(ItemStack stack, Level level) {
		stack.setDamageValue(stack.getMaxDamage());
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		player.startUsingItem(hand);
		return InteractionResult.CONSUME;
	}

	// Sets the use animation to the Brush animation
	@Override
	public ItemUseAnimation getUseAnimation(ItemStack stack) {
		return ItemUseAnimation.BRUSH;
	}

	// Allows holding right click for 60 minutes
	@Override
	public int getUseDuration(ItemStack stack, LivingEntity user) {
		return 72000;
	}

	// Item use logic
	@Override
	public void onUseTick(Level level, LivingEntity user, ItemStack stack, int remainingUseTicks) {
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
