package nibbyy.slowcraft.client.items;

import net.fabricmc.fabric.api.client.model.loading.v1.wrapper.WrapperBakedItemModel;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import nibbyy.slowcraft.items.SlowComponents;
import nibbyy.slowcraft.items.SlowToolComponent;
import nibbyy.slowcraft.registry.SlowToolRecord;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class SlowToolModel extends WrapperBakedItemModel {
	public SlowToolModel(ItemModel wrapped) {
		super(wrapped);
	}

	@Override
	public void update(
			@NonNull ItemStackRenderState state,
			ItemStack stack,
			@NonNull ItemModelResolver resolver,
			@NonNull ItemDisplayContext displayContext,
			@Nullable ClientLevel level,
			@Nullable ItemOwner itemOwner,
			int seed)
	{
		SlowToolComponent component = stack.get(SlowComponents.SLOW_TOOL);

		if (component == null) {
			super.update(state, stack, resolver, displayContext, level, itemOwner, seed);
			return;
		}

		SlowToolRecord definition = component.value();
		ItemStack displayStack = definition.display().create();

		boolean canRenderDisplay = !displayStack.isEmpty() && displayStack.getItem() != stack.getItem();

		if (canRenderDisplay) {
			resolver.appendItemLayers(state, displayStack, displayContext, level, itemOwner, seed);
		}

		if (definition.showOverlay() || !canRenderDisplay) {
			super.update(state, stack, resolver, displayContext, level, itemOwner, seed);
		}
	}
}