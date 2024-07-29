package emi.dev.emi.emi.handler;

import com.google.common.collect.Lists;
import emi.dev.emi.emi.EmiPort;
import emi.dev.emi.emi.api.recipe.EmiCraftingRecipe;
import emi.dev.emi.emi.api.recipe.EmiRecipe;
import emi.dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import emi.dev.emi.emi.api.recipe.handler.EmiCraftContext;
import emi.dev.emi.emi.api.recipe.handler.StandardRecipeHandler;
import emi.mitemod.emi.api.EMIInventoryCrafting;
import emi.shims.java.net.minecraft.client.gui.tooltip.TooltipComponent;
import emi.shims.java.net.minecraft.text.Text;
import net.minecraft.Container;
import net.minecraft.ContainerPlayer;
import net.minecraft.ContainerWorkbench;
import net.minecraft.Slot;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class InventoryRecipeHandler implements StandardRecipeHandler<ContainerPlayer> {
	public static final Text TOO_SMALL = EmiPort.translatable("emi.too_small");
	
	@Override
	public List<Slot> getInputSources(ContainerPlayer handler) {
		List<Slot> list = Lists.newArrayList();
		for (int i = 1; i < 5; i++) {
			list.add(handler.getSlot(i));
		}
		int invStart = 9;
		for (int i = invStart; i < invStart + 36; i++) {
			list.add(handler.getSlot(i));
		}
		return list;
	}
	
	@Override
	public List<Slot> getCraftingSlots(ContainerPlayer handler) {
		List<Slot> list = Lists.newArrayList();
		// This is like, bad, right? There has to be a better way to do this
		list.add(handler.getSlot(1));
		list.add(handler.getSlot(2));
		list.add(null);
		list.add(handler.getSlot(3));
		list.add(handler.getSlot(4));
		list.add(null);
		list.add(null);
		list.add(null);
		list.add(null);
		return list;
	}
	
	@Override
	public List<Slot> getCraftingSlots(EmiRecipe recipe, ContainerPlayer handler) {
		if (recipe instanceof EmiCraftingRecipe craf && craf.shapeless) {
			List<Slot> list = Lists.newArrayList();
			list.add(handler.getSlot(1));
			list.add(handler.getSlot(2));
			list.add(handler.getSlot(3));
			list.add(handler.getSlot(4));
			return list;
		}
		return getCraftingSlots(handler);
	}
	
	@Override
	public @Nullable Slot getOutputSlot(ContainerPlayer handler) {
		return (Slot) handler.inventorySlots.get(0);
	}
	
	@Override
	public boolean supportsRecipe(EmiRecipe recipe) {
		if (recipe.getCategory() == VanillaEmiRecipeCategories.CRAFTING && recipe.supportsRecipeTree()) {
			if (recipe instanceof EmiCraftingRecipe crafting) {
				return crafting.canFit(2, 2);
			}
			return true;
		}
		return false;
	}
	
	@Override
	public boolean canCraft(EmiRecipe recipe, EmiCraftContext<ContainerPlayer> context) {
		Container sh = context.getScreenHandler();
		return canFit(sh, recipe) && StandardRecipeHandler.super.canCraft(recipe, context);
	}
	
	private boolean canFit(Container sh, EmiRecipe recipe) {
		int w = 0;
		int h = 0;
		EmiCraftingRecipe ecr = null;
		if (sh instanceof ContainerWorkbench arsh) {
			w = ((EMIInventoryCrafting)arsh.craft_matrix).getInventoryWidth();
			h = arsh.craft_matrix.getSizeInventory() / ((EMIInventoryCrafting)arsh.craft_matrix).getInventoryWidth();
		}
		if (sh instanceof ContainerPlayer arsh) {
			w = ((EMIInventoryCrafting)arsh.craft_matrix).getInventoryWidth();
			h = arsh.craft_matrix.getSizeInventory() / ((EMIInventoryCrafting)arsh.craft_matrix).getInventoryWidth();
		}
		if (recipe instanceof EmiCraftingRecipe crafting) {
			ecr = crafting;
		}
		if (ecr != null) {
			return ecr.canFit(w, h);
		}
		return false;
	}
	
	@Override
	public List<TooltipComponent> getTooltip(EmiRecipe recipe, EmiCraftContext<ContainerPlayer> context) {
		if (!canCraft(recipe, context)) {
			Container sh = context.getScreenHandler();
			if (!canFit(sh, recipe)) {
				return List.of(TooltipComponent.of(EmiPort.ordered(TOO_SMALL)));
			}
		}
		
		return StandardRecipeHandler.super.getTooltip(recipe, context);
	}
}
