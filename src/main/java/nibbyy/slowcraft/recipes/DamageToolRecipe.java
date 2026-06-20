package nibbyy.slowcraft.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.ShapelessCraftingRecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;

import java.util.List;

public class DamageToolRecipe extends NormalCraftingRecipe {
	public static final MapCodec<DamageToolRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			CommonInfo.MAP_CODEC.forGetter(recipe -> recipe.commonInfo),
			CraftingBookInfo.MAP_CODEC.forGetter(recipe -> recipe.bookInfo),
			Ingredient.CODEC.fieldOf("input").forGetter(recipe -> recipe.input),
			Ingredient.CODEC.fieldOf("tool").forGetter(recipe -> recipe.tool),
			ItemStackTemplate.CODEC.fieldOf("result").forGetter(recipe -> recipe.result),
			Codec.INT.optionalFieldOf("tool_damage", 1).forGetter(recipe -> recipe.toolDamage)
	).apply(instance, DamageToolRecipe::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, DamageToolRecipe> STREAM_CODEC = StreamCodec.composite(
			CommonInfo.STREAM_CODEC, recipe -> recipe.commonInfo,
			CraftingBookInfo.STREAM_CODEC, recipe -> recipe.bookInfo,
			Ingredient.CONTENTS_STREAM_CODEC, recipe -> recipe.input,
			Ingredient.CONTENTS_STREAM_CODEC, recipe -> recipe.tool,
			ItemStackTemplate.STREAM_CODEC, recipe -> recipe.result,
			ByteBufCodecs.VAR_INT, recipe -> recipe.toolDamage,
			DamageToolRecipe::new
	);

	public static final RecipeSerializer<DamageToolRecipe> SERIALIZER = new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);

	private final Ingredient input;
	private final Ingredient tool;
	private final ItemStackTemplate result;
	private final int toolDamage;

	public DamageToolRecipe(
			Recipe.CommonInfo commonInfo,
			CraftingRecipe.CraftingBookInfo bookInfo,
			Ingredient input,
			Ingredient tool,
			ItemStackTemplate result,
			int toolDamage
	) {
		super(commonInfo, bookInfo);
		this.input = input;
		this.tool = tool;
		this.result = result;
		this.toolDamage = toolDamage;
	}

	@Override
	public boolean matches(CraftingInput input, net.minecraft.world.level.Level level) {
		if (input.ingredientCount() != 2) {
			return false;
		}

		boolean foundInput = false;
		boolean foundTool = false;

		for (int slot = 0; slot < input.size(); slot++) {
			ItemStack stack = input.getItem(slot);

			if (stack.isEmpty()) {
				continue;
			}

			if (!foundInput && this.input.test(stack)) {
				foundInput = true;
			} else if (!foundTool && this.tool.test(stack)) {
				foundTool = true;
			} else {
				return false;
			}
		}

		return foundInput && foundTool;
	}

	@Override
	public ItemStack assemble(CraftingInput input) {
		return this.result.create();
	}

	@Override
	public List<RecipeDisplay> display() {
		return List.of(new ShapelessCraftingRecipeDisplay(
				List.of(this.input.display(), this.tool.display()),
				new SlotDisplay.ItemStackSlotDisplay(this.result),
				new SlotDisplay.ItemSlotDisplay(Items.CRAFTING_TABLE)
		));
	}

	@Override
	public RecipeSerializer<? extends NormalCraftingRecipe> getSerializer() {
		return SlowRecipeSerializer.DAMAGE_TOOL_SHAPELESS;
	}

	@Override
	protected PlacementInfo createPlacementInfo() {
		return PlacementInfo.create(List.of(this.input, this.tool));
	}

	@Override
	public NonNullList<ItemStack> getRemainingItems(CraftingInput input) {
		NonNullList<ItemStack> remaining = CraftingRecipe.defaultCraftingReminder(input);

		for (int slot = 0; slot < input.size(); slot++) {
			ItemStack stack = input.getItem(slot);

			if (this.tool.test(stack)) {
				remaining.set(slot, damageTool(stack));
				break;
			}
		}

		return remaining;
	}

	private ItemStack damageTool(ItemStack tool) {
		ItemStack returnedTool = tool.copy();
		returnedTool.setCount(1);

		if (this.toolDamage <= 0 || !returnedTool.isDamageableItem()) {
			return returnedTool;
		}

		int newDamage = returnedTool.getDamageValue() + this.toolDamage;

		if (newDamage >= returnedTool.getMaxDamage()) {
			return ItemStack.EMPTY;
		}

		returnedTool.setDamageValue(newDamage);
		return returnedTool;
	}
}
