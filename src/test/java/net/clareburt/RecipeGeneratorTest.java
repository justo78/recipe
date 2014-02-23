package net.clareburt;

import net.clareburt.model.Ingredient;
import net.clareburt.model.Recipe;
import net.clareburt.model.Unit;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/**
 * @author Justin Clareburt
 * @since 23/02/14
 */
public class RecipeGeneratorTest {

	private RecipeGenerator recipeGenerator;

	@Before
	public void setup() {
		recipeGenerator = new RecipeGenerator();
	}

	@Test
	public void shouldReturnDefaultSuggestionForNullInputs() {
		final String result = recipeGenerator.generateRecipe(null, null);
		assertEquals("Order Takeout", result);
	}

	@Test
	public void shouldReturnDefaultSuggestionIfNoRecipes() {
		final String result = recipeGenerator.generateRecipe(createIngredients(), null);
		assertEquals("Order Takeout", result);
	}

	@Test
	public void shouldReturnDefaultSuggestionIfNoIngredients() {
		final String result = recipeGenerator.generateRecipe(null, createRecipes());
		assertEquals("Order Takeout", result);
	}

	@Test
	public void shouldReturnDefaultSuggestionIfNoMatchingIngredients() {
		final String result = recipeGenerator.generateRecipe(createEmptyIngredients(), createRecipes());
		assertEquals("Order Takeout", result);
	}

	@Test
	public void shouldIgnoreRecipesWithIngredientsOutOfDate() {
		// Note: Cheese is out of date so cheese toasty should not be returned
		final ArrayList<Ingredient> fridgeItems = new ArrayList<Ingredient>();
		fridgeItems.add(createIngredientWithDate("bread", 2, Unit.slices, "15/04/2014"));
		fridgeItems.add(createIngredientWithDate("cheese", 2, Unit.slices, "25/12/2013"));

		final ArrayList<Recipe> recipes = new ArrayList<Recipe>();
		recipes.add(createToastRecipe("toast"));
		recipes.add(createCheeseToastyRecipe("cheese toasty"));

		final String result = recipeGenerator.generateRecipe(fridgeItems, recipes);
		assertEquals("toast", result);
	}

	@Test
	public void shouldIgnoreRecipesWithNotEnoughIngredients() {
		final ArrayList<Ingredient> fridgeItems = new ArrayList<Ingredient>();
		fridgeItems.add(createIngredientWithDate("bread", 2, Unit.slices, "21/04/2014"));
		fridgeItems.add(createIngredientWithDate("cheese", 2, Unit.slices, "15/04/2014"));
		fridgeItems.add(createIngredientWithDate("crackers", 5, Unit.of, "17/04/2014"));

		final ArrayList<Recipe> recipes = new ArrayList<Recipe>();
		recipes.add(createToastRecipe("toast"));
		recipes.add(createCheeseToastyRecipe("cheese toasty"));
		recipes.add(createCheeseAndCrackersRecipe("cheese and crackers"));

		final String result = recipeGenerator.generateRecipe(fridgeItems, recipes);
		assertEquals("cheese toasty", result);
	}

	@Test
	public void shouldReturnRecipeWithClosestIngredientsUseByDate() {
		final ArrayList<Ingredient> fridgeItems = new ArrayList<Ingredient>();
		fridgeItems.add(createIngredientWithDate("bread", 2, Unit.slices, "21/04/2014"));
		fridgeItems.add(createIngredientWithDate("cheese", 2, Unit.slices, "25/04/2014"));
		fridgeItems.add(createIngredientWithDate("crackers", 10, Unit.of, "17/04/2014"));

		final ArrayList<Recipe> recipes = new ArrayList<Recipe>();
		recipes.add(createCheeseToastyRecipe("cheese toasty"));
		recipes.add(createCheeseAndCrackersRecipe("cheese and crackers"));

		final String result = recipeGenerator.generateRecipe(fridgeItems, recipes);
		assertEquals("cheese and crackers", result);
	}

	@Test
	public void shouldHandleIngredientsWithDifferentUseByDates() {
		final ArrayList<Ingredient> fridgeItems = new ArrayList<Ingredient>();
		fridgeItems.add(createIngredientWithDate("bread", 2, Unit.slices, "21/04/2014"));
		fridgeItems.add(createIngredientWithDate("cheese", 2, Unit.slices, "25/04/2014"));
		fridgeItems.add(createIngredientWithDate("crackers", 10, Unit.of, "17/04/2014"));
		fridgeItems.add(createIngredientWithDate("bread", 2, Unit.slices, "15/04/2014"));

		final ArrayList<Recipe> recipes = new ArrayList<Recipe>();
		recipes.add(createCheeseToastyRecipe("cheese toasty"));
		recipes.add(createCheeseAndCrackersRecipe("cheese and crackers"));

		final String result = recipeGenerator.generateRecipe(fridgeItems, recipes);
		assertEquals("cheese toasty", result);
	}

