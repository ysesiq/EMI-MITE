package emi.dev.emi.emi.recipe;

import emi.dev.emi.emi.EmiPort;
import emi.dev.emi.emi.api.recipe.EmiCraftingRecipe;
import emi.dev.emi.emi.api.stack.EmiStack;
import emi.dev.emi.emi.api.widget.WidgetHolder;
import emi.mitemod.emi.api.EMIShapelessRecipes;
import emi.shims.java.com.unascribed.retroemi.RetroEMI;
import emi.shims.java.net.minecraft.util.SyntheticIdentifier;
import net.minecraft.IRecipe;
import net.minecraft.ItemStack;
import net.minecraft.Material;
import net.minecraft.ShapelessRecipes;

import java.text.DecimalFormat;
import java.util.List;
import java.util.stream.Collectors;

public class EmiShapelessRecipe extends EmiCraftingRecipe {

	private final ShapelessRecipes shapeless_recipe;
	private final int crafting_difficulty;

	public EmiShapelessRecipe(EMIShapelessRecipes recipe, ShapelessRecipes shapelessRecipes, int craftingDifficulty) {
		super(((List<ItemStack>) recipe.getRecipeItems()).stream().map(RetroEMI::wildcardIngredient).collect(Collectors.toList()),
				EmiStack.of(EmiPort.getOutput((IRecipe) recipe)), new SyntheticIdentifier(recipe), recipe.getSecondaryOutput(null));
        crafting_difficulty = craftingDifficulty;
        EmiShapedRecipe.setRemainders(input, (IRecipe) recipe);
		this.shapeless_recipe = shapelessRecipes;
	}


	@Override
	public Material craftLevel() {
		return this.shapeless_recipe.getMaterialToCheckToolBenchHardnessAgainst();
	}

	@Override
	public boolean canFit(int width, int height) {
		return input.size() <= width * height;
	}

	@Override
	public void addWidgets(WidgetHolder widgets) {
		super.addWidgets(widgets);
		DecimalFormat decimalFormat = new DecimalFormat("0.00");
		float crafting_time = (float) ((Math.pow((crafting_difficulty - 100), 0.74) + 100) / 20);
		widgets.addText(EmiPort.translatable("emi.craft_difficult.items", String.format("%d", crafting_difficulty)), 55, 40, 0xFFFFFFFF, true);
		widgets.addText(EmiPort.translatable("emi.craft_time.items", String.format("%s", decimalFormat.format(crafting_time) ) ), 55, 2, 0xFFFFFFFF, true);
	}

}
