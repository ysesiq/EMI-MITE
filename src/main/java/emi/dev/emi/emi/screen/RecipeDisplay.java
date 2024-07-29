package emi.dev.emi.emi.screen;

import com.google.common.collect.Lists;
import emi.dev.emi.emi.EmiPort;
import emi.dev.emi.emi.EmiUtil;
import emi.dev.emi.emi.api.recipe.EmiRecipe;
import emi.dev.emi.emi.api.widget.RecipeFillButtonWidget;
import emi.dev.emi.emi.api.widget.TextWidget;
import emi.dev.emi.emi.config.EmiConfig;
import emi.dev.emi.emi.registry.EmiRecipeFiller;
import emi.dev.emi.emi.widget.RecipeDefaultButtonWidget;
import emi.dev.emi.emi.widget.RecipeScreenshotButtonWidget;
import emi.dev.emi.emi.widget.RecipeTreeButtonWidget;
import emi.shims.java.net.minecraft.text.Text;
import emi.shims.java.net.minecraft.util.Formatting;

import java.util.List;
import java.util.stream.Collectors;

public class RecipeDisplay {
	public static final int DISPLAY_PADDING = 8;
	public final EmiRecipe recipe;
	private final int width, height;
	private List<ButtonType> rightButtons = Lists.newArrayList();
	private List<ButtonType> leftButtons = Lists.newArrayList();
	private int leftWidth = 0, rightWidth = 0;
	private int rows = 0;
	public Throwable exception;
	
	public RecipeDisplay(EmiRecipe recipe) {
		this.recipe = recipe;
		width = recipe.getDisplayWidth();
		height = recipe.getDisplayHeight();
		if (EmiRecipeFiller.isSupported(recipe) && EmiConfig.recipeFillButton) {
			rightButtons.add(ButtonType.FILL);
		}
		if (recipe.supportsRecipeTree()) {
			if (EmiConfig.recipeTreeButton) {
				rightButtons.add(ButtonType.TREE);
			}
			if (EmiConfig.recipeDefaultButton) {
				rightButtons.add(ButtonType.DEFAULT);
			}
		}
		if (EmiConfig.recipeScreenshotButton) {
			leftButtons.add(ButtonType.SCREENSHOT);
		}
		rows = Math.max(1, (height + DISPLAY_PADDING + 2) / 14);
		leftWidth = Math.max(0, (leftButtons.size() + rows - 1) / rows * 14 - 1);
		rightWidth = Math.max(0, (rightButtons.size() + rows - 1) / rows * 14 - 1);
	}
	
	// Error display
	public RecipeDisplay(Throwable exception) {
		this.recipe = null;
		this.width = 128;
		this.height = 64;
		this.exception = exception;
	}
	
	public WidgetGroup getWidgets(int x, int y, int availableWidth, int availableHeight) {
		int wx = x + (availableWidth - width) / 2;
		wx = Math.max(x + getLeftWidth(), Math.min(x + availableWidth - width - getRightWidth(), wx));
		int wy = y;
		int wWidth = width;
		int wHeight = Math.min(availableHeight, height);
		WidgetGroup widgets = new WidgetGroup(recipe, wx, wy, wWidth, wHeight);
		if (recipe != null) {
			try {
				recipe.addWidgets(widgets);
				addButtons(widgets, leftButtons, 0 - 4 - 13, -14);
				addButtons(widgets, rightButtons, width + 5, 14);
			}
			catch (Throwable t) {
				widgets = new WidgetGroup(recipe, wx, wy, wWidth, wHeight);
				widgets.add(new TextWidget(EmiPort.ordered(EmiPort.translatable("emi.error.recipe.render")), wWidth / 2, wHeight / 2 - 5,
						Formatting.RED.getColorValue(), true).horizontalAlign(TextWidget.Alignment.CENTER));
				if (exception != null) {
					List<Text> text = EmiUtil.getStackTrace(exception).stream().map(s -> (Text) EmiPort.literal(s)).collect(Collectors.toList());
					widgets.addTooltipText(text, 0, 0, wWidth, wHeight);
				}
			}
		}
		else {
			widgets.add(new TextWidget(EmiPort.ordered(EmiPort.translatable("emi.error.recipe.initialize")), wWidth / 2, wHeight / 2 - 5,
					Formatting.RED.getColorValue(), true).horizontalAlign(TextWidget.Alignment.CENTER));
			if (exception != null) {
				List<Text> text = EmiUtil.getStackTrace(exception).stream().map(s -> (Text) EmiPort.literal(s)).collect(Collectors.toList());
				widgets.addTooltipText(text, 0, 0, wWidth, wHeight);
			}
		}
		return widgets;
	}
	
	private void addButtons(WidgetGroup widgets, List<ButtonType> types, int x, int xOff) {
		int space = Math.min(8, height + 8 - (Math.min(rows, types.size()) * 14 - 2));
		int bottom = height + DISPLAY_PADDING / 2 - 12 - space / 2;
		int size = types.size();
		while (size > 0) {
			int used = Math.min(rows, size);
			List<ButtonType> current = types.subList(size - used, size);
			int yOff = 0;
			for (ButtonType type : current) {
				int bx = x;
				int by = bottom - yOff;
				widgets.add(switch (type) {
					case FILL -> new RecipeFillButtonWidget(bx, by, recipe);
					case TREE -> new RecipeTreeButtonWidget(bx, by, recipe);
					case DEFAULT -> new RecipeDefaultButtonWidget(bx, by, recipe);
					case SCREENSHOT -> new RecipeScreenshotButtonWidget(bx, by, recipe);
				});
				yOff += 14;
			}
			size -= used;
			x += xOff;
		}
	}
	
	public int getLeftWidth() {
		return leftWidth;
	}
	
	public int getRightWidth() {
		return rightWidth;
	}
	
	public int getWidth() {
		return leftWidth + rightWidth + width;
	}
	
	public int getHeight() {
		return height;
	}
	
	private static enum ButtonType {
		FILL, TREE, DEFAULT, SCREENSHOT
	}
}
