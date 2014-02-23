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

	public String generateRecipe(Collection<Ingredient> fridgeItems, Collection<Recipe> recipes) {
		if (fridgeItems == null || fridgeItems.isEmpty() || recipes == null || recipes.isEmpty()) {
			return DEFAULT_RECIPE;
		}
		final Date currentDate = getCurrentDate();
		// Loop through recipes checking if ingredients exist (in date)
		final Map<Recipe, Date> recipeDates = new HashMap<Recipe, Date>();
		for (Recipe recipe : recipes) {
			recipeDates.put(recipe, null);
			final Collection<Ingredient> ingredients = recipe.getIngredients();
			for (Ingredient recipeIngredient : ingredients) {
				// Look for ingredient in fridge
				final Ingredient ingredient = findItemWithClosestUseByDate(fridgeItems, recipeIngredient, currentDate);
				if (ingredient == null) {
					// This ingredient was not found.
					// Remove the recipe and immediately stop checking other recipe ingredients.
					recipeDates.remove(recipe);
					break;
				}
				updateRecipeUseByDate(recipeDates, recipe, ingredient.getUseBy());
			}
		}
		return getBestRecipe(recipeDates);
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

	private void updateRecipeUseByDate(Map<Recipe, Date> recipeDates, Recipe recipe, Date useBy) {
		final Date recipeUseByDate = recipeDates.get(recipe);
		if (recipeUseByDate == null || recipeUseByDate.after(useBy)) {
			recipeDates.put(recipe, useBy);
		}
	}

	private String getBestRecipe(Map<Recipe, Date> recipeDates) {
		String bestRecipeName = DEFAULT_RECIPE;
		Date closestDate = null;
		final Set<Recipe> recipes = recipeDates.keySet();
		for (Recipe recipe : recipes) {
			final Date recipeDate = recipeDates.get(recipe);
			if (closestDate == null || closestDate.after(recipeDate)) {
				closestDate = recipeDate;
				bestRecipeName = recipe.getName();
			}
		}
		return bestRecipeName;
	}

	private Date getCurrentDate() {
		return DateUtil.getCurrentDate();
	}
}
