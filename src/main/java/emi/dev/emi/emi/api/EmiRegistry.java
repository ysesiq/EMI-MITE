package emi.dev.emi.emi.api;

import emi.dev.emi.emi.api.recipe.EmiRecipe;
import emi.dev.emi.emi.api.recipe.EmiRecipeCategory;
import emi.dev.emi.emi.api.stack.Comparison;
import emi.dev.emi.emi.api.stack.EmiIngredient;
import emi.dev.emi.emi.api.stack.EmiStack;
import emi.dev.emi.emi.api.stack.serializer.EmiIngredientSerializer;
import net.minecraft.Container;
import net.minecraft.CraftingManager;
import net.minecraft.GuiScreen;
import net.minecraft.ResourceLocation;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public interface EmiRegistry {
	
	/**
	 * @return The vanilla recipe manager, for iterating recipe types.
	 */
	CraftingManager getRecipeManager();
	
	/**
	 * Adds a recipe category.
	 * Recipes are organized based on recipe category.
	 */
	void addCategory(EmiRecipeCategory category);
	
	/**
	 * Adds a workstation to a recipe category.
	 */
	void addWorkstation(EmiRecipeCategory category, EmiIngredient workstation);
	
	/**
	 * Adds a recipe to EMI that can be viewed and associated with its components.
	 */
	void addRecipe(EmiRecipe recipe);
	
	/**
	 * Adds a predicate to run on all current and future recipes to prevent certain ones from being added.
	 */
	void removeRecipes(Predicate<EmiRecipe> predicate);
	
	/**
	 * Adds a predicate to run on all current and future recipes to prevent certain ones with the given identifier from being added.
	 */
	default void removeRecipes(ResourceLocation id) {
		removeRecipes(r -> id.equals(r.getId()));
	}
	
	/**
	 * Add recipes that are reliant on a majority of EMI metadata is populated.
	 * The passed consumer will be run after all EMI plugins have executed.
	 */
	void addDeferredRecipes(Consumer<Consumer<EmiRecipe>> consumer);
	
	/**
	 * Adds an EmiStack to the sidebar.
	 */
	void addEmiStack(EmiStack stack);
	
	/**
	 * Adds an EmiStack to the sidebar immediately following another.
	 * If the predicate never succeeds, the provided EmiStack will not be added.
	 */
	void addEmiStackAfter(EmiStack stack, Predicate<EmiStack> predicate);
	
	/**
	 * Adds an EmiStack to the sidebar immediately following another.
	 * If the predicate never succeeds, the provided EmiStack will not be added.
	 */
	default void addEmiStackAfter(EmiStack stack, EmiStack other) {
		addEmiStackAfter(stack, s -> s.equals(other));
	}
	
	/**
	 * Adds a predicate to run on all current and future EmiStacks to prevent certain ones from being added to the sidebar.
	 */
	void removeEmiStacks(Predicate<EmiStack> predicate);
	
	/**
	 * Adds a predicate to run on all current and future EmiStacks to prevent matching ones from being added to the sidebar.
	 */
	default void removeEmiStacks(EmiStack stack) {
		removeEmiStacks(s -> s.equals(stack));
	}
	
	/**
	 * Adds a serializer for a given type of ingredient.
	 * This will allow it to be favorited, among other things.
	 */
	<T extends EmiIngredient> void addIngredientSerializer(Class<T> clazz, EmiIngredientSerializer<T> serializer);
	
	/**
	 * Adds an EmiExclusionArea to screens of a given class.
	 * Exclusion areas can provide rectangles where EMI will not place EmiStacks.
	 */
	<T extends GuiScreen> void addExclusionArea(Class<T> clazz, EmiExclusionArea<T> area);
	
	/**
	 * Adds an EmiExclusionArea to every screen.
	 * Exclusion areas can provide rectangles where EMI will not place EmiStacks.
	 */
	void addGenericExclusionArea(EmiExclusionArea<GuiScreen> area);
	
	/**
	 * Adds an EmiDragDropHandler to screens of a given class.
	 * Drag drop handlers can consume events related to sidebar stacks being dragged and dropped.
	 */
	<T extends GuiScreen> void addDragDropHandler(Class<T> clazz, EmiDragDropHandler<T> handler);
	
	/**
	 * Adds an EmiDragDropHandler to every screen.
	 * Drag drop handlers can consume events related to sidebar stacks being dragged and dropped.
	 */
	void addGenericDragDropHandler(EmiDragDropHandler<GuiScreen> handler);
	
	/**
	 * Adds an EmiStackProvider to screens of a given class.
	 * Stack providers can inform EMI of EmiIngredients that are located on the screen.
	 */
	<T extends GuiScreen> void addStackProvider(Class<T> clazz, EmiStackProvider<T> provider);
	
	/**
	 * Adds an EmiStackProvider to every screen.
	 * Stack providers can inform EMI of EmiIngredients that are located on the screen.
	 */
	void addGenericStackProvider(EmiStackProvider<GuiScreen> provider);
	
	/**
	 * Adds a default compraison method for a stack key.
	 *
	 * @param key        A stack key such as an item or fluid.
	 * @param comparison A function to mutate the current comprison method.
	 */
	void setDefaultComparison(Object key, Function<Comparison, Comparison> comparison);
	
	/**
	 * Adds a default compraison method for a stack using its key.
	 *
	 * @param key        A stack key such as an item or fluid.
	 * @param comparison The desired comparison method.
	 */
	default void setDefaultComparison(Object key, Comparison comparison) {
		setDefaultComparison(key, old -> comparison);
	}
	
	/**
	 * Adds a default compraison method for a stack using its key.
	 *
	 * @param stack      A stack to derive a key from.
	 * @param comparison A function to mutate the current comprison method.
	 */
	default void setDefaultComparison(EmiStack stack, Function<Comparison, Comparison> comparison) {
		setDefaultComparison(stack.getKey(), comparison);
	}
	
	/**
	 * Adds a default compraison method for a stack using its key.
	 *
	 * @param stack      A stack to derive a key from.
	 * @param comparison The desired comparison method.
	 */
	default void setDefaultComparison(EmiStack stack, Comparison comparison) {
		setDefaultComparison(stack.getKey(), old -> comparison);
	}
	
	/**
	 * Adds a recipe handler to a specified type of screen handler.
	 * Recipe handlers are responsible for filling recipes automatically.
	 */
	<T extends Container> void addRecipeHandler(Class<T> type, emi.dev.emi.emi.api.recipe.handler.EmiRecipeHandler<T> handler);
}
