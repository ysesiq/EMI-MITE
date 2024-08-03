package emi.dev.emi.emi.handler;

import net.minecraft.ContainerWorkbench;
import com.google.common.collect.Lists;
import emi.dev.emi.emi.api.recipe.EmiRecipe;
import emi.dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import emi.dev.emi.emi.api.recipe.handler.StandardRecipeHandler;
import net.minecraft.IRecipe;
import net.minecraft.MITEContainerCrafting;
import net.minecraft.Slot;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CraftingRecipeHandler implements StandardRecipeHandler<ContainerWorkbench> {
	
	@Override
	public List<Slot> getInputSources(ContainerWorkbench handler) {
		List<Slot> list = Lists.newArrayList();
		for (int i = 1; i < 10; i++) {
			list.add(handler.getSlot(i));
		}
		int invStart = 10;
		for (int i = invStart; i < invStart + 36; i++) {
			list.add(handler.getSlot(i));
		}
		return list;
	}
	
	@Override
	public List<Slot> getCraftingSlots(ContainerWorkbench handler) {
		List<Slot> list = Lists.newArrayList();
		for (int i = 1; i < 10; i++) {
			list.add(handler.getSlot(i));
		}
		return list;
	}
	
	@Override
	public @Nullable Slot getOutputSlot(ContainerWorkbench handler) {
		return handler.getSlot(0);
	}
	
	@Override
	public boolean supportsRecipe(EmiRecipe recipe) {
		return recipe.getCategory() == VanillaEmiRecipeCategories.CRAFTING && recipe.supportsRecipeTree();
	}
}
