package nibbyy.slowcraft.mixin;

import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.item.ItemStack;
import nibbyy.slowcraft.items.SlowComponents;
import nibbyy.slowcraft.items.SlowTool;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GrindstoneMenu.class)
public class GrindstoneMenuMixin {
	@Inject (method = "computeResult", at = @At ("HEAD"), cancellable = true)
	private void slowcraft$preventSlowToolGrinding(ItemStack input, ItemStack additional, CallbackInfoReturnable<ItemStack> cir) {
		if (isSlowTool(input)|| isSlowTool(additional)) {
			cir.setReturnValue(ItemStack.EMPTY);
		}
	}

	@Unique
	private static boolean isSlowTool(ItemStack stack) {
		return stack.getItem() instanceof SlowTool || stack.has(SlowComponents.SLOW_TOOL);
	}
}