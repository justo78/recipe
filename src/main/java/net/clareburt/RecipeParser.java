package net.clareburt;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import net.clareburt.model.Recipe;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Justin Clareburt
 * @since 23/02/14
 */
public class RecipeParser {

	public Collection<Recipe> getRecipesFromJson(String recipeJson) throws JsonSyntaxException {
		final Collection<Recipe> recipes = new Gson().fromJson(recipeJson, new TypeToken<Collection<Recipe>>() {}.getType());
		if (recipes == null) return new ArrayList<Recipe>();
		return recipes;
	}

}
