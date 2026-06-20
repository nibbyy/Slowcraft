package nibbyy.slowcraft.items;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import nibbyy.slowcraft.registry.SlowToolRecord;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class DataDrivenSlowTool extends Item {
	private static final SoundEvent USAGE_SOUND = SoundEvents.BRUSH_GENERIC;
	private static final SoundEvent FINISH_SOUND = SoundEvents.BUBBLE_POP;

	public DataDrivenSlowTool(Properties properties) {
		super(properties);
	}

	public @NonNull InteractionResult use(
			@NonNull Level level,
			Player player,
			@NonNull InteractionHand hand
	) {
		ItemStack stack = player.getItemInHand(hand);
		SlowToolRecord definition = getDefinition(stack);

		if (definition == null) {
			return InteractionResult.PASS;
		}

		initializeProgress(stack, definition);
		player.startUsingItem(hand);

		return InteractionResult.CONSUME;
	}

	@Override
	public @NonNull ItemUseAnimation getUseAnimation(@NonNull ItemStack stack) {
		return ItemUseAnimation.BRUSH;
	}

	@Override
	public int getUseDuration(@NonNull ItemStack stack, @NonNull LivingEntity user) {
		return 72000;
	}

	@Override
	public void onUseTick(@NonNull Level level, LivingEntity user, ItemStack stack, int remainingUseTime) {
		SlowToolRecord definition = getDefinition(stack);

		if (definition == null) {
			user.stopUsingItem();
			return;
		}

		int timeUsing = user.getTicksUsingItem();
		int currentProgress = stack.getDamageValue();

		boolean isProgressTick = timeUsing > 1 && timeUsing % 5 == 0;
		boolean isFinishingTick = isProgressTick && currentProgress <= 1;

		if (timeUsing == 1 || timeUsing % 8 == 0) {
			playCraftingSound(user, USAGE_SOUND);
		}

		if (level.isClientSide() && isFinishingTick) {
			playCraftingSound(user, FINISH_SOUND);
		}

		if (!level.isClientSide() && isProgressTick) {
			if (currentProgress > 1) {
				stack.setDamageValue(currentProgress - 1);
			} else {
				finishCrafting(user, definition);
			}
		}
	}

	static void initializeProgress(ItemStack stack, SlowToolRecord definition) {
		int requiredProgress = definition.maxProgress();
		Integer currentMax = stack.get(DataComponents.MAX_DAMAGE);

		if (currentMax == null || currentMax != requiredProgress) {
			stack.set(
					DataComponents.MAX_DAMAGE,
					requiredProgress
			);
			stack.set(
					DataComponents.DAMAGE,
					requiredProgress
			);
		} else if (!stack.has(DataComponents.DAMAGE)) {
			stack.set(
					DataComponents.DAMAGE,
					requiredProgress
			);
		}
	}

	private static void finishCrafting(LivingEntity user, SlowToolRecord definition) {
		if (!(user instanceof Player player)) {
			user.stopUsingItem();
			return;
		}

		InteractionHand usedHand = user.getUsedItemHand();
		player.setItemInHand(usedHand, ItemStack.EMPTY);

		for (ItemStackTemplate output : definition.outputs()) {
			ItemStack outputStack = output.create();

			if (!outputStack.isEmpty()) {
				player.getInventory().placeItemBackInInventory(outputStack);
			}
		}

		user.stopUsingItem();
	}

	private static @Nullable SlowToolRecord getDefinition(ItemStack stack) {
		SlowToolComponent component = stack.get(SlowComponents.SLOW_TOOL);

		return component == null ? null : component.value();
	}

	private static void playCraftingSound(LivingEntity user, SoundEvent sound) {
		user.playSound(sound, 0.25F + 0.25F * user.getRandom().nextInt(2),
				(user.getRandom().nextFloat() - user.getRandom().nextFloat())
						* 0.25F + 1.75F);
	}

	@Override
	public Component getName(ItemStack stack) {
		SlowToolRecord definition = getDefinition(stack);

		if (definition != null && definition.name().isPresent()) {
			return definition.name().get().copy();
		}

		return super.getName(stack);
	}

	private static void initializeFromDefinition(ItemStack stack) {
		SlowToolRecord definition = getDefinition(stack);

		if (definition != null) {
			initializeProgress(stack, definition);
		}
	}

	@Override
	public void onCraftedBy(ItemStack stack, Player player) {
		super.onCraftedBy(stack, player);

		initializeFromDefinition(stack);
	}

	@Override
	public void onCraftedPostProcess(ItemStack stack, Level level) {
		super.onCraftedPostProcess(stack, level);

		initializeFromDefinition(stack);
	}

	@Override
	public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, @Nullable EquipmentSlot slot) {
		super.inventoryTick(stack, level, entity, slot);

		initializeFromDefinition(stack);
	}
}