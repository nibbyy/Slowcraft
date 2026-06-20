package nibbyy.slowcraft.client.compat.jei;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.placement.HorizontalAlignment;
import mezz.jei.api.gui.placement.VerticalAlignment;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import nibbyy.slowcraft.Slowcraft;
import org.jspecify.annotations.NonNull;

public class JEISlowtoolCategory implements IRecipeCategory<JEISlowtoolRecipe> {
	private static final int INPUT_SLOT_X = 10;
	private static final int INPUT_SLOT_Y = 19;

	private static final int OUTPUT_COLUMNS = 2;
	private static final int OUTPUT_SLOT_SPACING = 28;
	private static final int SINGLE_OUTPUT_X = 80;
	private static final int MULTI_OUTPUT_X = SINGLE_OUTPUT_X - 16;

	private static final int ARROW_X = 30;
	private static final int ARROW_Y = 20;

	private static final int TIME_TEXT_Y = 38;
	private static final int TIME_TEXT_WIDTH = 42;
	private static final int TIME_TEXT_HEIGHT = 12;

	private final IDrawable arrow;
	private final IDrawable icon;
	private final int width = 116;
	private final int height = 56;

	private static final Identifier ICON_TEXTURE = Identifier.fromNamespaceAndPath(
			Slowcraft.MOD_ID,
			"textures/item/progressive_crafting.png"
	);

	public JEISlowtoolCategory(IGuiHelper guiHelper) {
		this.icon = guiHelper.drawableBuilder(ICON_TEXTURE, 0, 0, 16, 16)
				.setTextureSize(16, 16)
				.build();
		this.arrow = guiHelper.createAnimatedRecipeArrow(80);
	}

	@Override
	public IRecipeType<JEISlowtoolRecipe> getRecipeType() {
		return JEISlowtoolRecipeTypes.SLOWTOOL;
	}

	@Override
	public @NonNull Component getTitle() {
		return Component.translatable("slowcraft.category.progressive_crafting");
	}

	@Override
	public int getWidth() { return width; }

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@Override
	public void draw(JEISlowtoolRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor guiGraphics, double mouseX, double mouseY) {
		arrow.draw(guiGraphics, ARROW_X, ARROW_Y);
	}

	@Override
	public void createRecipeExtras(IRecipeExtrasBuilder builder, JEISlowtoolRecipe recipe, IFocusGroup focuses) {
		Component timeText = Component.translatable("jei.slowcraft.progressive_crafting.time", recipe.seconds());

		builder.addText(timeText, TIME_TEXT_WIDTH, TIME_TEXT_HEIGHT)
				.setPosition(INPUT_SLOT_X, TIME_TEXT_Y)
				.setTextAlignment(HorizontalAlignment.CENTER)
				.setTextAlignment(VerticalAlignment.CENTER)
				.setColor(0xFF555555);
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, JEISlowtoolRecipe recipe, IFocusGroup focuses) {
		builder.addInputSlot(INPUT_SLOT_X, INPUT_SLOT_Y)
				.setStandardSlotBackground()
				.add(recipe.tool());

		int outputRows = (recipe.outputs().size() + OUTPUT_COLUMNS - 1) / OUTPUT_COLUMNS;
		int firstOutputY = INPUT_SLOT_Y - ((outputRows - 1) * OUTPUT_SLOT_SPACING / 2);
		int firstOutputX = recipe.outputs().size() == 1 ? SINGLE_OUTPUT_X : MULTI_OUTPUT_X;

		for (int i = 0; i < recipe.outputs().size(); i++) {
			int column = i % OUTPUT_COLUMNS;
			int row = i / OUTPUT_COLUMNS;
			int x = firstOutputX + (column * OUTPUT_SLOT_SPACING);
			int y = firstOutputY + (row * OUTPUT_SLOT_SPACING);

			builder.addOutputSlot(x, y)
					.setOutputSlotBackground()
					.add(recipe.outputs().get(i));
		}
	}

	@Override
	public Identifier getIdentifier(JEISlowtoolRecipe recipe) {
		return recipe.id();
	}
}