	@Test
	public void shouldCompareDatesOfAllIngredients() {
		final ArrayList<Ingredient> fridgeItems = new ArrayList<Ingredient>();
		fridgeItems.add(createIngredientWithDate("bread", 10, Unit.slices, "25/12/2014"));
		fridgeItems.add(createIngredientWithDate("cheese", 10, Unit.slices, "25/12/2014"));
		fridgeItems.add(createIngredientWithDate("butter", 250, Unit.grams, "25/12/2014"));
		fridgeItems.add(createIngredientWithDate("peanut butter", 250, Unit.grams, "26/12/2014"));
		fridgeItems.add(createIngredientWithDate("mixed salad", 150, Unit.grams, "27/12/2014"));

		final ArrayList<Recipe> recipes = new ArrayList<Recipe>();
		recipes.add(createSalasSandwichRecipe("salad sandwich"));
		recipes.add(createPeanutButterSandwichRecipe("peanut butter sandwich"));

		final String result = recipeGenerator.generateRecipe(fridgeItems, recipes);
		assertEquals("peanut butter sandwich", result);
	}

	//-------- Helper methods --------

	private ArrayList<Ingredient> createIngredients() {
		final ArrayList<Ingredient> ingredients = createEmptyIngredients();
		ingredients.add(createBreadIngredient());
		ingredients.add(createCheeseIngredient());
		return ingredients;
	}

	private ArrayList<Ingredient> createEmptyIngredients() {
		return new ArrayList<Ingredient>();
	}

	private ArrayList<Recipe> createRecipes() {
		final ArrayList<Recipe> recipes = new ArrayList<Recipe>();
		recipes.add(createToastRecipe("toast"));
		recipes.add(createCheeseToastyRecipe("cheese toasty"));
		recipes.add(createCheeseAndCrackersRecipe("cheese and crackers"));
		return recipes;
	}

	private Recipe createNewRecipe(String recipeName) {
		final Recipe recipe = new Recipe();
		recipe.setName(recipeName);
		return recipe;
	}

	private Recipe createToastRecipe(String recipeName) {
		final Recipe recipe = createNewRecipe(recipeName);
		recipe.setIngredients(Arrays.asList(createBreadIngredient()));
		return recipe;
	}

	private Recipe createCheeseToastyRecipe(String recipeName) {
		final Recipe recipe = createNewRecipe(recipeName);
		recipe.setIngredients(Arrays.asList(createBreadIngredient(), createCheeseIngredient()));
		return recipe;
	}

	private Recipe createCheeseAndCrackersRecipe(String recipeName) {
		final Recipe recipe = createNewRecipe(recipeName);
		recipe.setIngredients(Arrays.asList(createCheeseIngredient(), createCrackersIngredient()));
		return recipe;
	}

	private Recipe createPeanutButterSandwichRecipe(String recipeName) {
		final Recipe recipe = createNewRecipe(recipeName);
		recipe.setIngredients(Arrays.asList(createBreadIngredient(), createButterIngredient(), createPeanutButterIngredient()));
		return recipe;
	}

	private Recipe createSalasSandwichRecipe(String recipeName) {
		final Recipe recipe = createNewRecipe(recipeName);
		recipe.setIngredients(Arrays.asList(createBreadIngredient(), createButterIngredient(), createSaladIngredient()));
		return recipe;
	}

	private Ingredient createBreadIngredient() {
		return createIngredient("bread", 2, Unit.slices);
	}

	private Ingredient createButterIngredient() {
		return createIngredient("butter", 50, Unit.grams);
	}

	private Ingredient createPeanutButterIngredient() {
		return createIngredient("peanut butter", 50, Unit.grams);
	}

	private Ingredient createCheeseIngredient() {
		return createIngredient("cheese", 2, Unit.slices);
	}

	private Ingredient createCrackersIngredient() {
		return createIngredient("crackers", 10, Unit.of);
	}

	private Ingredient createSaladIngredient() {
		return createIngredient("mixed salad", 50, Unit.grams);
	}

	private Ingredient createIngredient(String item, int amount, Unit unit) {
		final Ingredient ingredient = new Ingredient();
		ingredient.setItem(item);
		ingredient.setAmount(amount);
		ingredient.setUnit(unit);
		return ingredient;
	}

	private Ingredient createIngredientWithDate(String item, int amount, Unit unit, String useByStr) {
		final Ingredient ingredient = createIngredient(item, amount, unit);
		ingredient.setUseBy(useByStr);
		return ingredient;
	}
}