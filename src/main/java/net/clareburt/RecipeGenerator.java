package net.clareburt;

import net.clareburt.model.Ingredient;
import net.clareburt.model.Recipe;
import net.clareburt.util.DateUtil;

import java.util.*;

/**
 * @author Justin Clareburt
 * @since 23/02/14
 */
public class RecipeGenerator {

	private static final String DEFAULT_RECIPE = "Order Takeout";

	/**
	 * Given a collection of items in the fridge and a collection of recipes, suggest a recipe for which the fridge
	 * contains the ingredients (not passed the useBy date) suggesting the recipe that used the ingredients that are
	 * going to expire soonest.
	 * @param fridgeItems Collection of Ingredients
	 * @param recipes Collection of Recipes
	 * @return String name of the recipe to use
	 */
	public String generateRecipe(Collection<Ingredient> fridgeItems, Collection<Recipe> recipes) {
		if (fridgeItems == null || fridgeItems.isEmpty() || recipes == null || recipes.isEmpty()) {
			return DEFAULT_RECIPE;
		}
		final Date currentDate = DateUtil.getCurrentDate();
		// Loop through recipes checking if ingredients exist (in date)
		final Map<String, Collection<Ingredient>> validRecipes = new HashMap<String, Collection<Ingredient>>();
		recipeLoop: for (Recipe recipe : recipes) {
			final Collection<Ingredient> ingredients = new ArrayList<Ingredient>();
			for (Ingredient recipeIngredient : recipe.getIngredients()) {
				// Look for ingredient in fridge
				final Ingredient ingredient = findItemWithClosestUseByDate(fridgeItems, recipeIngredient, currentDate);
				if (ingredient == null) {
					// This ingredient was not found. Immediately stop checking the ingredients in this recipe.
					continue recipeLoop;
				}
				ingredients.add(ingredient);
			}
			validRecipes.put(recipe.getName(), ingredients);
		}
		return getBestRecipe(validRecipes);
	}

	private Ingredient findItemWithClosestUseByDate(Collection<Ingredient> fridgeItems, Ingredient recipeIngredient, Date currentDate) {
		Ingredient ingredient = null;
		Date closestDate = null;
		for (Ingredient fridgeItem : fridgeItems) {
			if (fridgeItem.getItem().equals(recipeIngredient.getItem())) {
				// Check useBy date and amount
				if (fridgeItem.getAmount() < recipeIngredient.getAmount() ||
						fridgeItem.getUseBy().before(currentDate)) {
					continue;
				}
				if (closestDate == null || closestDate.after(fridgeItem.getUseBy())) {
					closestDate = fridgeItem.getUseBy();
					ingredient = fridgeItem;
				}
			}
		}
		return ingredient;
	}

	private String getBestRecipe(Map<String, Collection<Ingredient>> recipes) {
		if (recipes.isEmpty()) {
			return DEFAULT_RECIPE;
		}
		final Recipe bestRecipe = new Recipe();
		for (String recipeName : recipes.keySet()) {
			final Collection<Ingredient> newRecipeIngredients = recipes.get(recipeName);
			// The first recipe will be used as the best recipe
			if (bestRecipe.getName() == null) {
				updateBestRecipe(bestRecipe, recipeName, newRecipeIngredients);
				continue;
			}
			// Update the best recipe if the new recipe has better ingredients
			if (hasBetterIngredients(newRecipeIngredients, bestRecipe.getIngredients())) {
				updateBestRecipe(bestRecipe, recipeName, newRecipeIngredients);
			}
		}
		return bestRecipe.getName();
	}

	/**
	 * Returns whether the new recipe ingredients will expire sooner than the best recipe ingredients
	 * or if all dates are the same returns whether the new recipe has more ingredients.
	 */
	private boolean hasBetterIngredients(Collection<Ingredient> newRecipeIngredients, Collection<Ingredient> bestRecipeIngredients) {
		// Copy the list of ingredients
		final Collection<Ingredient> newIngredients = new HashSet<Ingredient>(newRecipeIngredients);
		final Collection<Ingredient> bestIngredients = new HashSet<Ingredient>(bestRecipeIngredients);
		// Compare the earliest expiring ingredient of each recipe, removing each ingredient from the collection after
		// each comparison until eventually a difference is found or one recipe has more ingredients that the other.
		while (true) {
			final Ingredient newIngredient = findEarliestExpiringItem(newIngredients);
			final Ingredient bestIngredient = findEarliestExpiringItem(bestIngredients);

			// Favour the recipe with the most ingredients
			if (newIngredient == null) {
				return false;
			}
			if (bestIngredient == null) {
				return true;
			}

			int compare = newIngredient.getUseBy().compareTo(bestIngredient.getUseBy());
			if (compare < 0) {
				// Found earlier expiring item. Update best recipe.
				return true;
			}
			if (compare > 0) {
				// Best recipe is still the best.
				return false;
			}
			// Remove both items from the lists and compare next earliest expiring items
			newIngredients.remove(newIngredient);
			bestIngredients.remove(bestIngredient);
		}
	}

	private Ingredient findEarliestExpiringItem(Collection<Ingredient> ingredients) {
		if (ingredients == null) return null;
		Ingredient earliestExpiringItem = null;
		for (Ingredient ingredient : ingredients) {
			if (earliestExpiringItem == null || ingredient.getUseBy().before(earliestExpiringItem.getUseBy())) {
				earliestExpiringItem = ingredient;
			}
		}
		return earliestExpiringItem;
	}

	private void updateBestRecipe(Recipe bestRecipe, String recipeName, Collection<Ingredient> newIngredients) {
		bestRecipe.setName(recipeName);
		bestRecipe.setIngredients(newIngredients);
	}

}
