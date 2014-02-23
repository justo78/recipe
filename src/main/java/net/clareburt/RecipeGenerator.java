package net.clareburt;

import net.clareburt.model.Ingredient;
import net.clareburt.model.Recipe;

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
					// This ingredient was not found. Immediately stop checking other recipe ingredients.
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
		Recipe bestRecipe = new Recipe();
		recipeLoop: for (String recipeName : recipes.keySet()) {
			final Collection<Ingredient> newRecipeIngredients = recipes.get(recipeName);
			if (bestRecipe.getName() == null) {
				updateBestRecipe(bestRecipe, recipeName, newRecipeIngredients);
				continue;
			}

			// Copy the list of ingredients
			final Collection<Ingredient> newIngredients = new HashSet<Ingredient>(newRecipeIngredients);
			final Collection<Ingredient> bestIngredients = new HashSet<Ingredient>(bestRecipe.getIngredients());
			while (true) {
				final Ingredient newIngredient = findEarliestExpiringIngredient(newIngredients);
				final Ingredient bestIngredient = findEarliestExpiringIngredient(bestIngredients);

				// Compare ingredients to current best recipe
				if (newIngredient == null) {
					// New recipe has no more ingredients. Favour the current best recipe.
					continue recipeLoop;
				}
				if (bestIngredient == null) {
					// New recipe has more ingredients. Favour recipe with more ingredients.
					updateBestRecipe(bestRecipe, recipeName, newRecipeIngredients);
					continue recipeLoop;
				}
				int compare = newIngredient.getUseBy().compareTo(bestIngredient.getUseBy());
				if (compare > 0) {
					// Best recipe is still the best. Check next recipe.
					continue recipeLoop;
				} else if (compare < 0) {
					// Found earlier expiring item. Update best recipe.
					updateBestRecipe(bestRecipe, recipeName, newRecipeIngredients);
					continue recipeLoop;
				} else {
					// Remove both items from the lists and compare next earliest items
					newIngredients.remove(newIngredient);
					bestIngredients.remove(bestIngredient);
				}
			}
		}
		return bestRecipe.getName();
	}

	private Ingredient findEarliestExpiringIngredient(Collection<Ingredient> ingredients) {
		if (ingredients == null) return null;
		Ingredient earliestExpiringIngredient = null;
		for (Ingredient ingredient : ingredients) {
			if (earliestExpiringIngredient == null) {
				earliestExpiringIngredient = ingredient;
			} else if (ingredient.getUseBy().before(earliestExpiringIngredient.getUseBy())) {
				earliestExpiringIngredient = ingredient;
			}
		}
		return earliestExpiringIngredient;
	}

	private void updateBestRecipe(Recipe bestRecipe, String recipeName, Collection<Ingredient> newIngredients) {
		bestRecipe.setName(recipeName);
		bestRecipe.setIngredients(newIngredients);
	}

}
