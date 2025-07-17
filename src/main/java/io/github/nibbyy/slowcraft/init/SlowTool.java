package io.github.nibbyy.slowcraft.init;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

public class SlowTool extends ToolItem {
    private final Item itemOutput;
    private final SoundEvent usageSound;
    private final SoundEvent finishSound;

    public SlowTool(Item itemOutput, int itemUses, SoundEvent usageSound, SoundEvent finishSound, Settings settings) {
        super(new SlowMaterial(itemUses), settings);
        this.itemOutput = itemOutput;
        this.usageSound = usageSound;
        this.finishSound = finishSound;
    }

    protected void playCraftingSound(World world, LivingEntity player) {
        player.playSound(usageSound,
                0.25F + 0.25F * (float) world.random.nextInt(2),
                (world.random.nextFloat() - world.random.nextFloat()) * 0.25F + 1.75F);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        user.setCurrentHand(hand);
        return TypedActionResult.consume(user.getStackInHand(hand));
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return 72000;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BRUSH;
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        int currentDamage = stack.getDamage();
        int iUseTime = user.getItemUseTime();

        // Play usageSound in intervals
        if (iUseTime % 8 == 0 || iUseTime == 1) {
            playCraftingSound(world, user);
        }

        if (iUseTime > 1 && iUseTime % 5 == 0) {
            if (currentDamage > 1) {
                // Crafting incrementation
                stack.setDamage(currentDamage - 1);
            } else {
                // Finished crafting
                ItemStack output = new ItemStack(itemOutput);
                user.setStackInHand(Hand.MAIN_HAND, output);
                user.playSound(finishSound, 0.8F, 0.8F);
            }
        }
    }
}
