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

public class SlowTool extends Item {
	private final SlowToolStack itemOutput;
	private final SoundEvent usageSound;
	private final SoundEvent finishSound;
	private final SlowToolStack returnItem;

	public SlowTool(SlowToolConfig config, Item.Properties properties) {
		super(properties.durability(config.itemUses));

		this.itemOutput = config.itemOutput;
		this.usageSound = config.usageSound;
		this.finishSound = config.finishSound;
		this.returnItem = config.returnItem;

		if (config.itemUses <= 0) {
			throw new IllegalArgumentException("SlowTool itemUses must be greater than 0");
		}

		if (config.itemOutput.isEmpty()) {
			throw new IllegalArgumentException("SlowTool itemOutput is required");
		}
	}

	public static SlowToolConfig config() {
		return new SlowToolConfig();
	}

	// Config makes constructing the item simpler
	public static class SlowToolConfig {
		private SlowToolStack itemOutput = SlowToolStack.EMPTY;
		private int itemUses;
		private SoundEvent usageSound = SoundEvents.BRUSH_GENERIC;
		private SoundEvent finishSound = SoundEvents.BUBBLE_POP;
		private SlowToolStack returnItem = SlowToolStack.EMPTY;

		// If only one field is provided, only give one item on finish
		public SlowToolConfig itemOutput(Item itemOutput) {
			return itemOutput(itemOutput, 1);
		}

		// If two fields provided, define amount to give
		public SlowToolConfig itemOutput(Item itemOutput, int count) {
			this.itemOutput = new SlowToolStack(itemOutput, count);
			return this;
		}

		public SlowToolConfig itemUses(int itemUses) {
			this.itemUses = itemUses;
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

		public SlowToolConfig returnItem(Item returnItem, int count) {
			this.returnItem = new SlowToolStack(returnItem, count);
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
		int useTime = user.getTicksUsingItem();

		// Play usageSound in intervals
		if (useTime % 8 == 0 || useTime == 1) {
			playCraftingSound(user);
		}

		if (!level.isClientSide() && useTime > 1 && useTime % 5 == 0) {
			if (currentDamage > 1) {
				// Crafting incrementation
				stack.setDamageValue(currentDamage - 1);
			} else {
				// Finished crafting
				ItemStack output = itemOutput.createStack();
				InteractionHand usedHand = user.getUsedItemHand();

				if (user instanceof Player player) {
					player.setItemInHand(usedHand, returnItem.createStack()); // Deletes the SlowTool, or replaces it with returnItem
					player.getInventory().placeItemBackInInventory(output); // Adds the itemOutput to inventory
				}

				user.playSound(finishSound, 0.8f, 0.8f);
				user.stopUsingItem();
			}
		}
	}

	// Called by onUseTick
	protected void playCraftingSound(LivingEntity user) {
		user.playSound(
				usageSound,
				0.25F + 0.25F * user.getRandom().nextInt(2),
				(user.getRandom().nextFloat() - user.getRandom().nextFloat()) * 0.25f + 1.75f
		);
	}
}
