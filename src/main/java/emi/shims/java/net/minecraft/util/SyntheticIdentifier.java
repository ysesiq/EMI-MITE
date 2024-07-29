package emi.shims.java.net.minecraft.util;

import emi.dev.emi.emi.Prototype;
import emi.dev.emi.emi.api.recipe.EmiCraftingRecipe;
import emi.dev.emi.emi.api.stack.EmiIngredient;
import emi.dev.emi.emi.api.stack.EmiStack;
import emi.dev.emi.emi.recipe.btw.EmiFoodRecipe;
import emi.mitemod.emi.api.EMIShapedRecipes;
import emi.shims.java.net.minecraft.nbt.StringNbtReader;
import net.minecraft.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SyntheticIdentifier extends ResourceLocation {
	
	public SyntheticIdentifier(Object o) {
		super(generateId(o));
	}
	
	public SyntheticIdentifier(Object o, String tail) {
		super(generateId(o) + tail);
	}
	
	private static String generateId(Object o) {
		if (o == null) {
			return "null:null";
		}
		else if (o instanceof ShapedRecipes sr) {
			return "shaped:/" + ((EMIShapedRecipes)sr).getRecipeWidth() + "x" + ((EMIShapedRecipes)sr).getRecipeHeight() + "/" + describeFlat(((EMIShapedRecipes)sr).getRecipeItems()) + "/" +
					describe(sr.getRecipeOutput());
		}
		else if (o instanceof ShapelessRecipes sr) {
			return "shapeless:/" + describeFlat(((EMIShapedRecipes)sr).getRecipeItems()) + "/" + describe(sr.getRecipeOutput());
		}
		else if (o instanceof EmiCraftingRecipe cr) {
			return "crafting:/" + describeFlat(cr.getInputs()) + "/" + describe(cr.getOutputs());
		}
		else if (o instanceof EmiFoodRecipe fr) {
			return "food:/" + describe(fr.getFoodItem());
		}
		return "unknown:/" + describe(o);
	}
	
	public static String describeFlat(List<?> li) {
		return describeFlat(li.stream());
	}
	
	public static String describeFlat(Object[] items) {
		return describeFlat(Arrays.stream(items));
	}
	
	public static String describeFlat(Stream<?> stream) {
		return stream.map(SyntheticIdentifier::describe).collect(Collectors.joining("/"));
	}
	
	public static String describe(Object o) {
		if (o == null) {
			return "null";
		}
		else if (o instanceof EmiStack es) {
			return describe(es.getItemStack());
		}
		else if (o instanceof EmiIngredient ei) {
			return ei.getEmiStacks().stream().map(SyntheticIdentifier::describe).collect(Collectors.joining("/", "[", "]"));
		}
		else if (o instanceof ItemStack is) {
			return is.itemID + "." + is.getItemDamage() + (is.hasTagCompound() ? StringNbtReader.encode(is.getTagCompound()) : "");
		}
		else if (o instanceof Block) {
			return describe(new ItemStack((Block) o));
		}
		else if (o instanceof String) {
			return (String) o;
		}
		else if (o instanceof List<?> l) {
			return l.stream().map(SyntheticIdentifier::describe).collect(Collectors.joining("/", "[", "]"));
		}
		else if (o instanceof Object[]) {
			Object[] arr = (Object[]) o;
			return Arrays.stream(arr).map(SyntheticIdentifier::describe).collect(Collectors.joining("/", "[", "]"));
		}
		else if (o instanceof Prototype p) {
			return p.item() == null ? "0.0" : p.item().itemID + "." + p.meta();
		}
		else {
			return o.getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(o));
		}
	}
}
